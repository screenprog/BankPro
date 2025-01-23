package com.screenprog.application.for_optimization;

import java.time.LocalDateTime;

public record TransactionDetails(Long transactionId, Double amount, Double balanceLeft, LocalDateTime transactionDate, String description) {
}
