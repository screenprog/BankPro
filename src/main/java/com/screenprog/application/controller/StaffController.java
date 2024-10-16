package com.screenprog.application.controller;

import com.screenprog.application.service.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/staff")
public class StaffController {
    final private Service service;

    @Autowired
    public StaffController(Service service) {
        this.service = service;
    }

    @PostMapping("deposit/{accountId}")
    public ResponseEntity<String> deposit(@PathVariable Long accountId, @RequestParam Double amount){
        try{
            service.deposit(accountId, amount);
            return ResponseEntity.ok("Deposit Successful");
        } catch (IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (RuntimeException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("withdraw/{accountId}")
    public ResponseEntity<String> withdraw(@PathVariable Long accountId, @RequestParam Double amount){
        try {
            service.withdraw(accountId, amount);
            return ResponseEntity.ok("Withdrawal Successful");
        } catch (IllegalArgumentException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (RuntimeException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
