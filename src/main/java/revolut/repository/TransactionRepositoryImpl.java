package revolut.repository;

import revolut.entity.Transaction;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class TransactionRepositoryImpl implements TransactionRepository {

    private Map<UUID, Transaction> transactionRepository = new ConcurrentHashMap();
    @Override
    public void save(Transaction transaction) {
        transactionRepository.put(UUID.randomUUID(), transaction);
    }
}
