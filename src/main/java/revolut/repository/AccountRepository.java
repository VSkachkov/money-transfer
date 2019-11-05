package revolut.repository;

import revolut.model.Account;
import revolut.model.AccountDto;
import revolut.model.MoneyTransferDto;
import revolut.model.Transaction;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

public interface AccountRepository {

    Transaction transferBetweenAccounts(MoneyTransferDto transferDto);

    Account create(AccountDto account);

    Set<Map.Entry<UUID, Account>> getAccounts();

    Account getById(UUID id);
}
