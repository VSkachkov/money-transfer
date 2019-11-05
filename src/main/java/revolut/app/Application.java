package revolut.app;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.eclipse.jetty.http.HttpStatus;
import revolut.app.api.Constants;
import revolut.app.errors.ResultResponse;
import revolut.model.AccountDto;
import revolut.model.MoneyTransferDto;
import revolut.model.Transaction;
import revolut.service.TransactionAccountService;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
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
                final Transaction transaction = service.createTransaction(dto);
                return gson.toJson(transaction);
            });

            get("/accounts", (req, res) -> gson.toJson(service.getAccounts()));
            post("/accounts/create", (req, res) -> {
                final Type itemsListType = new TypeToken<List<AccountDto>>() {}.getType();
                res.status(HttpStatus.CREATED_201);
                final List<AccountDto> accounts = gson.fromJson(req.body(), itemsListType);
                return gson.toJson(ResultResponse.builder().success(service.createAccounts(accounts)).build());
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
        exception(Exception.class, (exception, request, response) -> {
            // Handle the exception here
            response.status(500);
            response.body(gson.toJson(ResultResponse.builder().success(false).message("Internal server error")));
        });
    }
}

