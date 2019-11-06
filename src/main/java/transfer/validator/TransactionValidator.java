package transfer.validator;

import org.eclipse.jetty.http.HttpStatus;
import transfer.constants.ErrorConstants;
import transfer.errors.ApplicationException;
import transfer.model.MoneyTransferDto;
import transfer.model.Transaction;

import java.math.BigDecimal;

public class TransactionValidator {
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

    public void validateTransactionExistence(final Transaction created) {
        if (created != null) {
            throw new ApplicationException(HttpStatus.BAD_REQUEST_400, ErrorConstants.TRANSACTION_EXISTS);
        }
    }
}
