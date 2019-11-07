package transfer.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger log = LoggerFactory.getLogger(TransactionAccountService.class);
    public static final long WAIT_DELAY = 1L;
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
    public synchronized TransactionDto createTransaction(final MoneyTransferDto transferDto) {
        log.info("Creating transaction " + transferDto);
        transactionValidator.validateNewTransferRequest(transferDto);
        final Account receiver = accountRepository.getById(transferDto.getReceiverId());
        final Account sender = accountRepository.getById(transferDto.getSenderId());
        final Transaction transaction = Transaction.builder().transactionId(transferDto.getTransactionId())
                .sender(sender).receiver(receiver).transactionStatus(Status.IN_PROCESS).time(new Date())
                .amount(transferDto.getAmount()).build();
        final Transaction created = transactionRepository.save(transferDto.getTransactionId(), transaction);
        transactionValidator.validateTransactionExistence(created);

        try {
            log.debug("trying locking transaction");
            if (transaction.getLock().tryLock(WAIT_DELAY, TimeUnit.SECONDS)) {
                log.debug("transaction locked");
                try {
                    if (sender.getLock().tryLock(WAIT_DELAY, TimeUnit.SECONDS)) {
                        log.debug("Sender locked");
                        try {
                            if (receiver.getLock().tryLock(WAIT_DELAY, TimeUnit.SECONDS)) {
                                log.debug("receiver locked");
                                if (sender.getBalance().compareTo(transferDto.getAmount()) >= 0) {
                                    sender.substract(transferDto.getAmount());
                                    receiver.add(transferDto.getAmount());
//                                    accountRepository.computeIfPresent(transferDto.getSenderId(),
//                                            (key, val) -> val.substract(transferDto.getAmount()));
//                                    accountRepository.computeIfPresent(transferDto.getReceiverId(),
//                                            (key, val) -> val.add(transferDto.getAmount()));
                                    log.debug("Transaction completed " + transferDto.getTransactionId());
                                    transaction.setTransactionStatus(Status.COMPLETED);
                                } else {
                                    transaction.setTransactionStatus(Status.FAILED_SENDER_HAS_NOT_ENOUGH_MONEY);
                                }
                            } else {
                                log.debug("Receiver locking failed");
                            }
                        } finally {
                            log.debug("Unlocking receiver");
                            receiver.getLock().unlock();
                        }
                    } else {
                        log.debug("Sender locking failed");
                        transaction.setTransactionStatus(Status.FAILED_TECHNICAL_ERROR);
                    }
                } finally {
                    log.debug("Unlocking sender");
                    sender.getLock().unlock();
                }
            }
            else {
                log.debug("Transaction locking failed");
            }
        } catch (final InterruptedException e) {
            log.error("InterruptedException occured" + e.getLocalizedMessage());
            transaction.setTransactionStatus(Status.FAILED_TECHNICAL_ERROR);
        } finally {
            log.debug("Unlocking transaction");
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
        log.info("Getting all transactions");
        return transactionRepository.getAll().stream()
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
        log.info("New account creation " + newAccount);
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
        log.info("Getting all accounts");
        return accountRepository.getAccounts().stream()
                .map(entry -> accountConverter.convert(entry.getKey(), entry.getValue())).collect(Collectors.toList());
    }

    /**
     * Returns transaction by ID
     *
     * @param id transaction ID
     * @return transaction DTO
     */
    public TransactionDto getTransactionById(final UUID id) {
        log.info("Getting transaction by ID " + id);
        return transactionConverter.convert(id, transactionRepository.getById(id));
    }

    /**
     * Returns account DTO by account ID
     *
     * @param id account ID
     * @return account DTO
     */
    public AccountDto getAccount(final UUID id) {
        log.info("Getting account by ID " + id);
        return accountConverter.convert(id, accountRepository.getById(id));
    }
}
