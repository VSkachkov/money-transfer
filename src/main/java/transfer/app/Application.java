package transfer.app;

import com.google.gson.Gson;
import org.eclipse.jetty.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import transfer.constants.Constants;
import transfer.constants.ErrorConstants;
import transfer.errors.ApplicationException;
import transfer.model.ResultResponse;
import transfer.model.AccountDto;
import transfer.model.MoneyTransferDto;
import transfer.model.TransactionDto;
import transfer.service.TransactionAccountService;

import java.util.UUID;

import static transfer.app.Configuration.getTransactionAccountService;
import static spark.Spark.*;



class Application {
    private static final Logger log = LoggerFactory.getLogger(Application.class);

    private static final Gson gson = new Gson();
    public static void main(final String[] args) {
        log.info("Initialization started");
        configSparkServer();
        configExceptions();
        configEndpoints();
    }

    /**
     * Configures endpoints of Spark Server
     */
    private static void configEndpoints() {
        log.info("Configuring endpoints");
        final TransactionAccountService service = getTransactionAccountService();
        path("/api", () -> {
            before((req, res) -> res.type(Constants.APPLICATION_JSON));
            get("/transactions", (req, res) -> gson.toJson(service.getAllTransactions()));
            get("/transactions/:id", (req, res) -> gson.toJson(service.getTransactionById(UUID.fromString(req.params(":id")))));
            post("/transactions/create", (req, res) -> {
                final MoneyTransferDto dto = gson.fromJson(req.body(), MoneyTransferDto.class);
                res.status(HttpStatus.CREATED_201);
                final TransactionDto transactionDto = service.createTransaction(dto);
                return gson.toJson(transactionDto);
            });

            get("/accounts", (req, res) -> gson.toJson(service.getAccounts()));
            post("/accounts/create", (req, res) -> {
                res.status(HttpStatus.CREATED_201);
                final AccountDto accountToCreate = gson.fromJson(req.body(), AccountDto.class);
                return gson.toJson(service.createAccount(accountToCreate));
            });
            get("/accounts/:id", (req, res) -> gson.toJson(service.getAccount(UUID.fromString(req.params(":id")))));
        });
    }

    /**
     * Configures Spark server always to response with JSON and ignore additional slash
     */
    private static void configSparkServer() {
        before((request, response) -> response.type(Constants.APPLICATION_JSON));
        before((req, res) -> {
            final String path = req.pathInfo();
            if (path.endsWith("/"))
                res.redirect(path.substring(0, path.length() - 1));
        });

    }

    /**
     * Configures Spark server with exceptions wrappers
     */
    private static void configExceptions() {
        exception(ApplicationException.class, (exception, request, response) -> {
            log.error("Application exception occured" + exception.getMessage());
            response.status(exception.getCode());
            response.body(gson.toJson(ResultResponse
                    .builder()
                    .success(false)
                    .message(exception.getMessage())
                    .build()));
        });
        //for any unexpected exception response with Http status 500
        exception(RuntimeException.class, (exception, request, response) -> {
            log.error("Runtime  exception occured: ", exception.getMessage());
            response.status(500);
            response.body(gson.toJson(ResultResponse
                    .builder()
                    .success(false)
                    .message(ErrorConstants.INTERNAL_SERVER_ERROR_MSG)
                    .build()));
        });
    }
}

