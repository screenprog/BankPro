package com.screenprog.application.service;

import com.screenprog.application.model.Account;
import com.screenprog.application.model.AccountDTO;
import com.screenprog.application.model.Customer;
import com.screenprog.application.model.Staff;
import com.screenprog.application.repo.AccountRepository;
import com.screenprog.application.repo.CustomerRepository;
import com.screenprog.application.repo.StaffRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@org.springframework.stereotype.Service
public class Service {

    final private CustomerRepository customerRepository;

    final private AccountRepository accountRepository;

    final private StaffRepository staffRepository;

    @Autowired
    public Service(CustomerRepository customerRepository, AccountRepository accountRepository, StaffRepository staffRepository) {
        this.customerRepository = customerRepository;
        this.accountRepository = accountRepository;
        this.staffRepository = staffRepository;
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
}
