package com.screenprog.application.repo;

import com.screenprog.application.model.Application;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApplicationsRepository extends JpaRepository<Application, Long> {
//    @Query(value = "SELECT * FROM apply WHERE apply.status = 'PENDING'", nativeQuery = true)
    /**
     * This method queries the database to fetch all applications
     * that have a status of 'PENDING'. These applications represent requests
     * that have not yet been processed or approved.
     * */
    @Query("SELECT a FROM Application a WHERE a.status = 'PENDING'")
    List<Application> findAllPendingApplications();


}
