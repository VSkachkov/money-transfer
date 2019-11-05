package revolut.service;

import lombok.AllArgsConstructor;
import revolut.converter.AccountConverter;
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
    private final AccountConverter accountConverter = new AccountConverter();

    public Transaction createTransaction(final MoneyTransferDto transferDto) {
        final Transaction transaction = accountRepository.transferBetweenAccounts(transferDto);
        transactionRepository.save(transaction);
        return transaction;
    }

    public List<TransactionDto> getAllTransactions() {
        return transactionRepository.getAll()
                .stream()
                .map(entry -> transactionConverter.convert(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

    public boolean createAccounts(final List<AccountDto> accounts) {
        for (final AccountDto account  :
                accounts) {
            accountRepository.create(account);
        }
        return true; //TODO what is purpose to return true?
    }

    public List<AccountDto> getAccounts() {
        return accountRepository.getAccounts();
    }

    public TransactionDto getTransactionById(final UUID id) {
        return
                transactionConverter.convert(id, transactionRepository.getById(id));
    }

    public AccountDto getAccount(final UUID id) {
        return accountConverter.convert(id, accountRepository.getById(id));
    }
}
