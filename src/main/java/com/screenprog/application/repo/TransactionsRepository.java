package com.screenprog.application.repo;

import com.screenprog.application.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionsRepository extends JpaRepository<Transaction, Long> {
}
