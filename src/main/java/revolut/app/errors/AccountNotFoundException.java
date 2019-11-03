package revolut.app.errors;

public class AccountNotFoundException extends ApplicationException {
    public AccountNotFoundException(int code, String message) {
        super(code, message);
    }
}
