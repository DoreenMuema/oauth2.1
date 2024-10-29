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

                // Generate token with JwtTokenService, including additional user info
                String token = jwtTokenService.generateToken(
                        clientId,
                        existingUser.getUsername(),
                        existingUser.getFirstName(),
                        existingUser.getLastName(),
                        existingUser.getEmail()
                );

                Date expiry = new Date(System.currentTimeMillis() + JwtTokenService.EXPIRATION_TIME);

                // Directly return the response as a map
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
        }

        return ResponseEntity.status(401).body("Invalid credentials");
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