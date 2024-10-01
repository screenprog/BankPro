package com.screenprog.application.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import static com.screenprog.application.model.Privilege.USER;

@Data
@Entity
@Component
public class Customer {

    @Id
    @SequenceGenerator(
            name = "customer_seq",
            sequenceName = "customer_seq",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "customer_seq"
    )
//    @Column(name = "customer_id")
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

    @Column(
            columnDefinition = "VARCHAR(50)"
    )
    private String password;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Account> account;

    @CreatedDate
    @Column(
            updatable = false
    )
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime lastModifiedAt;

//    @Column(
//            name = "privileges",
//            columnDefinition = "VARCHAR(20)"
//    )
//    private Privilege privilege = USER;

}
