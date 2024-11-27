package com.screenprog.application.controller;

import com.screenprog.application.model.*;

import com.screenprog.application.service.Service;
import com.screenprog.application.service.UserService;
import jakarta.persistence.Access;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private Service service;

    @PostMapping("register")
    private ResponseEntity<String> applyToBeACustomer(@RequestBody Apply apply){
        return ResponseEntity.ok(userService.applyCustomer(apply));
    }

    @PostMapping("open-account")
    private ResponseEntity<String> openAccount(@RequestBody Account account){
        return null;
    }
    @GetMapping("check-balance")
    private ResponseEntity<String> checkBalance(@RequestBody Long accountNumber){
        Double balance = userService.getBalance(accountNumber);
        if(balance == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Account number is incorrect");
        return ResponseEntity.ok(balance.toString());
    }

    @PutMapping("withdraw")// ?accountNumber=*********
    private ResponseEntity<Transaction> withdraw(@RequestParam Long accountNumber, @RequestBody Double balance){
        Transaction transaction = service.withdraw(accountNumber, balance);
        if( transaction == null)
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok(transaction);
    }

    @PutMapping("transfer")
    private ResponseEntity<Transaction> transferAmount(@RequestBody TransferDTO transferDTO){
        Transaction transaction = service.transferAmount(transferDTO);
        if(transaction.getAccountId() == null)
            ResponseEntity.notFound().build();
        return ResponseEntity.ok(transaction);
    }

//    TODO:
//    add functionality to check whether the one who is
//    changing the password is the one who is logged in
    @PostMapping("/change-password")
    private ResponseEntity<Users> changePassword(@RequestBody Users user){
        Users userWithUpdatedPassword = service.changePassword(user);
        if(userWithUpdatedPassword == null)
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok(userWithUpdatedPassword);
    }

    /*
    TODO:
    open account
    */

}
