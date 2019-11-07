package transfer.validator;

import org.eclipse.jetty.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import transfer.constants.ErrorConstants;
import transfer.errors.ApplicationException;
import transfer.model.AccountDto;

/**
 * Validator for account DTO
 */
public class AccountValidator {
    private static final Logger log = LoggerFactory.getLogger(AccountValidator.class);

    /**
     * Validates presence of all the mandatory fields in DTO for new account creation
     *
     * @param newAccount
     */
    public void validateNewAccount(final AccountDto newAccount) {
        if (newAccount == null || newAccount.getId() == null || newAccount.getBalance() == null) {
            log.error("Account DTO validation failed " + newAccount);
            throw new ApplicationException(HttpStatus.BAD_REQUEST_400, ErrorConstants.INVALID_PARAMETERS);
        }
    }
}
