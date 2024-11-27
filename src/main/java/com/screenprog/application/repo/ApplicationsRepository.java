package com.screenprog.application.repo;

import com.screenprog.application.model.Apply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApplicationsRepository extends JpaRepository<Apply, Long> {
    @Query(value = "select * from q where q.status = PENDING", nativeQuery = true)
    List<Apply> findAllPendingApplications();
}
