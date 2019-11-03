package revolut.app.api.user;

import revolut.app.api.Constants;
import revolut.app.api.Handler;
import revolut.app.api.ResponseEntity;
import revolut.app.api.StatusCode;
import revolut.app.errors.ApplicationExceptions;
import revolut.app.errors.GlobalExceptionHandler;
import revolut.domain.account.MoneyTransferDto;
import revolut.domain.account.Transaction;
import revolut.domain.account.UserAccountService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class RegistrationHandler extends Handler {

    private final UserAccountService userAccountService;

    public RegistrationHandler(UserAccountService userAccountService, ObjectMapper objectMapper,
                               GlobalExceptionHandler exceptionHandler) {
        super(objectMapper, exceptionHandler);
        this.userAccountService = userAccountService;
    }

    @Override
    protected void execute(HttpExchange exchange) throws IOException {
        byte[] response;
        if ("POST".equals(exchange.getRequestMethod())) {
            ResponseEntity e = doPost(exchange.getRequestBody());
            exchange.getResponseHeaders().putAll(e.getHeaders());
            exchange.sendResponseHeaders(e.getStatusCode().getCode(), 0);
            response = super.writeResponse(e.getBody());
        } else {
            throw ApplicationExceptions.methodNotAllowed(
                "Method " + exchange.getRequestMethod() + " is not allowed for " + exchange.getRequestURI()).get();
        }

        OutputStream os = exchange.getResponseBody();
        os.write(response);
        os.close();
    }

    private ResponseEntity<RegistrationResponse> doPost(InputStream is) {
        MoneyTransferDto transferDto = super.readRequest(is, MoneyTransferDto.class);

        Transaction userId = userAccountService.createTransaction(transferDto);

        RegistrationResponse response = new RegistrationResponse(userId.toString());

        return new ResponseEntity<>(response,
            getHeaders(Constants.CONTENT_TYPE, Constants.APPLICATION_JSON), StatusCode.OK);
    }
}
