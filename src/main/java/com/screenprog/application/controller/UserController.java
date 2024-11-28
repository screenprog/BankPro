package com.screenprog.application.controller;

import com.screenprog.application.email_service.EmailService;
import com.screenprog.application.email_service.OtpService;
import com.screenprog.application.model.*;

import com.screenprog.application.service.Service;
import com.screenprog.application.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private Service service;
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

    @PostMapping("register")
    private ResponseEntity<String> applyToBeACustomer(@ModelAttribute ApplicationDTO applyDTO,
                                                      @RequestParam("image") MultipartFile image,
                                                      @RequestParam("card") MultipartFile verificationId,
                                                      @RequestParam("signature") MultipartFile signatureImage){
        try{
            return ResponseEntity.ok(userService.applyCustomer(applyDTO, image, verificationId, signatureImage));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }



    @PostMapping("open-account")
    private ResponseEntity<Account> openAccount(@RequestBody AccountDTO accountDTO){
//        accountDTO = new AccountDTO(accountDTO.customerId(), null, null, accountDTO.type());
//        try {
            return ResponseEntity.ok(userService.openAccount(accountDTO));
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
//        }
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
