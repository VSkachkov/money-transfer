package transfer.model;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Money transfer DTO class
 */
@Getter
@Data
@Builder
public class MoneyTransferDto {
    private UUID transactionId;
    private UUID receiverId;
    private UUID senderId;
    private BigDecimal amount;
}
