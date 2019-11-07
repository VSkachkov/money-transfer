package transfer.repository;

import transfer.model.Transaction;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Transaction repository interface
 */
public interface TransactionRepository {
    Transaction save(UUID id, Transaction transaction);

    Set<Map.Entry<UUID, Transaction>> getAll();

    Transaction getById(UUID id);
}
