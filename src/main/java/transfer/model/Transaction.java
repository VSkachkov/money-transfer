package transfer.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.locks.ReentrantLock;

@Builder
@Getter
public class Transaction {
    private UUID transactionId;
    private Account sender;
    private Account receiver;
    private BigDecimal amount;
    private Date time;
    @Setter
    private Status transactionStatus;
    @Builder.Default
    private ReentrantLock lock = new ReentrantLock();
}
