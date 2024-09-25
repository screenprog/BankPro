package com.screenprog.application.model;

import jakarta.persistence.Entity;
import lombok.Data;

import static com.screenprog.application.model.Privilege.STAFF;

@Entity
@Data
public class Staff {
    //TODO: add all the attributes s of staff
    private Long staffId;
    private String firstName;
    private String lastName;
    private String role;
    private final Privilege privilege = STAFF;
}
