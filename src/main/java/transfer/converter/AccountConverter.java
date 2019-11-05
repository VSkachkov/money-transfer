package transfer.converter;

import transfer.model.Account;
import transfer.model.AccountDto;

import java.util.UUID;

public class AccountConverter {
    public AccountDto convert(final UUID id, final Account account) {
        return AccountDto.builder()
                .id(id)
                .balance(account.getBalance())
                .currency(account.getCurrency())
                .build();
    }
}
