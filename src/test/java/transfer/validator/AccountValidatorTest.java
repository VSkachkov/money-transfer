package transfer.validator;

import org.junit.Test;
import transfer.errors.ApplicationException;
import transfer.model.AccountDto;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class AccountValidatorTest {
    private final AccountValidator validator = new AccountValidator();
    private UUID accountId = UUID.randomUUID();

    @Test(expected = ApplicationException.class)
    public void testNewAccountWithoutBalance() {
        final AccountDto dto = AccountDto.builder().id(accountId).balance(null).build();
        validator.validateNewAccount(dto);
    }

    @Test(expected = ApplicationException.class)
    public void testNewAccountWithoutAccountId() {
        final AccountDto dto = AccountDto.builder().id(null).balance(BigDecimal.ONE).build();
        validator.validateNewAccount(dto);
    }
}