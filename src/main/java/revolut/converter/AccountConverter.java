package revolut.converter;

import revolut.model.Account;
import revolut.model.AccountDto;

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
