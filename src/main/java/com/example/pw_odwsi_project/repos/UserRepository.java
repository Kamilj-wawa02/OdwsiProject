package com.example.pw_odwsi_project.repos;

import com.example.pw_odwsi_project.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByUsernameIgnoreCase(String username);
    boolean existsByEmailIgnoreCase(String email);
    User findByEmailIgnoreCase(String email);
    User findByUsernameIgnoreCase(String username);
}
