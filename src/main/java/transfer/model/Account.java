package transfer.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.locks.ReentrantLock;


@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class Account {
    private UUID id;
    private BigDecimal balance;
    private Currency currency;
    @Builder.Default
    private ReentrantLock lock = new ReentrantLock();

    public synchronized Account add(final BigDecimal augend) {
        balance = balance.add(augend);
        return this;
    }

    public synchronized Account substract(final BigDecimal subtrahend) {
        balance = balance.subtract(subtrahend);
        return this;
    }
}