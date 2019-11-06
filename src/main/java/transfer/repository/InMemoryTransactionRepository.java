package transfer.repository;

import transfer.model.Transaction;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryTransactionRepository implements TransactionRepository {

    private Map<UUID, Transaction> transactionRepository = new ConcurrentHashMap();

    @Override
    public Transaction save(final UUID id, final Transaction transaction) {
        return transactionRepository.putIfAbsent(id, transaction);
    }

    @Override
    public Set<Map.Entry<UUID, Transaction>> getAll() {
        return transactionRepository.entrySet();
    }

    @Override
    public Transaction getById(final UUID id) {
        return transactionRepository.get(id);
    }
}
