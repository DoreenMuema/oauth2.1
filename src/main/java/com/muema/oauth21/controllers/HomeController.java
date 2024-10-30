package com.muema.oauth21.controllers;


import com.muema.oauth21.model.User;
import com.muema.oauth21.services.UserService;
import com.muema.oauth21.util.JwtTokenService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class HomeController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtTokenService jwtTokenService;

    @PostMapping("/register")
    public User register(@RequestBody User user) {
        return userService.registerUser(user);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestHeader("clientId") String clientId,
            @RequestHeader("clientSecret") String clientSecret,
            @RequestBody User user) {

        Optional<User> optionalUser = userService.findByUsername(user.getUsername());

        if (optionalUser.isPresent()) {
            User existingUser = optionalUser.get();

            if (clientId.equals(existingUser.getClientId()) &&
                    clientSecret.equals(existingUser.getClientSecret()) &&
                    new BCryptPasswordEncoder().matches(user.getPassword(), existingUser.getPassword())) {

                // Generate OTP and store it for the user
                userService.saveOtpForUser(existingUser);

                // Return the OTP in the response for testing purposes (you can handle OTP delivery later)
                return ResponseEntity.ok(Map.of(
                        "message", "OTP has been generated. Please verify.",
                        "otp", existingUser.getOtp()  // Only for testing, remove in production
                ));
            }
        }

        return ResponseEntity.status(401).body("Invalid credentials");
    }

    @PostMapping("/validate-otp")
    public ResponseEntity<?> validateOtp(@RequestBody Map<String, String> otpRequest) {
        String username = otpRequest.get("username");
        String otp = otpRequest.get("otp");

        Optional<User> optionalUser = userService.findByUsername(username);

        if (optionalUser.isPresent()) {
            User existingUser = optionalUser.get();

            // Validate the OTP
            if (userService.validateOtp(existingUser, otp)) {
                // Generate token with JwtTokenService after OTP is verified
                String token = jwtTokenService.generateToken(
                        existingUser.getClientId(),
                        existingUser.getUsername(),
                        existingUser.getFirstName(),
                        existingUser.getLastName(),
                        existingUser.getEmail()
                );

                Date expiry = new Date(System.currentTimeMillis() + JwtTokenService.EXPIRATION_TIME);

                return ResponseEntity.ok(Map.of(
                        "id", existingUser.getId(),
                        "firstName", existingUser.getFirstName(),
                        "lastName", existingUser.getLastName(),
                        "username", existingUser.getUsername(),
                        "email", existingUser.getEmail(),
                        "token", token,
                        "expiry", expiry
                ));
            }
            return ResponseEntity.status(403).body("Invalid or expired OTP.");
        }

        return ResponseEntity.status(404).body("User not found.");
    }

    @GetMapping("/clients")
    public ResponseEntity<?> getClients(HttpServletRequest request) {
        String header = request.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7); // Extract the token

            // Validate the token
            if (jwtTokenService.validateToken(token)) {
                return ResponseEntity.ok("This is the protected resource gained by access token.");
            } else {
                return ResponseEntity.status(403).body("Invalid or expired token.");
            }
        }

        return ResponseEntity.status(400).body("Authorization header must be provided in the 'Bearer' format.");
    }


}