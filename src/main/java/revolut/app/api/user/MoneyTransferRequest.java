package revolut.app.api.user;

import lombok.Value;

@Value
class MoneyTransferRequest {

    String login;
    String password;
}
