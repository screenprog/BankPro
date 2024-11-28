package com.screenprog.application.model;

import java.time.LocalDate;

public record ApplicationDTO(
        String firstName,
        String lastName,
        String dob,
        String email,
        String mobileNumber,
        Gender gender,
        String address)
{
    public Application toApply() {
       return new Application(null,firstName(), lastName(), email(), mobileNumber(), gender(), LocalDate.parse(dob()), address(), null, null, null, ApplicationStatus.PENDING);
    }
}