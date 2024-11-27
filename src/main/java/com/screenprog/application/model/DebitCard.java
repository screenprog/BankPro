package com.screenprog.application.model;

import jakarta.persistence.*;

@Entity
public class DebitCard {
    @Id
    @SequenceGenerator(
            name = "debit_card_seq",
            sequenceName = "debit_card_seq",
            allocationSize = 12345
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
    private String cardHolderName;
    private String expirationDate;

    @OneToOne
    @JoinColumn(name = "account_id")
    private Account account;

}

