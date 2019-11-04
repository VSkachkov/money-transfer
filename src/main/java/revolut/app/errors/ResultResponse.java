package revolut.app.errors;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Value;

@Getter
@Builder
@AllArgsConstructor
public class ResultResponse {

    int code;
    boolean success;
    String message;
}
