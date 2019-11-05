package transfer.validator;

import org.eclipse.jetty.http.HttpStatus;
import transfer.constants.ErrorConstants;
import transfer.errors.ApplicationException;
import transfer.model.AccountDto;


public class AccountValidator {
    public void validateNewAccount(final AccountDto newAccount) {
        if (newAccount == null || newAccount.getId() == null || newAccount.getBalance() == null) {
            throw new ApplicationException(HttpStatus.BAD_REQUEST_400, ErrorConstants.INVALID_PARAMETERS);
        }
    }
}
