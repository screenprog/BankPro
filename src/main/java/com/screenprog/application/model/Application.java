package com.screenprog.application.model;

import com.screenprog.application.email_service.EmailDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Application {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String mobileNumber;
    @Enumerated(EnumType.STRING)
    private Gender gender;
    private LocalDate dob;
    private String address;
    private String password;
    @Lob
    private byte[] image;
    @Lob
    private byte[] verificationId;
    @Lob
    private byte[] signatureImage;
    @Setter
    @Enumerated(EnumType.STRING)
    private ApplicationStatus status;

    public CustomerDTO toCustomerDTO() {
        return new CustomerDTO(firstName, lastName, dob.toString(),
                address,password, mobileNumber, email );
    }

    public EmailDTO toApplicationReceivedEmailDTO() {
        return new EmailDTO(email, "Application Received",
                String.format(
                        "Dear %s, \n  Thank you for applying. \n  %s %s \n  %s,\n  %s",
                        firstName,
                        "Your application has been received and",
                        "it will be reviewed in the next few days till then stay tuned.",
                        "Best regards",
                        "NOTE: This is an automated email"));
    }

}