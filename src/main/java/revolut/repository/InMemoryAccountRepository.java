package revolut.repository;

import org.eclipse.jetty.http.HttpStatus;
import revolut.app.api.ErrorConstants;
import revolut.app.errors.AccountNotFoundException;
import lombok.NoArgsConstructor;
import revolut.app.errors.ApplicationException;
import revolut.model.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@NoArgsConstructor
public class InMemoryAccountRepository implements AccountRepository {

    private static final long WAIT_SEC = 1L;

    public InMemoryAccountRepository(final Map<UUID, Account> initialAccount) {
        ACCOUNT_STORE.putAll(initialAccount);
    }


    private static final Map<UUID, Account> ACCOUNT_STORE = new ConcurrentHashMap<>();

    @Override
    public Transaction transferBetweenAccounts(final MoneyTransferDto transferDto) {
        Status status;

        if (ACCOUNT_STORE.get(transferDto.getReceiverId()) == null) {
            throw new AccountNotFoundException(500, "Not found account");
        }
        final Account receiverAccount = ACCOUNT_STORE.get(transferDto.getReceiverId());
        final Account senderAccount = ACCOUNT_STORE.get(transferDto.getSenderId());
        try {
            status = Status.IN_PROCESS;
            if (senderAccount.getLock().tryLock(WAIT_SEC, TimeUnit.SECONDS)) {
                try {
                    if (receiverAccount.getLock().tryLock(WAIT_SEC, TimeUnit.SECONDS)) {
                        if (senderAccount.getBalance().compareTo(transferDto.getAmount()) >= 0) {
                            ACCOUNT_STORE.computeIfPresent(transferDto.getSenderId(),
                                    (key, val) -> val.substract(transferDto.getAmount()));
                            ACCOUNT_STORE.computeIfPresent(transferDto.getReceiverId(),
                                    (key, val) -> val.add(transferDto.getAmount()));
                            status = Status.COMPLETED;
                        } else {
                            status = Status.FAILED_SENDER_HAS_NOT_ENOUGH_MONEY;
                        }
                    }
                } finally {
                    receiverAccount.getLock().unlock();
                }
            } else {
                //error waiting lock // TODO
            }
        } catch (final InterruptedException e) {
            status = Status.FAILED_TECHNICAL_ERROR;
        } finally {
            senderAccount.getLock().unlock();
        }
        return Transaction.builder()
                .amount(transferDto.getAmount())
                .receiver(receiverAccount)
                .sender(senderAccount)
                .transactionStatus(status)
                .time(new Date())
                .build();
    }

    @Override
    public Account create(final AccountDto newAccountDto) {
        final Account newAccount = Account.builder().balance(
                newAccountDto.getBalance() != null ? newAccountDto.getBalance() : BigDecimal.ZERO)
                .build();
        if (ACCOUNT_STORE.putIfAbsent(newAccountDto.getId(), newAccount) != null) {
            throw new ApplicationException(HttpStatus.BAD_REQUEST_400, ErrorConstants.ACCOUNT_EXISTS_MSG);
        }
        return newAccount;
    }

    @Override
    public Set<Map.Entry<UUID, Account>> getAccounts() {
        return ACCOUNT_STORE.entrySet();
    }

    @Override
    public Account getById(final UUID id) {
        return ACCOUNT_STORE.get(id);
    }

    public synchronized Account getAccountStatus(final UUID client) {
        return ACCOUNT_STORE.get(client);
    }

    public synchronized BigDecimal getSum() {
        return ACCOUNT_STORE.values()
                .stream()
                .map(Account::getBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
