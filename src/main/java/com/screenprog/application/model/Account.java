package com.screenprog.application.model;


import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;


@Entity
@Data
public class Account {


    @Id
    @SequenceGenerator(
            name = "account_sequence",
            sequenceName = "account_sequence",
            initialValue = 1001,
            allocationSize = 1
    )

    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "account_sequence"
    )
    private Long accountNumber;

    @ManyToOne
    @JoinColumn(
            name = "customer_id"
    )

    @JsonBackReference
    private Customer customer;

    private Double balance;

    @CreationTimestamp
    @Column(
          updatable = false
    )
    private LocalDate openDate;

    @Column(columnDefinition = "VARCHAR(10)")
    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(columnDefinition = "VARCHAR(10)")
    @Enumerated(EnumType.STRING)
    private AccountType type;
}

enum AccountType{
    CURRENT, SAVING
}
enum Status {
    ACTIVE, CLOSED, SUSPENDED, PENDING
}
