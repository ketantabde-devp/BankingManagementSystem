package com.bank.service;

import com.bank.dto.AmountRequest;
import com.bank.entity.*;
import com.bank.exception.BadRequestException;
import com.bank.repository.BankAccountRepository;
import com.bank.repository.BankTransactionRepository;
import com.bank.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BankingServiceTest {
    @Mock BankAccountRepository accountRepository;
    @Mock BankTransactionRepository transactionRepository;
    @Mock UserRepository userRepository;
    @InjectMocks BankingService bankingService;

    @Test
    void depositShouldIncreaseBalance() {
        User user = User.builder().username("ketan").build();
        BankAccount account = BankAccount.builder().accountNumber("1234567890").balance(new BigDecimal("100.00"))
                .status(AccountStatus.ACTIVE).user(user).build();
        AmountRequest request = new AmountRequest(); request.setAmount(new BigDecimal("50.00"));
        when(accountRepository.findByAccountNumber(account.getAccountNumber())).thenReturn(Optional.of(account));
        when(accountRepository.save(any(BankAccount.class))).thenAnswer(i -> i.getArgument(0));

        BankAccount result = bankingService.deposit(account.getAccountNumber(), request, "ketan");

        assertEquals(new BigDecimal("150.00"), result.getBalance());
        verify(transactionRepository).save(any(BankTransaction.class));
    }

    @Test
    void withdrawalShouldFailWhenBalanceIsInsufficient() {
        User user = User.builder().username("ketan").build();
        BankAccount account = BankAccount.builder().accountNumber("1234567890").balance(new BigDecimal("100.00"))
                .status(AccountStatus.ACTIVE).user(user).build();
        AmountRequest request = new AmountRequest(); request.setAmount(new BigDecimal("150.00"));
        when(accountRepository.findByAccountNumber(account.getAccountNumber())).thenReturn(Optional.of(account));

        assertThrows(BadRequestException.class, () -> bankingService.withdraw(account.getAccountNumber(), request, "ketan"));
        verify(accountRepository, never()).save(any(BankAccount.class));
    }
}
