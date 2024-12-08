package com.screenprog.application.model;

import org.springframework.web.bind.annotation.RequestBody;

public record WithdrawDTO(Long accountNumber, Double balance, String pin) {
}
