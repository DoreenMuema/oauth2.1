package com.muema.oauth21.repo;


import com.muema.oauth21.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username); // Updated to Optional<User>
    Optional<User> findByClientId(String clientId); // Updated to Optional<User>
}