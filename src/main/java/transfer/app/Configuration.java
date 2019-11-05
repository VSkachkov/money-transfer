package transfer.app;

import transfer.repository.InMemoryAccountRepository;
import transfer.repository.AccountRepository;
import transfer.repository.TransactionRepository;
import transfer.repository.InMemoryTransactionRepository;
import transfer.service.TransactionAccountService;

class Configuration {

    private static final AccountRepository USER_REPOSITORY = new InMemoryAccountRepository();
    private static final TransactionRepository TRANSACTION_REPOSITORY = new InMemoryTransactionRepository(); //TODO fix Impl
    private static final TransactionAccountService USER_SERVICE = new TransactionAccountService(USER_REPOSITORY, TRANSACTION_REPOSITORY);

    static TransactionAccountService getUserService() {
        return USER_SERVICE;
    }

    static AccountRepository getUserRepository() {
        return USER_REPOSITORY;
    }

}
