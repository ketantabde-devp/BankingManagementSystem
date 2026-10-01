package com.bank.repository;

import com.bank.entity.BankTransaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BankTransactionRepository extends JpaRepository<BankTransaction, Long> {
    Page<BankTransaction> findByAccountNumberOrderByTransactionDateDesc(String accountNumber, Pageable pageable);
}
