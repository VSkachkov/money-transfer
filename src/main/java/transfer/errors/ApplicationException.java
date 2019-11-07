package transfer.errors;

import lombok.Getter;

/**
 * Exception class that wraps all the application exceptions to JSON with message
 */
@Getter
public class ApplicationException extends RuntimeException {

    private final int code;

    public ApplicationException(final int code, final String message) {
        super(message);
        this.code = code;
    }
}