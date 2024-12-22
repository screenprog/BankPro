package com.screenprog.application.dtos;


import com.screenprog.application.model.Application;
import com.screenprog.application.model.ApplicationStatus;
import com.screenprog.application.model.Gender;

import java.time.LocalDate;

import static com.screenprog.application.security.BCryptEncryption.encoder;

public record ApplicationDTO(
        String firstName,
        String lastName,
        String email,
        String phone,
        String dob,
        Gender gender,
        String address,
        String password)
{


    /*Converting this DTO into Application object to work store it into database*/
    public Application toApplication() {
       return new Application(null,firstName(), lastName(), email(), phone(), gender(), LocalDate.parse(dob()), address(), encoder.encode(password()), null, null, null, ApplicationStatus.PENDING);
    }
}