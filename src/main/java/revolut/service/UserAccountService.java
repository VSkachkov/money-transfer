package revolut.service;

import lombok.AllArgsConstructor;
import revolut.entity.MoneyTransferDto;
import revolut.entity.Transaction;
import revolut.repository.AccountRepository;
import revolut.repository.TransactionRepository;

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
