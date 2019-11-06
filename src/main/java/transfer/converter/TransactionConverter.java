package transfer.converter;

import org.eclipse.jetty.http.HttpStatus;
import transfer.constants.ErrorConstants;
import transfer.errors.ApplicationException;
import transfer.model.Transaction;
import transfer.model.TransactionDto;

import java.util.UUID;

/**
 * Class to convert transaction entities to DTO
 */
public class TransactionConverter {

    /**
     * Converts entity to DTO
     *
     * @param id transaction ID
     * @param transaction transaction entity
     * @return transaction DTO
     */
    public TransactionDto convert(final UUID id, final Transaction transaction) {
        if (transaction == null || id == null) {
            throw new ApplicationException(HttpStatus.NOT_FOUND_404, ErrorConstants.REQUESTED_DATA_NOT_FOUND);
        }
        return TransactionDto.builder()
                .transactionId(id)
                .receiverId(transaction.getReceiver().getId())
                .senderId(transaction.getSender().getId())
                .amount(transaction.getAmount())
                .time(transaction.getTime())
                .transactionStatus(transaction.getTransactionStatus())
                .build();
    }
}
