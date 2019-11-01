package revolut.domain.account;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class UserService {

    private final UserAccountsRepository userAccountsRepository;

    public String createTransaction(MoneyTransferDto transferDto) {
        return userAccountsRepository.createTransaction(transferDto);
    }

}
