package revolut.app;

import revolut.app.api.user.RegistrationHandler;
import com.sun.net.httpserver.BasicAuthenticator;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpServer;
import revolut.domain.account.MoneyTransferDto;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;

import static revolut.app.Configuration.*;
import static revolut.app.api.ApiUtils.splitQuery;
import static spark.Spark.get;
import static spark.Spark.post;

class Application {

    public static void main(String[] args) throws IOException {

        post("/hello", (req, res) -> {
            final String body = req.body();
            MoneyTransferDto dto = getObjectMapper().convertValue(body, MoneyTransferDto.class);
            return dto.toString();});
    }
}
