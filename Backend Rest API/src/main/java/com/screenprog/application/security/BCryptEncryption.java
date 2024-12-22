package com.screenprog.application.security;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class BCryptEncryption {
    public static BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);
}
