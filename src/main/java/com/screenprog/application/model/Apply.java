package com.screenprog.application.model;

import jakarta.persistence.*;
import lombok.Setter;

@Entity
public class Apply {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String mobileNumber;
    @Enumerated(EnumType.STRING)
    private Gender gender;
    private String address;
    @Lob
    private byte[] Image;
    @Lob
    private byte[] verificationId;
    @Lob
    private byte[] signatureImage;
    @Setter
    @Enumerated(EnumType.STRING)
    private ApplicationStatus status;

}