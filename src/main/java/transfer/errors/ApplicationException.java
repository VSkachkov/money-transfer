package transfer.errors;

import lombok.Getter;

@Getter
public class ApplicationException extends RuntimeException {

    private final int code;

    public ApplicationException(final int code, final String message) {
        super(message);
        this.code = code;
    }
}