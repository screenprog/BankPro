package com.screenprog.application.controller;

import com.screenprog.application.model.*;
import com.screenprog.application.service.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/staff")
public class StaffController {
    final private Service service;

    @Autowired
    public StaffController(Service service) {
        this.service = service;
    }


    @PostMapping("/add-one-account")
    private ResponseEntity<Void> addAccount(@RequestBody AccountDTO account, UriComponentsBuilder ucb) {
        Optional<Account> accountSaved = service.addAccount(account);

        if(accountSaved.isEmpty())
            return ResponseEntity.notFound().build();

        URI locationOfNewCustomer = ucb.path("/staff/get-account/{id}")
                .buildAndExpand(accountSaved.get().getAccountNumber())
                .toUri();
        return ResponseEntity.created(locationOfNewCustomer).build();
    }

    @GetMapping("get-account/{id}")
    private ResponseEntity<Account> getById(@PathVariable Long id){
        Optional<Account> account = Optional.of(service.getAccount(id));
        return account.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());

    }

    @GetMapping("/get-all-accounts")
    public ResponseEntity<List<Account>> getAllAccounts(){
        return ResponseEntity.ok(service.getAllAccounts());
    }

    @PostMapping("/add-one-customer")
    public ResponseEntity<Void> addCustomer(@RequestBody CustomerDTO customer, UriComponentsBuilder ucb){
        Customer customer1 = service.addCustomer(customer);
        URI locationOfNewCustomer = ucb.path("/staff/get-customer/{id}")
                .buildAndExpand(customer1.getCustomerID())
                .toUri();
        return ResponseEntity.created(locationOfNewCustomer).build();
    }

    @GetMapping("/get-customer/{id}")
    public ResponseEntity<Customer> getCustomer(@PathVariable Long id){
        Customer customer = service.getCustomer(id);
        if(customer == null)
            return ResponseEntity.notFound().build();

        return ResponseEntity.ok(customer);
    }

    @GetMapping("/get-all-customers")
    public ResponseEntity<List<Customer>> getAllCustomers(){
        return ResponseEntity.ok(service.getAllCustomer());
    }


    @PostMapping("deposit/{accountId}")
    public ResponseEntity<String> deposit(@PathVariable Long accountId, @RequestParam Double amount){
        try{
            Transaction transaction = service.deposit(accountId, amount);
            if(transaction == null)
                return ResponseEntity.notFound().build();
            return ResponseEntity.ok("Deposit Successful");
        } catch (IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (RuntimeException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PostMapping("withdraw/{accountId}")
    public ResponseEntity<String> withdraw(@PathVariable Long accountId, @RequestParam Double amount){
        try {
            Transaction transaction = service.withdraw(accountId, amount);
            if (transaction == null)
                return ResponseEntity.notFound().build();
            return ResponseEntity.ok("Withdrawal Successful");
        } catch (IllegalArgumentException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (RuntimeException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("get-pending-application")
    public ResponseEntity<List<Application>> getPendingApplications(){
        List<Application> pendingApplications = service.getPendingApplications();
        return pendingApplications.isEmpty()?
                ResponseEntity.notFound().build():
                ResponseEntity.ok(pendingApplications);
    }

//    TODO: implement customer creation if application is
//    TODO: verified
    @PostMapping("update-applications")
    public ResponseEntity<List<Application>> updateApplicationStatus(@RequestBody List<Application> applications){
      return ResponseEntity.ok(service.updateApplications(applications));
    }
}
