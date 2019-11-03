package revolut.repository;

import revolut.entity.MoneyTransferDto;
import revolut.entity.Transaction;

public interface AccountRepository {

    Transaction transferBetweenAccounts(MoneyTransferDto transferDto);

    String getAll();
}
