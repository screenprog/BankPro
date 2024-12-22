package com.screenprog.application.dtos;

import com.screenprog.application.model.AccountType;
import com.screenprog.application.model.Status;

public record AccountDTO (
    Long customerId,
    Double balance,
    Status status,
    AccountType type,
    Integer pin
){}
