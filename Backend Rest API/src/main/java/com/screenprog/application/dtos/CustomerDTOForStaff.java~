package com.screenprog.application.model;

import java.time.LocalDate;

public record CustomerDTOForStaff(
        String firstName,
        String lastName,
        String dob,
        String email,
        String phoneNumber,
        String address,
        String password) {
    /*This method  is converting the CustomerDTO object into Customer object
     * to save it into database*/
    public Customer toCustomer() {
        return new Customer(this.firstName, this.lastName, LocalDate.parse(this.dob), this.address, this.password, this.phoneNumber, this.email, null);
    }

}