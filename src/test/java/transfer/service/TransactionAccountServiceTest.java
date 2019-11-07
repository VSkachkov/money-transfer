package transfer.service;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import transfer.model.AccountDto;
import transfer.model.MoneyTransferDto;
import transfer.model.Transaction;
import transfer.model.TransactionDto;
import transfer.repository.InMemoryAccountRepository;
import transfer.repository.InMemoryTransactionRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class TransactionAccountServiceTest {
    private TransactionAccountService service = null;
    private final UUID acc1Id = UUID.randomUUID();
    private final UUID acc2Id = UUID.randomUUID();
    private final BigDecimal initBalance1 = BigDecimal.valueOf(100);
    private final BigDecimal initBalance2 = BigDecimal.valueOf(200);
    private final BigDecimal transferAmount1 = BigDecimal.valueOf(1.5);
    private final AccountDto account1Dto = AccountDto.builder().id(acc1Id).balance(initBalance1).build();
    private final AccountDto account2Dto = AccountDto.builder().id(acc2Id).balance(initBalance2).build();

    @Before
    public void init() {
        service = new TransactionAccountService(new InMemoryAccountRepository(), new InMemoryTransactionRepository());
    }

    @Test
    public void testCreateTransaction() {
        initAccounts();
        final TransactionDto transactionDto = createTransaction(acc1Id, acc2Id, transferAmount1);
        Assert.assertEquals(acc1Id, transactionDto.getSenderId());
        Assert.assertEquals(acc2Id, transactionDto.getReceiverId());
        Assert.assertEquals(transferAmount1, transactionDto.getAmount());
    }

    private void initAccounts() {
        final AccountDto created1 = service.createAccount(account1Dto);
        final AccountDto created2 = service.createAccount(account2Dto);
        Assert.assertEquals(account1Dto.getId(), created1.getId());
        Assert.assertEquals(account2Dto.getId(), created2.getId());
        Assert.assertEquals(account1Dto.getBalance(), created1.getBalance());
        Assert.assertEquals(account2Dto.getBalance(), created2.getBalance());
    }

    private TransactionDto createTransaction(final UUID sender, final UUID receiver, final BigDecimal transferAmount) {
        final MoneyTransferDto transferDto = createMoneyTransferDto(sender, receiver, transferAmount);
        return service.createTransaction(transferDto);
    }

    @Test
    public void getAllTransactions() {
        initAccounts();
        Assert.assertTrue(service.getAllTransactions().isEmpty());
        createTransaction(acc1Id, acc2Id, transferAmount1);
        Assert.assertEquals(1, service.getAllTransactions().size());
    }

    BigDecimal calculateSumAllBalances() {
        final List<AccountDto> accountDtoList = service.getAccounts();
        return accountDtoList.stream().map(AccountDto::getBalance).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private MoneyTransferDto createMoneyTransferDto(final UUID sender, final UUID receiver, final BigDecimal transferAmount) {
        return MoneyTransferDto.builder().transactionId(UUID.randomUUID())
                .amount(transferAmount).receiverId(receiver).senderId(sender).build();
    }

    @Test
    public void test() throws InterruptedException {
        initAccounts();
        Assert.assertEquals(BigDecimal.valueOf(300), this.calculateSumAllBalances());
        final ExecutorService exec = Executors.newCachedThreadPool();
        for (int i = 0; i < 13; i++) {
            exec.execute(new MoneyChecker(service));
        }
        TimeUnit.SECONDS.sleep(5);
        Assert.assertTrue(this.calculateSumAllBalances().equals(BigDecimal.valueOf(300.0)));
        System.out.println("Number of transactions " + service.getAllTransactions().size());
    }

    class MoneyChecker implements Runnable {
        TransactionAccountService service;

        MoneyChecker(final TransactionAccountService service) {
            this.service = service;
        }

        public void run() {
            for (int i = 0; i < 10000; i++) {
                service.createTransaction(createMoneyTransferDto(acc1Id, acc2Id, transferAmount1));
                service.createTransaction(createMoneyTransferDto(acc2Id, acc1Id, transferAmount1));
            }
        }
    }

}