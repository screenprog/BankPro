package com.screenprog.application.email_service;

import java.security.SecureRandom;

public record EmailDTO(String to, String subject, String text) {
}
