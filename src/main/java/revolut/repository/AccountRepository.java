package revolut.repository;

import revolut.entity.AccountDto;
import revolut.entity.MoneyTransferDto;
import revolut.entity.Transaction;

import java.util.List;

public interface AccountRepository {

    Transaction transferBetweenAccounts(MoneyTransferDto transferDto);

    String getAll();

    void create(AccountDto account);

    List<AccountDto> getAccounts();
}
