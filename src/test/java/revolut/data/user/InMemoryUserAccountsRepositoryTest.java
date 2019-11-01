package revolut.data.user;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import revolut.domain.account.MoneyTransferDto;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;


class InMemoryUserAccountsRepositoryTest {
    UUID user1 = UUID.randomUUID();
    UUID user2 = UUID.randomUUID();
    BigDecimal one = BigDecimal.ONE; //TODO java is pass-by-value
    MoneyTransferDto transferDto1 = MoneyTransferDto.builder().receiverId(user1).senderId(user2).amount(one).build();
    MoneyTransferDto transferDto2 = MoneyTransferDto.builder().receiverId(user2).senderId(user1).amount(one).build();

    @Test
    void createTransaction() {
        Map<UUID, BigDecimal> initial = new HashMap<>();
        initial.put(user1, BigDecimal.TEN);
        initial.put(user2, BigDecimal.TEN);
        InMemoryUserAccountsRepository repository = new InMemoryUserAccountsRepository(initial);
        repository.createTransaction(transferDto1);
        repository.createTransaction(transferDto2);
        Assertions.assertEquals(BigDecimal.valueOf(20), repository.getAccountStatus(user1).add(repository.getAccountStatus(user2)));
    }

    class MoneyChecker implements Runnable {
        InMemoryUserAccountsRepository repository;

        MoneyChecker(InMemoryUserAccountsRepository repository) {
            this.repository = repository;
        }

        public void run() {
//            for (int i = 0; i < 100; i++) {
//                repository.createTransaction(transferDto1);
//                repository.createTransaction(transferDto2);
//            }
            while (true) {
                for (int i = 0; i < 1000; i++) {
                    repository.createTransaction(transferDto1);
                    repository.createTransaction(transferDto2);
                }
                BigDecimal acc1 = repository.getAccountStatus(user1);
                BigDecimal acc2 = repository.getAccountStatus(user2);
                if (acc1.add(acc2).compareTo(BigDecimal.valueOf(20)) != 0) {
                    System.out.println("acc1 " + acc1.toString());
                    System.out.println("acc2 " + acc2.toString());
                    System.exit(0);
                }
            }
        }
    }


    @Test
    public void test() throws InterruptedException {
        UUID user1 = UUID.randomUUID();
        UUID user2 = UUID.randomUUID();
        Map<UUID, BigDecimal> initial = new HashMap<>();
        initial.put(user1, BigDecimal.TEN);
        initial.put(user2, BigDecimal.TEN);
        InMemoryUserAccountsRepository repository = new InMemoryUserAccountsRepository(initial);
        ExecutorService exec =
                Executors.newCachedThreadPool();
        for (int i = 0; i < 10; i++) {
            exec.execute(new MoneyChecker(repository));
        }
        exec.shutdown();
        Assertions.assertEquals(BigDecimal.valueOf(20), repository.getAccountStatus(user1).add(repository.getAccountStatus(user2)));
    }
}