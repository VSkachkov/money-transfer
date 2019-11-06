package transfer.app;

import transfer.repository.InMemoryAccountRepository;
import transfer.repository.AccountRepository;
import transfer.repository.TransactionRepository;
import transfer.repository.InMemoryTransactionRepository;
import transfer.service.TransactionAccountService;

/**
 * Main configuration class to avoid necessity of DI usage
 */
class Configuration {

    private static final AccountRepository ACCOUNT_REPOSITORY = new InMemoryAccountRepository();
    private static final TransactionRepository TRANSACTION_REPOSITORY = new InMemoryTransactionRepository();
    private static final TransactionAccountService USER_SERVICE = new TransactionAccountService(ACCOUNT_REPOSITORY,
            TRANSACTION_REPOSITORY);

    static TransactionAccountService getTransactionAccountService() {
        return USER_SERVICE;
    }
}
