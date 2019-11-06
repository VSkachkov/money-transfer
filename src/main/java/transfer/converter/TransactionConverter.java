package transfer.converter;

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
            return null;
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
