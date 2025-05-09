package com.screenprog.application.controller;

import com.screenprog.application.dtos.AccountDTO;
import com.screenprog.application.for_optimization.AccountDetails;
import com.screenprog.application.dtos.CustomerDTO;
import com.screenprog.application.dtos.TransferDTO;
import com.screenprog.application.for_optimization.CustomerDetails;
import com.screenprog.application.for_optimization.TransactionDetails;
import com.screenprog.application.model.*;
import com.screenprog.application.service.ApplicationsService;
import com.screenprog.application.service.CenteralisedService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    final private CenteralisedService service;
    final private ApplicationsService applicationsService;
    final private Logger LOGGER = LoggerFactory.getLogger(StaffController.class);

    public StaffController(CenteralisedService service, ApplicationsService applicationsService) {
        this.service = service;
        this.applicationsService = applicationsService;
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
    public ResponseEntity<List<AccountDetails>> getAllAccounts(){
        return ResponseEntity.ok(service.getAllAccounts());
    }

    /*TODO: test this end-point :o DONE*/
    /*TODO: This is completed and working very well*/
    /*TODO: Do not touch it! It's working very fine*/
    @PostMapping("/add-one-customer")
    public ResponseEntity<Void> addCustomer(@ModelAttribute CustomerDTO customer, UriComponentsBuilder ucb){
        LOGGER.info("Inside add one customer");
        Customer customer1 = service.addCustomer(customer);
        URI locationOfNewCustomer = ucb.path("/staff/get-customer/{id}")
                .buildAndExpand(customer1.getCustomerID())
                .toUri();
        return ResponseEntity.created(locationOfNewCustomer).build();
    }

    @GetMapping("all-transactions")
    private ResponseEntity<List<TransactionDetails>> allTransactions(){
        return ResponseEntity.ok(service.getAllTransactions());
    }
    @GetMapping("/get-customer")
    public ResponseEntity<Customer> getCustomer(@RequestParam Long id){
        Customer customer = service.getCustomer(id);
        if(customer == null)
            return ResponseEntity.notFound().build();

        return ResponseEntity.ok(customer);
    }

    @GetMapping("/get-all-customers")
    public ResponseEntity<List<CustomerDetails>> getAllCustomers(){
        return ResponseEntity.ok(service.getAllCustomer());
    }


    @PostMapping("deposit")
    public ResponseEntity<String> deposit(@RequestBody TransferDTO transferDTO){
        try{
            Transaction transaction = service.deposit(transferDTO.accountIdOfReceiver(), transferDTO.balance());
            if(transaction == null)
                return ResponseEntity.notFound().build();
            return ResponseEntity.ok("Deposit Successful");
        } catch (IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (RuntimeException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }


    @PutMapping("withdraw")
    public ResponseEntity<String> withdraw(@RequestBody TransferDTO transferDTO){
        LOGGER.info("Inside withdraw");
        try {
            Transaction transaction = service.withdraw(transferDTO.accountIdOfReceiver(), transferDTO.balance());
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
        List<Application> pendingApplications = applicationsService.getPendingApplications();
        return pendingApplications.isEmpty()?
                ResponseEntity.notFound().build():
                ResponseEntity.ok(pendingApplications);
    }

    /*TODO: implement customer creation if application is verified :o DONE */
    /*TODO: Test this implementation :o DONE*/
    @PostMapping("update-applications")
    public ResponseEntity<List<Application>> updateApplicationStatus(@RequestBody List<Application> applications){
        return ResponseEntity.ok(applicationsService.updateApplications(applications));
    }

    /*TODO: Add a feature to fetch the pending accounts to make them active*/


    /*TODO: Test this end point :o DONE*/
    /*TODO: The precision needs to be fixed from 7876.540000000001 to 7876.54*/
    @PutMapping("transfer-amount")
    public ResponseEntity<Transaction> transferAmount(@RequestBody TransferDTO transferDTO){
        return ResponseEntity.ok(service.transferAmount(transferDTO));
    }



}
