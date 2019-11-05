package revolut.model;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

@Builder
@Getter
public class Transaction {
    private UUID transactionId;
    private Account sender;
    private Account receiver;
    private BigDecimal amount;
    private Date time;
    private Status transactionStatus;
}
