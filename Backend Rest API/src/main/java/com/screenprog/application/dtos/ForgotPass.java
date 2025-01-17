package com.screenprog.application.dtos;


public record ForgotPass(String otp, String newPass, String email) {
}
