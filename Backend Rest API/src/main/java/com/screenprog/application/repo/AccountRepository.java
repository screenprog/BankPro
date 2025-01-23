package com.screenprog.application.repo;

import com.screenprog.application.dtos.AccountDTO;
import com.screenprog.application.model.Account;
import jakarta.persistence.Tuple;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    @Query("SELECT accountNumber, balance, status, openDate FROM Account")
    List<Tuple> findAccountDetailsForFrontend();
}
