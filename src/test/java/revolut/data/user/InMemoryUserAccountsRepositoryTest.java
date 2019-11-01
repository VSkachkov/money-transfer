package revolut.data.user;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import revolut.domain.account.MoneyTransferDto;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

public class InMemoryUserAccountsRepositoryTest {
    UUID user1 = UUID.randomUUID();
    UUID user2 = UUID.randomUUID();
    BigDecimal one = BigDecimal.ONE; //TODO java is pass-by-value
    MoneyTransferDto transferDto1 = MoneyTransferDto.builder().receiverId(user1).senderId(user2).amount(one).build();
    MoneyTransferDto transferDto2 = MoneyTransferDto.builder().receiverId(user2).senderId(user1).amount(one).build();
    InMemoryUserAccountsRepository repository;
    @Before
    public void setUp() throws Exception {
        Map<UUID, BigDecimal> initial = new HashMap<>();
        initial.put(user1, BigDecimal.TEN);
        initial.put(user2, BigDecimal.TEN);
        repository = new InMemoryUserAccountsRepository(initial);
    }

    @Test
    public void createTransaction() {
        repository.createTransaction(transferDto1);
        repository.createTransaction(transferDto2);
        Assert.assertEquals(BigDecimal.valueOf(20), repository.getAccountStatus(user1).add(repository.getAccountStatus(user2)));
    }

    @Test
    public void test() throws InterruptedException {

        Assert.assertEquals(BigDecimal.valueOf(20), repository.getSum());
        ExecutorService exec =
                Executors.newCachedThreadPool();
        for (int i = 0; i < 10; i++) {
            exec.execute(new MoneyChecker(repository));
        }
        TimeUnit.SECONDS.sleep(5);
        Assert.assertEquals(BigDecimal.valueOf(20), repository.getSum());
    }

    class MoneyChecker implements Runnable {
        InMemoryUserAccountsRepository repository;

        MoneyChecker(InMemoryUserAccountsRepository repository) {
            this.repository = repository;
        }

        public void run() {
            if (repository == null) {
                throw new NullPointerException();
            }
            System.out.println("Initial value u1 " + repository.getAccountStatus(user1));
            System.out.println("Initial value u2 " + repository.getAccountStatus(user2));
            int n = 0;
            while (true) {
                n++;
                for (int i = 0; i < 1000; i++) {
                    repository.createTransaction(transferDto1);
                    repository.createTransaction(transferDto2);
                }
                BigDecimal accSum = repository.getSum();
                if (accSum.compareTo(BigDecimal.valueOf(20)) != 0) {
                    System.out.println("Inconsistent data found!!");
                    System.out.println("acc sum " + accSum);
                    System.out.println("n " + n);
                    System.exit(0);
                }
            }
        }
    }
}