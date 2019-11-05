package revolut.app.errors;

import lombok.Getter;

@Getter
public class ApplicationException extends RuntimeException {

    private final int code;

    ApplicationException(final int code, final String message) {
        super(message);
        this.code = code;
    }
}