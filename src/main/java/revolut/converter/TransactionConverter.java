package revolut.converter;

import revolut.model.Transaction;
import revolut.model.TransactionDto;

import java.util.UUID;

public class TransactionConverter {

    public TransactionDto convert(final UUID id, final Transaction transaction) {
        return TransactionDto.builder()
                .transactionId(id)
                .receiverId(transaction.getReceiver().getId())
                .senderId(transaction.getSender().getId())
                .amount(transaction.getAmount())
                .time(transaction.getTime())
                .build();
    }
}
