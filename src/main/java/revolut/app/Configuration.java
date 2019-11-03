package revolut.app;

import revolut.app.errors.GlobalExceptionHandler;
import revolut.data.user.InMemoryAccountRepository;
import revolut.domain.account.AccountRepository;
import revolut.domain.account.TransactionRepository;
import revolut.domain.account.TransactionRepositoryImpl;
import revolut.domain.account.UserAccountService;
import com.fasterxml.jackson.databind.ObjectMapper;

class Configuration {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final AccountRepository USER_REPOSITORY = new InMemoryAccountRepository();
    private static final TransactionRepository TRANSACTION_REPOSITORY = new TransactionRepositoryImpl(); //TODO fix Impl
    private static final UserAccountService USER_SERVICE = new UserAccountService(USER_REPOSITORY, TRANSACTION_REPOSITORY);
    private static final GlobalExceptionHandler GLOBAL_ERROR_HANDLER = new GlobalExceptionHandler(OBJECT_MAPPER);

    static ObjectMapper getObjectMapper() {
        return OBJECT_MAPPER;
    }

    static UserAccountService getUserService() {
        return USER_SERVICE;
    }

    static AccountRepository getUserRepository() {
        return USER_REPOSITORY;
    }

    public static GlobalExceptionHandler getErrorHandler() {
        return GLOBAL_ERROR_HANDLER;
    }
}
