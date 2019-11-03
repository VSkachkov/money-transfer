package revolut.app;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import revolut.app.errors.ResultResponse;
import revolut.entity.AccountDto;
import revolut.entity.MoneyTransferDto;
import revolut.entity.Transaction;
import revolut.service.UserAccountService;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import static revolut.app.Configuration.getUserService;
import static spark.Spark.*;



class Application {
    public static void main(String[] args) throws IOException {
        Configuration configuration = new Configuration();
        Gson gson = new Gson();
        UserAccountService service = getUserService();
        configSparkServer();

        path("/api", () -> {
            before((request, response) -> response.type("application/json"));
            path("/transactions", () -> {
                get("", (req, res) -> service.getAllTransactions());
                post("/create", (req, res) -> {
                    final MoneyTransferDto dto = gson.fromJson(req.body(), MoneyTransferDto.class);
                    Transaction transaction = service.createTransaction(dto);
                    return gson.toJson(transaction);
                });
            });
            path("/accounts", () -> {
               post("/create", (req, res) -> {
                   Type itemsListType = new TypeToken<List<AccountDto>>() {}.getType();
                   final List<AccountDto> accounts = gson.fromJson(req.body(), itemsListType);
                   return gson.toJson(ResultResponse.builder().success(service.createAccounts(accounts)).build());
               });
               get("", (req, res) -> {
                   return gson.toJson(service.getAccounts());
               });
            });
        });
    }

    private static void configSparkServer() {
        Gson gson = new Gson();
        before((request, response) -> response.type("application/json"));
        before((req, res) -> {
            String path = req.pathInfo();
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

