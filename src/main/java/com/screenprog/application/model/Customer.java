package com.screenprog.application.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.screenprog.application.email_service.EmailDTO;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Customer {

    @Id
    @SequenceGenerator(
            name = "customer_seq",
            sequenceName = "customer_seq",
            allocationSize = 1,
            initialValue = 1001
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "customer_seq"
    )
    private Long customerID;

    @Column(
            name = "first_name",
            columnDefinition = "VARCHAR(10)"
    )
    private String firstName;

    @Column(
            name = "last_name",
            columnDefinition = "VARCHAR(10)"
    )
    private String lastName;

    @Column(
            columnDefinition = "DATE"
    )
    private LocalDate dob;

    @Column(
            columnDefinition = "TEXT"
    )
    private String address;

    @Column(
            name = "contact_number",
            columnDefinition = "VARCHAR(15)"
    )
    private String phoneNumber;

    @Column(
            columnDefinition = "VARCHAR(40)",
            unique = true
    )
    private String email;

    private String password;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Account> account;

    @CreatedDate
    @Column(
            updatable = false,
            columnDefinition = "TIMESTAMP WITH TIME ZONE"
    )
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(
            columnDefinition = "TIMESTAMP WITH TIME ZONE"
    )
    private LocalDateTime lastModifiedAt;

    public Customer(String firstName, String lastName, LocalDate dob, String address, String password, String phoneNumber, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.dob = dob;
        this.address = address;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.email = email;
    }

    public Users toUser() {
        return new Users(null,this.customerID.toString(), this.password, List.of("USER"));
    }

    public EmailDTO toApplicationVerifiedEmail() {
        return new EmailDTO(email, "Application Verified",
                String.format("Dear %s, \n\n  %s \n  %s \n  %s \n  %s \n  %s \n  %s \n  %s",firstName,
                        "Congratulations! Your application has been verified and now you are our customer",
                        "You can create your account and start using our services",
                        "Before starting make sure to login and change your default password.",
                        "Here are your login details:",
                        "Customer ID: "+customerID,
                        "Password: "+firstName+"@123",
                        "We look forward to serving you."
                        ));
    }

//    public Users toUser() {
//        return new Users(this.customerID.toString(), this.password, List.of("USER"));
//    }

//    @Column(
//            name = "privileges",
//            columnDefinition = "VARCHAR(20)"
//    )
//    private Privilege privilege = USER;

}
