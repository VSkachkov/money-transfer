package transfer.model;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Money transfer DTO class
 */
@Getter
@Data
@Builder
@ToString
public class MoneyTransferDto {
    private UUID transactionId;
    private UUID receiverId;
    private UUID senderId;
    private BigDecimal amount;
    public synchronized BigDecimal getAmount() {
        return amount;
    }
}
