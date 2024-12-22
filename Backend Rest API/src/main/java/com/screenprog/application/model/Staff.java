package com.screenprog.application.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;




@Entity
@Data
public class Staff {
    //TODO: add all the attributes of staff
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long staffId;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String address;
    private String password;
    private List<String> roles;

    public Users toUser() {
        System.out.println("In toUser()");
        return new Users(null,email, password, roles);
    }
}
