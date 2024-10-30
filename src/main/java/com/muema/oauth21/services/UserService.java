package com.muema.oauth21.services;

import com.muema.oauth21.model.User;
import com.muema.oauth21.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;
import java.util.Random;
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

    // Method to generate OTP
    public String generateOtp() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000); // Generates a 6-digit OTP
        return String.valueOf(otp);
    }
    // Method to update user with OTP and expiry
    public void saveOtpForUser(User user) {
        String otp = generateOtp();
        user.setOtp(otp);
        user.setOtpExpiry(new Date(System.currentTimeMillis() + 300000)); // 5 min expiry
        userRepository.save(user);
    }
    // Method to validate OTP
    public boolean validateOtp(User user, String inputOtp) {
        if (user.getOtp().equals(inputOtp) && user.getOtpExpiry().after(new Date())) {
            return true; // OTP is valid and not expired
        }
        return false;
    }
}
