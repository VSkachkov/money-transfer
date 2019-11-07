package transfer.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import static transfer.service.TransactionAccountService.WAIT_DELAY;

/**
 * Account entity class
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class Account {
    private static final Logger log = LoggerFactory.getLogger(Account.class);

    private UUID id;
    private BigDecimal balance;
    private  Currency currency;
    @Builder.Default
    private ReentrantLock lock = new ReentrantLock();

    public BigDecimal getBalance() {
        try {
            lock.lock();
            return balance;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Increases balance of account on augend value
     *
     * @param augend amount of money to add
     * @return account
     */
    public Account add(final BigDecimal augend) {
        try {
            if (lock.tryLock(WAIT_DELAY, TimeUnit.SECONDS)) {
                try {
                    balance = balance.add(augend);
                } finally {
                    lock.unlock();
                }
            }
        } catch (final InterruptedException e) {
            log.error(e.getMessage(), e);
        }
        return this;
    }

    /**
     * Reduces balance of account on subtrahend value
     *
     * @param subtrahend amount of money to remove from account
     * @return account
     */
    public Account substract(final BigDecimal subtrahend) {
        try {
            if (lock.tryLock(WAIT_DELAY, TimeUnit.SECONDS)) {
                try {
                    balance = balance.subtract(subtrahend);
                } finally {
                    lock.unlock();
                }
            }
        } catch (final InterruptedException e) {
            log.error(e.getMessage(), e);
        }
        return this;
    }
}
