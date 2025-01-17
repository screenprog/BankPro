package com.screenprog.application.email_service;


import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

@Service
public class OtpService {

    private final Map<String, String> otpStore = new HashMap<>();
    private final Random random = new Random();

    public String generateOtp(String email) {
        String otp = String.format("%06d", random.nextInt(1000000)); // Generate a 6-digit OTP
        if(otpStore.containsKey(email))
            return null;
        otpStore.put(email, otp);
        Executors.newVirtualThreadPerTaskExecutor().submit(() -> otpRemover(email));
        System.out.println(email + " : " + otpStore.get(email));
        return otp;
    }

    public boolean verifyOtp(String email, String otp) {
        boolean equals = otp.equals(otpStore.get(email));
        System.out.println(email + " : " + (equals? "OTP succeed" : "OTP failed" ));
        return equals;
    }

    private void otpRemover(String email) {
        try {
            Thread.sleep(1000 * 60 * 5); //after five minutes otp will be removed: if not used
            clearOtp(email);
        } catch (InterruptedException e) {
            System.out.println("Time interrupted");
        }

    }

    public String clearOtp(String email) {
        otpStore.remove(email);
        return email;
    }
}
