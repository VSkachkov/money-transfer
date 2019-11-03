package revolut.domain.account;

public interface TransactionRepository {
    void save(Transaction transaction);
}
