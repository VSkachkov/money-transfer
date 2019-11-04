package revolut.app;

//import revolut.app.errors.GlobalExceptionHandler;
import revolut.repository.InMemoryAccountRepository;
import revolut.repository.AccountRepository;
import revolut.repository.TransactionRepository;
import revolut.repository.InMemoryTransactionRepository;
import revolut.service.TransactionAccountService;

class Configuration {

//    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final AccountRepository USER_REPOSITORY = new InMemoryAccountRepository();
    private static final TransactionRepository TRANSACTION_REPOSITORY = new InMemoryTransactionRepository(); //TODO fix Impl
    private static final TransactionAccountService USER_SERVICE = new TransactionAccountService(USER_REPOSITORY, TRANSACTION_REPOSITORY);

//    static ObjectMapper getObjectMapper() {
//        return OBJECT_MAPPER;
//    }

    static TransactionAccountService getUserService() {
        return USER_SERVICE;
    }

    static AccountRepository getUserRepository() {
        return USER_REPOSITORY;
    }

}
