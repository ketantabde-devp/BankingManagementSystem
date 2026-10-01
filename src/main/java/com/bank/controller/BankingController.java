package com.bank.controller;

import com.bank.dto.*;
import com.bank.entity.BankAccount;
import com.bank.entity.BankTransaction;
import com.bank.service.BankingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class BankingController {
    private final BankingService bankingService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BankAccount createAccount(@Valid @RequestBody CreateAccountRequest request, Authentication authentication) {
        return bankingService.createAccount(request, authentication.getName());
    }

    @GetMapping("/{accountNumber}")
    public BankAccount getAccount(@PathVariable String accountNumber, Authentication authentication) {
        return bankingService.getAccount(accountNumber, authentication.getName());
    }

    @PostMapping("/{accountNumber}/deposit")
    public BankAccount deposit(@PathVariable String accountNumber, @Valid @RequestBody AmountRequest request, Authentication authentication) {
        return bankingService.deposit(accountNumber, request, authentication.getName());
    }

    @PostMapping("/{accountNumber}/withdraw")
    public BankAccount withdraw(@PathVariable String accountNumber, @Valid @RequestBody AmountRequest request, Authentication authentication) {
        return bankingService.withdraw(accountNumber, request, authentication.getName());
    }

    @PostMapping("/{accountNumber}/transfer")
    public String transfer(@PathVariable String accountNumber, @Valid @RequestBody TransferRequest request, Authentication authentication) {
        return bankingService.transfer(accountNumber, request, authentication.getName());
    }

    @GetMapping("/{accountNumber}/transactions")
    public Page<BankTransaction> history(@PathVariable String accountNumber,
                                         @RequestParam(defaultValue = "0") int page,
                                         @RequestParam(defaultValue = "10") int size,
                                         Authentication authentication) {
        return bankingService.getHistory(accountNumber, authentication.getName(), page, size);
    }
}
