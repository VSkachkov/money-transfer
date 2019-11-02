package revolut.app;

import revolut.app.errors.GlobalExceptionHandler;
import revolut.data.user.InMemoryUserAccountsRepository;
import revolut.domain.account.UserAccountsRepository;
import revolut.domain.account.UserAccountService;
import com.fasterxml.jackson.databind.ObjectMapper;

class Configuration {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final UserAccountsRepository USER_REPOSITORY = new InMemoryUserAccountsRepository();
    private static final UserAccountService USER_SERVICE = new UserAccountService(USER_REPOSITORY);
    private static final GlobalExceptionHandler GLOBAL_ERROR_HANDLER = new GlobalExceptionHandler(OBJECT_MAPPER);

    static ObjectMapper getObjectMapper() {
        return OBJECT_MAPPER;
    }

    static UserAccountService getUserService() {
        return USER_SERVICE;
    }

    static UserAccountsRepository getUserRepository() {
        return USER_REPOSITORY;
    }

    public static GlobalExceptionHandler getErrorHandler() {
        return GLOBAL_ERROR_HANDLER;
    }
}
