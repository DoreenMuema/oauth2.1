package com.muema.oauth21.services;

import com.muema.oauth21.model.User;
import com.muema.oauth21.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public User registerUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setClientId(UUID.randomUUID().toString());
        user.setClientSecret(UUID.randomUUID().toString());
        return userRepository.save(user);
    }

    public Optional<User> findByUsername(String username) { // Updated to return Optional<User>
        return userRepository.findByUsername(username);
    }
}