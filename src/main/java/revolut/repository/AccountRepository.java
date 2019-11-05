package revolut.repository;

import revolut.model.Account;
import revolut.model.AccountDto;
import revolut.model.MoneyTransferDto;
import revolut.model.Transaction;

import java.util.List;
import java.util.UUID;

public interface AccountRepository {

    Transaction transferBetweenAccounts(MoneyTransferDto transferDto);

    void create(AccountDto account);

    List<AccountDto> getAccounts();

    Account getById(UUID id);
}
