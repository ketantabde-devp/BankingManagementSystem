package com.bank.controller;

import com.bank.entity.BankAccount;
import com.bank.repository.BankAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    private final BankAccountRepository accountRepository;

    @GetMapping("/accounts")
    public List<BankAccount> getAllAccounts() { return accountRepository.findAll(); }

    @PatchMapping("/accounts/{accountNumber}/block")
    public BankAccount block(@PathVariable String accountNumber) {
        BankAccount account = accountRepository.findByAccountNumber(accountNumber).orElseThrow();
        account.setStatus(com.bank.entity.AccountStatus.BLOCKED);
        return accountRepository.save(account);
    }
}
