package com.screenprog.application.for_optimization;

import java.time.LocalDate;

public record CustomerDetails(Long customerID, String firstName, String lastName, LocalDate dob, String email) {
}
