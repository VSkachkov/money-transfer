package revolut.data.user;

import revolut.domain.account.MoneyTransferDto;
import revolut.domain.account.UserAccountsRepository;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@NoArgsConstructor
public class InMemoryUserAccountsRepository implements UserAccountsRepository {

    public InMemoryUserAccountsRepository(Map<UUID, BigDecimal> initialAccount) {
        USERS_ACCOUNT_STORE.putAll(initialAccount);
    }

    private static final Map<UUID, BigDecimal> USERS_ACCOUNT_STORE = new ConcurrentHashMap() ;

    @Override
    public String createTransaction(MoneyTransferDto transferDto) {
        BigDecimal receiverAccount = USERS_ACCOUNT_STORE.getOrDefault(transferDto.getReceiverId(), BigDecimal.ZERO);
        BigDecimal senderAccount = USERS_ACCOUNT_STORE.getOrDefault(transferDto.getSenderId(), BigDecimal.ZERO);
        USERS_ACCOUNT_STORE.put(transferDto.getReceiverId(), receiverAccount.add(transferDto.getAmount()));
        USERS_ACCOUNT_STORE.put(transferDto.getSenderId(), senderAccount.subtract(transferDto.getAmount()));
        return "Result!";
    }

    public BigDecimal getAccountStatus(UUID client) {
        return USERS_ACCOUNT_STORE.get(client);
    }
}
