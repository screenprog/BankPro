package com.screenprog.application.repo;

import com.screenprog.application.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestBody;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
}
