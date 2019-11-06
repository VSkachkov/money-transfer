package transfer.validator;

import org.eclipse.jetty.http.HttpStatus;
import transfer.constants.ErrorConstants;
import transfer.errors.ApplicationException;
import transfer.model.MoneyTransferDto;
import transfer.model.Transaction;

import java.math.BigDecimal;

/**
 * Validator for transaction DTO
 */
public class TransactionValidator {

    /**
     * Validates transaction dto for
     * 1) the presence of all mandatory fields
     * 2) sender and receiver are not the same
     * 3) the balance of sender is positive
     *
     * @param dto transaction DTO
     */
    public void validateNewTransferRequest(final MoneyTransferDto dto) {
        if (dto == null || dto.getTransactionId() == null || dto
                .getReceiverId() == null || dto.getSenderId() == null) {
            throw new ApplicationException(HttpStatus.BAD_REQUEST_400, ErrorConstants.INVALID_PARAMETERS);
        }
        if (dto.getSenderId().equals(dto.getReceiverId())) {
            throw new ApplicationException(HttpStatus.BAD_REQUEST_400, ErrorConstants.MONEY_TRANSFER_NOT_ALLOWED);
        }
        if (dto.getAmount() == null || dto.getAmount().compareTo(BigDecimal.ZERO)<=0) {
            throw new ApplicationException(HttpStatus.BAD_REQUEST_400, ErrorConstants.INVALID_SUM_AMOUNT);
        }
    }

    /**
     * Validates that transaction was not created earlier
     *
     * @param created if transaction was created before, throws exception
     */
    public void validateTransactionExistence(final Transaction created) {
        if (created != null) {
            throw new ApplicationException(HttpStatus.BAD_REQUEST_400, ErrorConstants.TRANSACTION_EXISTS);
        }
    }
}
