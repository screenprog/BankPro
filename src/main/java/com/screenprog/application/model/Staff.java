package com.screenprog.application.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

import static com.screenprog.application.model.Privilege.STAFF;

@Entity
@Data
public class Staff {
    //TODO: add all the attributes of staff
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long staffId;
    private String firstName;
    private String lastName;
    private String role;
    private final Privilege privilege = STAFF;
}
