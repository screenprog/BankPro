package com.screenprog.application.controller;

import com.screenprog.application.email_service.EmailService;
import com.screenprog.application.email_service.OtpService;
import com.screenprog.application.model.*;
import com.screenprog.application.service.CenteralisedService;
import com.screenprog.application.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private CenteralisedService service;
    @Autowired
    private EmailService emailService;
    @Autowired
    private OtpService otpService;

    @PostMapping("email")
    private ResponseEntity<String> email(@RequestParam String email){
        return ResponseEntity.ok(emailService.sendOTP(email));
    }

    @PostMapping("verify-email")
    private ResponseEntity<String> verifyOTP(@RequestParam("email") String email,@RequestParam("otp") String otp){
        if(otpService.verifyOtp(email, otp))
            return ResponseEntity.ok(otpService.clearOtp(email));
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Invalid OTP");
    }

    /*DONE*/
    @PostMapping("register")
    private ResponseEntity<String> applyToBeACustomer(@ModelAttribute ApplicationDTO applicationDTO,
                                                      @RequestParam("image") MultipartFile image,
                                                      @RequestParam("card") MultipartFile verificationId,
                                                      @RequestParam("signature") MultipartFile signatureImage){
        try{
            return ResponseEntity.ok(userService.applyCustomer(applicationDTO, image, verificationId, signatureImage));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    /*TODO: Test this end point*/
    @PostMapping("open-account")
    private ResponseEntity<Account> openAccount(@RequestBody AccountDTO accountDTO){
            return ResponseEntity.ok(userService.openAccount(accountDTO));
    }

    @GetMapping("check-balance")
    private ResponseEntity<String> checkBalance(@RequestParam Long accountNumber){
        Double balance = userService.getBalance(accountNumber);
        if(balance == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Account number is incorrect");
        return ResponseEntity.ok(balance.toString());
    }

    /*TODO: This end-point might get suspension */
    @PutMapping("withdraw")
    private ResponseEntity<Transaction> withdraw(@RequestBody WithdrawDTO withdrawDTO){
        Transaction transaction = userService.withdraw(withdrawDTO);
        if( transaction == null)
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok(transaction);
    }

    /*TODO: Test this end point*/
    /*TODO: This end-point might get suspension*/
    @PostMapping("deposit")
    public ResponseEntity<String> deposit(@RequestBody WithdrawDTO withdrawDTO){
        try{
            Transaction transaction = userService.deposit(withdrawDTO);
            if(transaction == null)
                return ResponseEntity.notFound().build();
            return ResponseEntity.ok("Deposit Successful");
        } catch (IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (RuntimeException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    /*TODO: Test this endpoint and it's functionality*/
    @PutMapping("transfer")
    private ResponseEntity<Transaction> transferAmount(@RequestBody TransferDTO transferDTO){
        Transaction transaction = userService.transferAmount(transferDTO);
        if(transaction.getAccountId() == null)
            ResponseEntity.notFound().build();
        return ResponseEntity.ok(transaction);
    }

    /*TODO: Add functionality to check whether the one who is
     * changing the password is the one who is logged in*/
    @PostMapping("change-password")
    private ResponseEntity<Users> changePassword(@RequestBody Users user){
        Users userWithUpdatedPassword = service.changePassword(user);
        if(userWithUpdatedPassword == null)
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok(userWithUpdatedPassword);
    }

    /*TODO: Test this end point*/
    @GetMapping("transaction-history")// ?accountNumber=************
    private ResponseEntity<List<Transaction>> transactions(@RequestParam Long accountNumber){
        return ResponseEntity.ok(userService.getTransactionHistory(accountNumber));
    }

}
