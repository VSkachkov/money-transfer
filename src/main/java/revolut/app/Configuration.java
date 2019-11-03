package revolut.app;

//import revolut.app.errors.GlobalExceptionHandler;
import revolut.repository.InMemoryAccountRepository;
import revolut.repository.AccountRepository;
import revolut.repository.TransactionRepository;
import revolut.repository.TransactionRepositoryImpl;
import revolut.service.UserAccountService;
import com.fasterxml.jackson.databind.ObjectMapper;

class Configuration {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final AccountRepository USER_REPOSITORY = new InMemoryAccountRepository();
    private static final TransactionRepository TRANSACTION_REPOSITORY = new TransactionRepositoryImpl(); //TODO fix Impl
    private static final UserAccountService USER_SERVICE = new UserAccountService(USER_REPOSITORY, TRANSACTION_REPOSITORY);

    static ObjectMapper getObjectMapper() {
        return OBJECT_MAPPER;
    }

    static UserAccountService getUserService() {
        return USER_SERVICE;
    }

    static AccountRepository getUserRepository() {
        return USER_REPOSITORY;
    }

}
