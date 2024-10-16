package com.screenprog.application.controller;

import com.screenprog.application.model.*;
import com.screenprog.application.service.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;

@RestController
@RequestMapping("/admin")
public class AdminController {

    final private Service service;
    @Autowired
    public AdminController(Service service) {
        this.service = service;
    }

    @PostMapping("/add-one-account")
    private ResponseEntity<Void> addAccount(@RequestBody AccountDTO account, UriComponentsBuilder ucb) {
        Optional<Account> accountSaved = service.addAccount(account);

        if(accountSaved.isEmpty())
            return ResponseEntity.notFound().build();

        URI locationOfNewCustomer = ucb.path("/admin/get-account/{id}")
                .buildAndExpand(accountSaved.get().getAccountNumber())
                .toUri();
        return ResponseEntity.created(locationOfNewCustomer).build();
    }

    @GetMapping("get-account/{id}")
    private ResponseEntity<Account> getById(@PathVariable Long id){
        Optional<Account> account = service.getAccount(id);
        return account.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());

    }

    @GetMapping("/get-all-accounts")
    public ResponseEntity<List<Account>> getAllAccounts(){
        return ResponseEntity.ok(service.getAllAccounts());
    }

    @PostMapping("/add-one-customer")
    public ResponseEntity<Void> addCustomer(@RequestBody Customer customer, UriComponentsBuilder ucb){
        Customer customer1 = service.addCustomer(customer);
        URI locationOfNewCustomer = ucb.path("get-customer/{id}")
                .buildAndExpand(customer1.getCustomerID())
                .toUri();
        return ResponseEntity.created(locationOfNewCustomer).build();
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
