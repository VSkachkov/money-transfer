package revolut.app;

import com.google.gson.Gson;
import revolut.app.errors.AccountNotFoundException;
import revolut.app.errors.ErrorDto;
import revolut.entity.MoneyTransferDto;
import revolut.entity.Transaction;
import revolut.service.UserAccountService;

import java.io.IOException;

import static revolut.app.Configuration.getUserService;
import static spark.Spark.*;

class Application {
    public static void main(String[] args) throws IOException {
        Configuration configuration = new Configuration();
        Gson gson = new Gson();
        UserAccountService service = getUserService();
        path("/api", () -> {

            path("/transactions", () -> {


                get("", (req, res) -> service.getAllTransactions());
                post("/create", (req, res) -> {
                    final String body = req.body();
                    res.header("Content-type", "Application/JSON");
                    final MoneyTransferDto dto = gson.fromJson(body, MoneyTransferDto.class);
                    Transaction transaction = service.createTransaction(dto);
                    return gson.toJson(transaction);
                });
            });

//            path("/accounts", () -> {
//               post("/create", (req, res) -> service.createAccount())
//            });
        });
        exception(AccountNotFoundException.class, (exception, request, response) -> {
            // Handle the exception here
            response.status(503);
            response.body(gson.toJson(ErrorDto.builder().errorMessage("Service unavailable")));
        });
    }
}

