package revolut.domain.account;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class UserAccountService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    public Transaction createTransaction(MoneyTransferDto transferDto) {
        Transaction transaction = accountRepository.transferBetweenAccounts(transferDto);
        transactionRepository.save(transaction);
        return transaction;
    }

    public String getAllTransactions() {
        return accountRepository.getAll();
    }
}
