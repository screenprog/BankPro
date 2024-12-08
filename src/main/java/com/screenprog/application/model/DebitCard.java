package com.screenprog.application.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;


@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class DebitCard {
    @Id
    @SequenceGenerator(
            name = "debit_card_seq",
            sequenceName = "debit_card_seq",
            allocationSize = 1,
            initialValue = 12345
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "debit_card_seq"
    )
    private Long cardNumber;
    @Column(
            columnDefinition = "VARCHAR(3)"
    )
    private String cvv;
    @Column(
            columnDefinition = "VARCHAR(50)"
    )
    private String cardHolderName;
    @Column(
            columnDefinition = "DATE"
    )
    private LocalDate expirationDate;
    private String pin;

    @OneToOne
    @JoinColumn(name = "account_id")
    private Account account;

}

