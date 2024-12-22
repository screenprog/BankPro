package com.screenprog.application.email_service;


import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class OtpService {

    private final Map<String, String> otpStore = new HashMap<>();
    private final Random random = new Random();

    public String generateOtp(String email) {
        String otp = String.format("%06d", random.nextInt(1000000)); // Generate a 6-digit OTP
        if(otpStore.containsKey(email))
            return null;
        otpStore.put(email, otp);
        System.out.println(email + " : " + otpStore.get(email));
        return otp;
    }

    public boolean verifyOtp(String email, String otp) {
        boolean equals = otp.equals(otpStore.get(email));
        System.out.println(email + " : " + equals);
        return equals;
    }

    public String clearOtp(String email) {
        otpStore.remove(email);
        return email;
    }
}
