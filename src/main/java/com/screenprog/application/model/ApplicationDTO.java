package com.screenprog.application.model;


import java.time.LocalDate;

import static com.screenprog.application.security.BCryptEncryption.encoder;

public record ApplicationDTO(
        String firstName,
        String lastName,
        String dob,
        String email,
        String mobileNumber,
        Gender gender,
        String address,
        String password)
{


    /*Converting this DTO into Application object to work store it into database*/
    public Application toApplication() {
       return new Application(null,firstName(), lastName(), email(), mobileNumber(), gender(), LocalDate.parse(dob()), address(), encoder.encode(password()), null, null, null, ApplicationStatus.PENDING);
    }
}