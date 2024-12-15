package com.screenprog.application.model;

import com.screenprog.application.email_service.EmailDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

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

    /*This method is converting the Application object into CustomerDTO
    * because addCustomer() method in CentralizedService requires CustomerDTO
    * object to function and register a customer */
    public CustomerDTO toCustomerDTO() {
        return new CustomerDTO(firstName, lastName, dob.toString(),
                address,password, mobileNumber, email, image);
    }

    public EmailDTO toApplicationReceivedEmailDTO() {
        return new EmailDTO(email, "Application Received",
                String.format(
                        "Dear %s, \n\n  %s \n  %s \n  %s \n  %s \n  %s",
                        firstName,
                        "Thank you for applying in our bank",
                        "Your application has been received and it will be reviewed",
                        "in the next upcoming days till then stay tuned.",
                        "Best regards",
                        "NOTE: This is an automated email"));
    }

    public EmailDTO toApplicationNotVerifiedEmail() {
        return new EmailDTO(email, "Application Rejected",
                String.format("Dear %s, \n\n  %s \n  %s \n  %s  \n  %s  \n  %s",firstName,
                        "Unfortunately your application has rejected and you are not verified",
                        "If you want to verify your application please visit our branch",
                        "Make sure to bring your any government approved id",
                        "We look forward to serving you.",
                        "Bank Staff"
                ));
    }
}