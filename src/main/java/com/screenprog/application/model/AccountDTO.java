package com.screenprog.application.model;


import java.time.LocalDate;

public record AccountDTO (
    Long customerId,
    Double balance,
    Status status,
    AccountType type
){}
