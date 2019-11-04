package revolut.repository;

import revolut.app.errors.AccountNotFoundException;
import lombok.NoArgsConstructor;
import revolut.model.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@NoArgsConstructor
public class InMemoryAccountRepository implements AccountRepository {

    private static final long WAIT_SEC = 1L;

    public InMemoryAccountRepository(Map<UUID, Account> initialAccount) {
        ACCOUNT_STORE.putAll(initialAccount);
    }


    private static final Map<UUID, Account> ACCOUNT_STORE = new ConcurrentHashMap<>();

    @Override
    public Transaction transferBetweenAccounts(MoneyTransferDto transferDto) {
        Status status;

        if (ACCOUNT_STORE.get(transferDto.getReceiverId()) == null) {
            throw new AccountNotFoundException(500, "Not found account");
        }
        Account receiverAccount = ACCOUNT_STORE.get(transferDto.getReceiverId());
        Account senderAccount = ACCOUNT_STORE.get(transferDto.getSenderId());
        try {
            status = Status.IN_PROCESS;
            if (senderAccount.getLock().tryLock(WAIT_SEC, TimeUnit.SECONDS)) {
                try {
                    if (receiverAccount.getLock().tryLock(WAIT_SEC, TimeUnit.SECONDS)) {
                        if (senderAccount.getBalance().compareTo(transferDto.getAmount()) >= 0) {
                            ACCOUNT_STORE.computeIfPresent(transferDto.getSenderId(), (key, val) -> val.substract(transferDto.getAmount()));
                            ACCOUNT_STORE.computeIfPresent(transferDto.getReceiverId(), (key, val) -> val.add(transferDto.getAmount()));
                            status = Status.COMPLETED;
                        } else {
                            status = Status.FAILED_SENDER_HAS_NOT_ENOUGH_MONEY;
                        }
                    }
                } finally {
                    receiverAccount.getLock().unlock();
                }
            } else {
                //error waiting lock
            }
        } catch (InterruptedException e) {
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
    public String getAll() {
        return ACCOUNT_STORE.entrySet().toString(); //TODO fix
    }

    @Override
    public void create(AccountDto newAccount) {
        ACCOUNT_STORE.putIfAbsent(newAccount.getId(),
                Account.builder().balance(newAccount.getBalance() != null ? newAccount.getBalance() : BigDecimal.ZERO).build());
    }

    @Override
    public List<AccountDto> getAccounts() {
        return ACCOUNT_STORE.entrySet().stream()
                .map(account -> AccountDto.builder()
                        .id(account.getKey())
                        .balance(account.getValue().getBalance())
                        .build())
                .collect(Collectors.toList());
    }

    public synchronized Account getAccountStatus(UUID client) {
        return ACCOUNT_STORE.get(client);
    }

    public synchronized BigDecimal getSum() {
        return ACCOUNT_STORE.values()
                .stream()
                .map(Account::getBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
