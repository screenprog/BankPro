//package com.screenprog.application.model;
//
//import jakarta.persistence.*;
//
//
//@Entity
//public non-sealed class CreditCard implements Card{
//    @Id
//    @SequenceGenerator(
//            name = "credit_card_seq",
//            sequenceName = "credit_card_seq",
//            allocationSize = 1,
//            initialValue = 12345
//    )
//    @GeneratedValue(
//            strategy = GenerationType.SEQUENCE,
//            generator = "credit_card_seq"
//    )
//    private Long cardNumber;
//    @Column(
//            columnDefinition = "VARCHAR(3)"
//    )
//    private String cvv;
//    private String cardHolderName;
//    private String expirationDate;
//
//    @OneToOne
//    @JoinColumn(name = "account_id")
//    private Account account;
//
//}
//
