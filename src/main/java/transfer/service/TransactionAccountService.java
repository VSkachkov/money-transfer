package transfer.service;

import lombok.AllArgsConstructor;
import transfer.converter.AccountConverter;
import transfer.model.*;
import transfer.converter.TransactionConverter;
import transfer.repository.AccountRepository;
import transfer.repository.TransactionRepository;
import transfer.validator.AccountValidator;
import transfer.validator.TransactionValidator;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@AllArgsConstructor
public class TransactionAccountService {

    private static final long WAIT_SEC = 1L;

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final TransactionConverter transactionConverter = new TransactionConverter();
    private final AccountConverter accountConverter = new AccountConverter();
    private final TransactionValidator transactionValidator = new TransactionValidator();
    private final AccountValidator accountValidator = new AccountValidator();

//    public TransactionDto createTransaction(final MoneyTransferDto transferDto) {
//        final Transaction transaction = accountRepository.transferBetweenAccounts(transferDto);
//        final UUID transactionId = UUID.randomUUID();
//        transactionRepository.save(transactionId, transaction);
//        return transactionConverter.convert(transactionId, transaction);
//    }

    public TransactionDto createTransaction(final MoneyTransferDto transferDto) {
        transactionValidator.validate(transferDto);
        final Account receiver = accountRepository.getById(transferDto.getReceiverId());
        final Account sender = accountRepository.getById(transferDto.getSenderId());
        final Transaction transaction = Transaction
                .builder()
                .transactionId(transferDto.getTransactionId())
                .sender(sender)
                .receiver(receiver)
                .transactionStatus(Status.IN_PROCESS)
                .amount(transferDto.getAmount())
                .build();
        transactionRepository.save(transferDto.getTransactionId(), transaction);
        try {
            if (transaction.getLock().tryLock(WAIT_SEC, TimeUnit.SECONDS)) {
                try {
                    if (sender.getLock().tryLock(WAIT_SEC, TimeUnit.SECONDS)) {
                        try {
                            if (receiver.getLock().tryLock(WAIT_SEC, TimeUnit.SECONDS)) {
                                if (sender.getBalance().compareTo(transferDto.getAmount()) >= 0) {
                                    sender.substract(transferDto.getAmount());
                                    receiver.add(transferDto.getAmount());
                                    transaction.setTransactionStatus(Status.COMPLETED);
                                } else {
                                    transaction.setTransactionStatus(Status.FAILED_SENDER_HAS_NOT_ENOUGH_MONEY);
                                }
                            }
                        } finally {
                            receiver.getLock().unlock();
                        }
                    } else {
                        transaction.setTransactionStatus(Status.FAILED_TECHNICAL_ERROR);
                    }
                } finally {
                    sender.getLock().unlock();
                }
            }
        } catch (final InterruptedException e) {
            transaction.setTransactionStatus(Status.FAILED_TECHNICAL_ERROR);
        } finally {
            transaction.getLock().unlock();
        }
        return transactionConverter.convert(transferDto.getTransactionId(), transaction);
    }

    public List<TransactionDto> getAllTransactions() {
        return transactionRepository.getAll()
                .stream()
                .map(entry -> transactionConverter.convert(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

    public AccountDto createAccount(final AccountDto newAccount) {
        accountValidator.validateNewAccount(newAccount);
        final Account created = accountRepository.create(newAccount);
        return accountConverter.convert(newAccount.getId(), created);

    }

    public List<AccountDto> getAccounts() {
        return accountRepository.getAccounts()
                .stream()
                .map(entry -> accountConverter.convert(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

    public TransactionDto getTransactionById(final UUID id) {
        return transactionConverter.convert(id, transactionRepository.getById(id));
    }

    public AccountDto getAccount(final UUID id) {
        return accountConverter.convert(id, accountRepository.getById(id));
    }
}
