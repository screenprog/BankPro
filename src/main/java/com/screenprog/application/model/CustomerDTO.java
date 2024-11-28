package com.screenprog.application.model;

import com.screenprog.application.email_service.EmailDTO;

import java.time.LocalDate;

public record CustomerDTO(
        String firstName,
        String lastName,
        String dob,
        String address,
        String password,
        String phoneNumber,
        String email) {
    public Customer toCustomer() {
        return new Customer(this.firstName, this.lastName, LocalDate.parse(this.dob), this.address, this.password, this.phoneNumber, this.email);
    }

}
