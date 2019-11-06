package transfer.converter;

import org.eclipse.jetty.http.HttpStatus;
import transfer.constants.ErrorConstants;
import transfer.errors.ApplicationException;
import transfer.model.Account;
import transfer.model.AccountDto;

import java.util.UUID;

/**
 * Class to convert entities to DTO
 */
public class AccountConverter {
    /**
     * Converts entity to DTO
     * @param id account ID
     * @param account account entity
     * @return account DTO
     */
    public AccountDto convert(final UUID id, final Account account) {
        if (id == null || account == null) {
            throw new ApplicationException(HttpStatus.NOT_FOUND_404, ErrorConstants.REQUESTED_DATA_NOT_FOUND);
        }
        return AccountDto.builder()
                .id(id)
                .balance(account.getBalance())
                .currency(account.getCurrency())
                .build();
    }
}
