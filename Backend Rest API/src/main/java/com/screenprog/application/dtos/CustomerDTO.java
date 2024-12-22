package com.screenprog.application.dtos;

import com.screenprog.application.model.Customer;

import java.time.LocalDate;

public record CustomerDTO(
        String firstName,
        String lastName,
        String dob,
        String address,
        String password,
        String phoneNumber,
        String email,
        byte[] image) {
    /*This method  is converting the CustomerDTO object into Customer object
    * to save it into database*/
    public Customer toCustomer() {
        return new Customer(this.firstName, this.lastName, LocalDate.parse(this.dob), this.address, this.password, this.phoneNumber, this.email, image);
    }


}
