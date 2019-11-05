package transfer.service;

import lombok.AllArgsConstructor;
import transfer.converter.AccountConverter;
import transfer.model.*;
import transfer.converter.TransactionConverter;
import transfer.repository.AccountRepository;
import transfer.repository.TransactionRepository;
import transfer.validator.AccountValidator;
import transfer.validator.TransactionValidator;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@AllArgsConstructor
public class TransactionAccountService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final TransactionConverter transactionConverter = new TransactionConverter();
    private final AccountConverter accountConverter = new AccountConverter();
    private final TransactionValidator transactionValidator = new TransactionValidator();
    private final AccountValidator accountValidator = new AccountValidator();

    public TransactionDto createTransaction(final MoneyTransferDto transferDto) {
        final Transaction transaction = accountRepository.transferBetweenAccounts(transferDto);
        final UUID transactionId = UUID.randomUUID(); //TODO think what to do with transactions
        transactionRepository.save(transactionId, transaction);
        return transactionConverter.convert(transactionId, transaction);
    }

    public List<TransactionDto> getAllTransactions() {
        return transactionRepository.getAll()
                .stream()
                .map(entry -> transactionConverter.convert(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

    public AccountDto createAccount(final AccountDto newAccount) {
        accountValidator.validateNewAccount(newAccount);
        final Account created = accountRepository.create(newAccount);
        return AccountDto.builder()
                .id(newAccount.getId())
                .currency(created.getCurrency())
                .balance(created.getBalance())
                .build();

    }

    public List<AccountDto> getAccounts() {
        return accountRepository.getAccounts()
                .stream()
                .map(entry -> accountConverter.convert(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

    public TransactionDto getTransactionById(final UUID id) {
        return
                transactionConverter.convert(id, transactionRepository.getById(id));
    }

    public AccountDto getAccount(final UUID id) {
        return accountConverter.convert(id, accountRepository.getById(id));
    }
}
