package revolut.service;

import lombok.AllArgsConstructor;
import revolut.model.*;
import revolut.converter.TransactionConverter;
import revolut.repository.AccountRepository;
import revolut.repository.TransactionRepository;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@AllArgsConstructor
public class TransactionAccountService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final TransactionConverter transactionConverter = new TransactionConverter();

    public Transaction createTransaction(MoneyTransferDto transferDto) {
        Transaction transaction = accountRepository.transferBetweenAccounts(transferDto);
        transactionRepository.save(transaction);
        return transaction;
    }

    public List<TransactionDto> getAllTransactions() {
        return transactionRepository.getAll()
                .stream()
                .map(entry -> transactionConverter.convert(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
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

    public TransactionDto getTransactionById(UUID id) {
        return
                transactionConverter.convert(id, transactionRepository.getById(id));
    }
}
