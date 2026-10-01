package com.bank.service;

import com.bank.dto.*;
import com.bank.entity.*;
import com.bank.exception.BadRequestException;
import com.bank.exception.ResourceNotFoundException;
import com.bank.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class BankingService {
    private final BankAccountRepository accountRepository;
    private final BankTransactionRepository transactionRepository;
    private final UserRepository userRepository;

    public BankAccount createAccount(CreateAccountRequest request, String username) {
        if (accountRepository.existsByAccountNumber(request.getAccountNumber())) {
            throw new BadRequestException("Account number already exists");
        }
        User user = getUser(username);
        return accountRepository.save(BankAccount.builder()
                .accountNumber(request.getAccountNumber()).accountHolderName(request.getAccountHolderName())
                .balance(BigDecimal.ZERO).status(AccountStatus.ACTIVE).user(user).build());
    }

    public BankAccount getAccount(String accountNumber, String username) {
        BankAccount account = getAccountByNumber(accountNumber);
        verifyOwner(account, username);
        return account;
    }

    public Page<BankTransaction> getHistory(String accountNumber, String username, int page, int size) {
        BankAccount account = getAccountByNumber(accountNumber);
        verifyOwner(account, username);
        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.min(Math.max(size, 1), 50));
        return transactionRepository.findByAccountNumberOrderByTransactionDateDesc(accountNumber, pageable);
    }

    @Transactional
    public BankAccount deposit(String accountNumber, AmountRequest request, String username) {
        BankAccount account = getAccount(accountNumber, username);
        ensureActive(account);
        account.setBalance(account.getBalance().add(request.getAmount()));
        record(accountNumber, TransactionType.DEPOSIT, request.getAmount(), null, "Cash deposit");
        return accountRepository.save(account);
    }

    @Transactional
    public BankAccount withdraw(String accountNumber, AmountRequest request, String username) {
        BankAccount account = getAccount(accountNumber, username);
        ensureActive(account);
        if (account.getBalance().compareTo(request.getAmount()) < 0) {
            throw new BadRequestException("Insufficient balance");
        }
        account.setBalance(account.getBalance().subtract(request.getAmount()));
        record(accountNumber, TransactionType.WITHDRAWAL, request.getAmount(), null, "Cash withdrawal");
        return accountRepository.save(account);
    }

    @Transactional
    public String transfer(String fromAccountNumber, TransferRequest request, String username) {
        if (fromAccountNumber.equals(request.getToAccountNumber())) {
            throw new BadRequestException("Source and destination accounts must be different");
        }
        BankAccount from = getAccount(fromAccountNumber, username);
        BankAccount to = getAccountByNumber(request.getToAccountNumber());
        ensureActive(from);
        ensureActive(to);
        if (from.getBalance().compareTo(request.getAmount()) < 0) {
            throw new BadRequestException("Insufficient balance");
        }
        from.setBalance(from.getBalance().subtract(request.getAmount()));
        to.setBalance(to.getBalance().add(request.getAmount()));
        accountRepository.save(from);
        accountRepository.save(to);
        record(fromAccountNumber, TransactionType.TRANSFER_OUT, request.getAmount(), to.getAccountNumber(), "Fund transfer to another account");
        record(to.getAccountNumber(), TransactionType.TRANSFER_IN, request.getAmount(), from.getAccountNumber(), "Fund transfer received");
        return "Transfer completed successfully";
    }

    private void record(String accountNumber, TransactionType type, BigDecimal amount, String related, String description) {
        transactionRepository.save(BankTransaction.builder().accountNumber(accountNumber).type(type).amount(amount)
                .relatedAccountNumber(related).transactionDate(LocalDateTime.now()).description(description).build());
    }

    private User getUser(String username) {
        return userRepository.findByUsername(username).orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
    private BankAccount getAccountByNumber(String number) {
        return accountRepository.findByAccountNumber(number).orElseThrow(() -> new ResourceNotFoundException("Account not found"));
    }
    private void verifyOwner(BankAccount account, String username) {
        if (!account.getUser().getUsername().equals(username)) throw new BadRequestException("You are not authorized to access this account");
    }
    private void ensureActive(BankAccount account) {
        if (account.getStatus() != AccountStatus.ACTIVE) throw new BadRequestException("Account is blocked");
    }
}
