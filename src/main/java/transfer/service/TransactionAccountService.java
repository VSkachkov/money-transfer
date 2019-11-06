package transfer.service;

import lombok.AllArgsConstructor;
import transfer.converter.AccountConverter;
import transfer.model.*;
import transfer.converter.TransactionConverter;
import transfer.repository.AccountRepository;
import transfer.repository.TransactionRepository;
import transfer.validator.AccountValidator;
import transfer.validator.TransactionValidator;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Main service for accounts and transactions
 */
@AllArgsConstructor
public class TransactionAccountService {

    private static final long WAIT_SEC = 1L;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final TransactionConverter transactionConverter = new TransactionConverter();
    private final AccountConverter accountConverter = new AccountConverter();
    private final TransactionValidator transactionValidator = new TransactionValidator();
    private final AccountValidator accountValidator = new AccountValidator();

    /**
     * Creates transaction from one account to another
     *
     * @param transferDto transfer DTO
     * @return transaction result DTO
     */
    public TransactionDto createTransaction(final MoneyTransferDto transferDto) {
        transactionValidator.validateNewTransferRequest(transferDto);
        final Account receiver = accountRepository.getById(transferDto.getReceiverId());
        final Account sender = accountRepository.getById(transferDto.getSenderId());
        final Transaction transaction = Transaction.builder().transactionId(transferDto.getTransactionId())
                .sender(sender).receiver(receiver).transactionStatus(Status.IN_PROCESS).time(new Date())
                .amount(transferDto.getAmount()).build();
        final Transaction created = transactionRepository.save(transferDto.getTransactionId(), transaction);
        transactionValidator.validateTransactionExistence(created);
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

    /**
     * Returns list of all transactions
     *
     * @return list of transactions
     */
    public List<TransactionDto> getAllTransactions() {
        return transactionRepository.getAll()
                .stream()
                .map(entry -> transactionConverter.convert(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

    /**
     * Creates new account
     *
     * @param newAccount new account DTO
     * @return created account DTO
     */
    public AccountDto createAccount(final AccountDto newAccount) {
        accountValidator.validateNewAccount(newAccount);
        final Account created = accountRepository.create(newAccount);
        return accountConverter.convert(newAccount.getId(), created);
    }

    /**
     * Returns list of all accounts
     *
     * @return list of all accounts DTOs
     */
    public List<AccountDto> getAccounts() {
        return accountRepository.getAccounts()
                .stream()
                .map(entry -> accountConverter.convert(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

    /**
     * Returns transaction by ID
     *
     * @param id transaction ID
     * @return transaction DTO
     */
    public TransactionDto getTransactionById(final UUID id) {
        return transactionConverter.convert(id, transactionRepository.getById(id));
    }

    /**
     * Returns account DTO by account ID
     *
     * @param id account ID
     * @return account DTO
     */
    public AccountDto getAccount(final UUID id) {
        return accountConverter.convert(id, accountRepository.getById(id));
    }
}
