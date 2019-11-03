package revolut.repository;

import revolut.entity.Transaction;

public interface TransactionRepository {
    void save(Transaction transaction);
}
