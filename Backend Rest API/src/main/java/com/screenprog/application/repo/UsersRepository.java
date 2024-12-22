package com.screenprog.application.repo;

import com.screenprog.application.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsersRepository extends JpaRepository<Users, Long> {
    Users findByUsername(String username);

    boolean existsByUsername(String username);
}
