package revolut.repository;

import revolut.model.AccountDto;
import revolut.model.MoneyTransferDto;
import revolut.model.Transaction;

import java.util.List;

public interface AccountRepository {

    Transaction transferBetweenAccounts(MoneyTransferDto transferDto);

    String getAll();

    void create(AccountDto account);

    List<AccountDto> getAccounts();
}
