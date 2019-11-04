package revolut.repository;

import revolut.model.Transaction;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

public interface TransactionRepository {
    void save(Transaction transaction);

    Set<Map.Entry<UUID, Transaction>> getAll();

    Transaction getById(UUID id);
}
