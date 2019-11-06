package transfer.converter;

import transfer.model.Account;
import transfer.model.AccountDto;

import java.util.UUID;

/**
 * Class to convert entities to DTO
 */
public class AccountConverter {
    /**
     * Converts entity to DTO
     * @param id account ID
     * @param account account entity
     * @return account DTO
     */
    public AccountDto convert(final UUID id, final Account account) {
        if (id == null || account == null) {
            return null;
        }
        return AccountDto.builder()
                .id(id)
                .balance(account.getBalance())
                .currency(account.getCurrency())
                .build();
    }
}
