package transfer.model;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

@Builder
@Getter
public class TransactionDto {
    private UUID transactionId;
    private UUID senderId;
    private UUID receiverId;
    private BigDecimal amount;
    private Date time;
    private Status transactionStatus;
}
