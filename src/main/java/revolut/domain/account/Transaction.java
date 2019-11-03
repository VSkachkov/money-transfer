package revolut.domain.account;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.Date;

@Builder
@Getter
public class Transaction {
    private Account sender;
    private Account receiver;
    private BigDecimal amount;
    private Date time;
    private Status transactionStatus;
}
