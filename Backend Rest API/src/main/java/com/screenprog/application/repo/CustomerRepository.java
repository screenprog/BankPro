package com.screenprog.application.repo;

import com.screenprog.application.model.Customer;
import jakarta.persistence.Tuple;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    boolean existsByEmail(String email);

    Customer findByEmail(String email);

    @Query("SELECT customerID, firstName, lastName, dob, email FROM Customer")
    List<Tuple> findCustomerDetailsForFrontend();
}
