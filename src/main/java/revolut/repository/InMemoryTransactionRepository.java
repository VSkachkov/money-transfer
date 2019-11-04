package revolut.repository;

import revolut.model.Transaction;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryTransactionRepository implements TransactionRepository {

    private Map<UUID, Transaction> transactionRepository = new ConcurrentHashMap();
    @Override
    public void save(Transaction transaction) {
        transactionRepository.put(UUID.randomUUID(), transaction);
    }

    @Override
    public Set<Map.Entry<UUID, Transaction>> getAll() {
        return transactionRepository.entrySet();
    }

    @Override
    public Transaction getById(UUID id) {
        return transactionRepository.get(id);
    }
}
