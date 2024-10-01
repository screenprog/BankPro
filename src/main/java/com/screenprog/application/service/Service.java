package com.screenprog.application.service;

import com.screenprog.application.model.*;
import com.screenprog.application.repo.AccountRepository;
import com.screenprog.application.repo.CustomerRepository;
import com.screenprog.application.repo.StaffRepository;
import com.screenprog.application.repo.TransactionsRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

@org.springframework.stereotype.Service
public class Service {

    final private CustomerRepository customerRepository;

    final private AccountRepository accountRepository;

    final private StaffRepository staffRepository;
    final private TransactionsRepository transactionRepository;

    @Autowired
    public Service(CustomerRepository customerRepository, AccountRepository accountRepository, StaffRepository staffRepository, TransactionsRepository transactionRepository) {
        this.customerRepository = customerRepository;
        this.accountRepository = accountRepository;
        this.staffRepository = staffRepository;
        this.transactionRepository = transactionRepository;
    }

    public List<Customer> getAllCustomer() {
        return customerRepository.findAll();
    }

    public Customer addCustomer(Customer customer) {
        customerRepository.save(customer);
        return customerRepository.findById(customer.getCustomerID()).orElseThrow(
                ()-> new RuntimeException("Customer is not saved")
                );
    }


    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    public Customer addAccount(AccountDTO account) {
        Customer customer = customerRepository.findById(account.customerId()).orElseThrow(() -> new RuntimeException("dfgjl"));
        Account account1 = new Account();
        account1.setCustomer(customer);
        account1.setBalance(account.balance());
        account1.setStatus(account.status());
        account1.setType(account.type());
        accountRepository.save(account1);
        return customer;
    }

    public Staff addStaff(Staff staff) {
        return staffRepository.save(staff);
    }

    public List<Staff> getAllStaff() {
        return staffRepository.findAll();
    }

    @Transactional
    public void deposit(Long accountId, Double amount) {

        Account account = getAccount(accountId);
        if(amount <= 0)
            throw new IllegalArgumentException("Deposit amount must be positive");
        account.setBalance(account.getBalance() + amount);
        Transaction transaction = Transaction.builder()
                .accountId(account)
                .amount(amount)
                .balanceLeft(account.getBalance())
                .build();
        transactionRepository.save(transaction);
        accountRepository.save(account);
    }

    public void withdraw(Long accountId, Double amount) {
        Account account = getAccount(accountId);
        if(account.getBalance() - amount < 100)
            throw new IllegalArgumentException("Insufficient balance");
        account.setBalance(account.getBalance() - amount);
        Transaction transaction = Transaction.builder()
                .accountId(account)
                .amount(amount)
                .balanceLeft(account.getBalance())
                .build();
        transactionRepository.save(transaction);
    }

    private Account getAccount(Long accountId) {
        return accountRepository.findById(accountId).orElseThrow(()-> new RuntimeException("Account Not Found"));
    }
}
