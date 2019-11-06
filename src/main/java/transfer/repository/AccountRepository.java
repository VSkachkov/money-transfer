package transfer.repository;

import transfer.model.Account;
import transfer.model.AccountDto;
import transfer.model.MoneyTransferDto;
import transfer.model.Transaction;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Account repository interface
 */
public interface AccountRepository {

    Account create(AccountDto account);

    Set<Map.Entry<UUID, Account>> getAccounts();

    Account getById(UUID id);
}
