package revolut.data.user;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import revolut.entity.Account;
import revolut.entity.MoneyTransferDto;
import revolut.repository.InMemoryAccountRepository;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class InMemoryTransactionRepositoryTest {
    UUID user1 = UUID.randomUUID();
    UUID user2 = UUID.randomUUID();
    BigDecimal one = BigDecimal.ONE; //TODO java is pass-by-value
    MoneyTransferDto transferDto1 = MoneyTransferDto.builder().receiverId(user1).senderId(user2).amount(one).build();
    MoneyTransferDto transferDto2 = MoneyTransferDto.builder().receiverId(user2).senderId(user1).amount(one).build();
    InMemoryAccountRepository repository;

    @Before
    public void setUp() throws Exception {
        Account account1 = Account.builder().balance(BigDecimal.TEN).build();
        Account account2 = Account.builder().balance(BigDecimal.TEN).build();
        Map<UUID, Account> initial = new HashMap<>();
        initial.put(user1, account1);
        initial.put(user2, account2);
        repository = new InMemoryAccountRepository(initial);
    }

    @Test
    public void createTransaction() {
        repository.transferBetweenAccounts(transferDto1);
        repository.transferBetweenAccounts(transferDto2);
        Assert.assertEquals(BigDecimal.valueOf(20),
                repository.getAccountStatus(user1).getBalance()
                        .add(repository.getAccountStatus(user2).getBalance()));
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
        InMemoryAccountRepository repository;

        MoneyChecker(InMemoryAccountRepository repository) {
            this.repository = repository;
        }

        public void run() {
            if (repository == null) {
                throw new NullPointerException();
            }
            System.out.println("Initial value u1 " + repository.getAccountStatus(user1).getBalance());
            System.out.println("Initial value u2 " + repository.getAccountStatus(user2).getBalance());
            int n = 0;
            while (true) {
                n++;
                for (int i = 0; i < 1000; i++) {
                    repository.transferBetweenAccounts(transferDto1);
                    repository.transferBetweenAccounts(transferDto2);
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