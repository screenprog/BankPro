package com.screenprog.application.for_optimization;

import com.screenprog.application.model.Status;
import java.time.LocalDateTime;

public record AccountDetails(Long accountNumber, Double balance, Status status, LocalDateTime openDate ) {
}
