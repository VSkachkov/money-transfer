package revolut.app;

import com.google.gson.Gson;
import revolut.domain.account.MoneyTransferDto;
import revolut.domain.account.UserAccountService;

import java.io.IOException;

import static revolut.app.Configuration.getUserService;
import static spark.Spark.post;

class Application {
    public static void main(String[] args) throws IOException {
        Configuration configuration = new Configuration();
        Gson gson = new Gson();
        post("/hello", (req, res) -> {
            final String body = req.body();
            res.header("Content-type", "Application/JSON");
            final MoneyTransferDto dto = gson.fromJson(body, MoneyTransferDto.class);
            getUserService().createTransaction(dto);
            return gson.toJson(dto);
        });
    }
}
