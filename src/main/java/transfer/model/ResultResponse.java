package transfer.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ResultResponse {

    int code;
    boolean success;
    String message;
}
