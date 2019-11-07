package transfer.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Account DTO class
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@ToString
public class AccountDto {
    private UUID id;
    private BigDecimal balance;
    private Currency currency; //TODO to implement currency exchange
}
