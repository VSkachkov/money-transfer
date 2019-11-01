package revolut.domain.account;

public interface UserAccountsRepository {

    String createTransaction(MoneyTransferDto transferDto);
}
