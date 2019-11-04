package revolut.model;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Data
@Builder
public class MoneyTransferDto {
    private UUID receiverId;
    private UUID senderId;
    private BigDecimal amount;
}
