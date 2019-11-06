package transfer.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Account DTO class
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class AccountDto {
    private UUID id;
    private BigDecimal balance;
    private Currency currency;
}
