package com.screenprog.application.model;

public record AccountDTO (
    Long customerId,
    Double balance,
    Status status,
    AccountType type,
    Integer pin
){}
