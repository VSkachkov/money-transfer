package revolut.app.errors;

public class AccountNotFoundException extends ApplicationException {
    public AccountNotFoundException(final int code, final String message) {
        super(code, message);
    }
}
