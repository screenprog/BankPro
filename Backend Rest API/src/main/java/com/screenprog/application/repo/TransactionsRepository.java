package com.screenprog.application.repo;

import com.screenprog.application.model.Account;
import com.screenprog.application.model.Transaction;
import jakarta.persistence.Tuple;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionsRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findAllByAccountId(Account accountNumber);

    @Query("SELECT transactionId, amount, balanceLeft, transactionDate, description FROM Transaction")
    List<Tuple> findTransactionsForFrontEnd();
}
