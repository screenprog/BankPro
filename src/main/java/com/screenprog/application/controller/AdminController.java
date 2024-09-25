package com.screenprog.application.controller;

import com.screenprog.application.model.Account;
import com.screenprog.application.model.AccountDTO;
import com.screenprog.application.model.Customer;
import com.screenprog.application.model.Staff;
import com.screenprog.application.service.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminController {

    final private Service service;
    @Autowired
    public AdminController(Service service) {
        this.service = service;
    }

    @PostMapping("/add-one-account")
    public Customer addAccount(@RequestBody AccountDTO account){
        return service.addAccount(account);
    }

    @GetMapping("/get-all-accounts")
    public List<Account> getAllAccounts(){
        return service.getAllAccounts();
    }

    @PostMapping("/add-one-customer")
    public Customer addCustomer(@RequestBody Customer customer){
        return service.addCustomer(customer);
    }

    @GetMapping("/get-all-customers")
    public List<Customer> getAllCustomers(){
        return service.getAllCustomer();
    }

    @PostMapping("/add-one-staff")
    public Staff addStaff(@RequestBody Staff staff){
        return service.addStaff(staff);
    }

    @PostMapping("/get-all-staff")
    public List<Staff> getAllStaff(){
        return service.getAllStaff();
    }


}
