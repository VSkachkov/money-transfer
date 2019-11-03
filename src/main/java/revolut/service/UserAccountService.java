package revolut.service;

import lombok.AllArgsConstructor;
import revolut.entity.Account;
import revolut.entity.AccountDto;
import revolut.entity.MoneyTransferDto;
import revolut.entity.Transaction;
import revolut.repository.AccountRepository;
import revolut.repository.TransactionRepository;

import java.util.List;

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

    public boolean createAccounts(List<AccountDto> accounts) {
        for (AccountDto account  :
                accounts) {
            accountRepository.create(account);
        }
        return true; //TODO what is purpose to return true?
    }

    public List<AccountDto> getAccounts() {
        return accountRepository.getAccounts();
    }
}
