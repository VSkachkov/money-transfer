package transfer.repository;

import org.eclipse.jetty.http.HttpStatus;
import transfer.constants.ErrorConstants;
import lombok.NoArgsConstructor;
import transfer.errors.ApplicationException;
import transfer.model.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;

@NoArgsConstructor
public class InMemoryAccountRepository implements AccountRepository {

    private static final long WAIT_SEC = 1L;

    public InMemoryAccountRepository(final Map<UUID, Account> initialAccount) {
        ACCOUNT_STORE.putAll(initialAccount);
    }


    private static final Map<UUID, Account> ACCOUNT_STORE = new ConcurrentHashMap<>();

    @Override
    public Account create(final AccountDto newAccountDto) {
        final Account newAccount = Account.builder().id(newAccountDto.getId()).balance(
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

    public Account computeIfPresent(final UUID key, BiFunction<? super UUID, ? super Account, ? extends Account> remappingFunction) {
        return ACCOUNT_STORE.computeIfPresent(key, remappingFunction);
    }
}
