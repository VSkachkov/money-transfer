package revolut.app;

import com.google.gson.Gson;
import org.eclipse.jetty.http.HttpStatus;
import revolut.app.api.Constants;
import revolut.app.api.ErrorConstants;
import revolut.app.errors.AccountExistsException;
import revolut.app.errors.ApplicationException;
import revolut.model.ResultResponse;
import revolut.model.AccountDto;
import revolut.model.MoneyTransferDto;
import revolut.model.Transaction;
import revolut.model.TransactionDto;
import revolut.service.TransactionAccountService;

import java.util.UUID;

import static revolut.app.Configuration.getUserService;
import static spark.Spark.*;



class Application {
    public static void main(final String[] args) {
        final Configuration configuration = new Configuration();
        final Gson gson = new Gson();
        final TransactionAccountService service = getUserService();
        configSparkServer();

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
//                final Type itemsListType = new TypeToken<List<AccountDto>>() {}.getType();
                res.status(HttpStatus.CREATED_201);
                final AccountDto accountToCreate = gson.fromJson(req.body(), AccountDto.class);
                return gson.toJson(service.createAccount(accountToCreate));
            });
            get("/accounts/:id", (req, res) -> gson.toJson(service.getAccount(UUID.fromString(req.params(":id")))));
        });
    }

    private static void configSparkServer() {
        final Gson gson = new Gson();
        before((request, response) -> response.type(Constants.APPLICATION_JSON));
        before((req, res) -> {
            final String path = req.pathInfo();
            if (path.endsWith("/"))
                res.redirect(path.substring(0, path.length() - 1));
        });
        exception(ApplicationException.class, (exception, request, response) -> {
            response.status(exception.getCode());
            response.body(gson.toJson(ResultResponse
                    .builder()
                    .success(false)
                    .message(exception.getMessage())
                    .build()));
        });
        exception(RuntimeException.class, (exception, request, response) -> {
            // Handle the exception here
            response.status(500);
            response.body(gson.toJson(ResultResponse
                    .builder()
                    .success(false)
                    .message(ErrorConstants.INTERNAL_SERVER_ERROR_MSG)
                    .build()));
        });
    }
}

