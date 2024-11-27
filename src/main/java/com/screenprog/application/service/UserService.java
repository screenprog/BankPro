package com.screenprog.application.service;

import com.screenprog.application.model.Account;
import com.screenprog.application.model.ApplicationStatus;
import com.screenprog.application.model.Apply;
import com.screenprog.application.repo.AccountRepository;
import com.screenprog.application.repo.ApplicationsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {


    @Autowired
    private ApplicationsRepository applicationsRepository;
    @Autowired
    private AccountRepository accountRepository;

    public String applyCustomer(Apply apply) {
        apply.setStatus(ApplicationStatus.PENDING);
        applicationsRepository.save(apply);
        return "Wait for your verification";
    }

    public Double getBalance(Long accountNumber) {
        return accountRepository.findById(accountNumber)
                .map(Account::getBalance)
                .orElse(null);
    }
}
