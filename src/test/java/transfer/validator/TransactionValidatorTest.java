package transfer.validator;

import org.junit.Test;
import transfer.errors.ApplicationException;
import transfer.model.MoneyTransferDto;
import transfer.model.Transaction;

import java.math.BigDecimal;
import java.util.UUID;

public class TransactionValidatorTest {

    private TransactionValidator validator = new TransactionValidator();
    private final UUID transactionId = UUID.randomUUID();
    private final UUID senderId = UUID.randomUUID();
    private final UUID receiverId = UUID.randomUUID();
    private final BigDecimal zeroAmount = BigDecimal.ZERO;
    private final BigDecimal tenUnitsamount = BigDecimal.TEN;

    @Test(expected = ApplicationException.class)
    public void testDtoHadNoTransactionId() {
        final UUID transactionId = null;
        final MoneyTransferDto moneyTransferDto = MoneyTransferDto.builder().transactionId(transactionId).build();
        validator.validateNewTransferRequest(moneyTransferDto);
    }

    @Test(expected = ApplicationException.class)
    public void testSenderAndReceiverAreTheSame() {
        final UUID receiverId = senderId;
        final MoneyTransferDto moneyTransferDto = MoneyTransferDto.builder().transactionId(transactionId)
                .receiverId(receiverId).senderId(senderId).amount(tenUnitsamount).build();
        validator.validateNewTransferRequest(moneyTransferDto);
    }

    @Test(expected = ApplicationException.class)
    public void testZeroMoneyAmount() {
        final MoneyTransferDto moneyTransferDto = MoneyTransferDto.builder().transactionId(transactionId)
                .receiverId(receiverId).senderId(senderId).amount(zeroAmount).build();
        validator.validateNewTransferRequest(moneyTransferDto);
    }

    @Test(expected = ApplicationException.class)
    public void testNegatimeMoneyAmount() {
        final MoneyTransferDto moneyTransferDto = MoneyTransferDto.builder().transactionId(transactionId)
                .receiverId(receiverId).senderId(senderId).amount(BigDecimal.valueOf(-1)).build();
        validator.validateNewTransferRequest(moneyTransferDto);
    }

    @Test(expected = ApplicationException.class)
    public void validateTransactionExistence() {
        validator.validateTransactionExistence(Transaction.builder().build());
    }
}