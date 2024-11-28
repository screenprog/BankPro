package com.screenprog.application.email_service;

import com.screenprog.application.model.ApplicationDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service //will be used for email verifications
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private OtpService otpService;

    public String sendEmail(EmailDTO emailDTO) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(emailDTO.to());
        message.setSubject(emailDTO.subject());
        message.setText(emailDTO.text());
        mailSender.send(message);
        return "Email sent successfully";
    }

    public String sendOTP(String email) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Your OTP");
        message.setText("Your OTP for email verification  : "
                + otpService.generateOtp(email));
        mailSender.send(message);
        return "Email sent successfully";
    }


}
