package revolut;

import lombok.*;

@Value
@Data
@Builder
class RegistrationRequest {

    String login;
    String password;
}
