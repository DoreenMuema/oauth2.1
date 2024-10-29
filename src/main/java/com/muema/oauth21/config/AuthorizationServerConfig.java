package com.muema.oauth21.config;

import com.muema.oauth21.model.User;
import com.muema.oauth21.repo.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.Nullable;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;

import java.util.Set;

@Configuration
public class AuthorizationServerConfig {

    private final UserRepository userRepository;

    // Constructor for injecting the UserRepository
    public AuthorizationServerConfig(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    // Creates a RegisteredClientRepository that uses JPA for user storage
    @Bean
    public RegisteredClientRepository registeredClientRepository() {
        return new JpaRegisteredClientRepository(userRepository);
    }

    // Custom implementation of RegisteredClientRepository using JPA
    private static class JpaRegisteredClientRepository implements RegisteredClientRepository {

        private final UserRepository userRepository;

        // Constructor for injecting the UserRepository
        public JpaRegisteredClientRepository(UserRepository userRepository) {
            this.userRepository = userRepository;
        }

        // Saves a new RegisteredClient to the database using the User entity
        @Override
        public void save(RegisteredClient registeredClient) {
            User user = new User();
            user.setUsername(registeredClient.getClientId()); // Use clientId as username
            user.setPassword(registeredClient.getClientSecret()); // Store client secret (should be hashed in production)
            // Additional fields can be set based on the RegisteredClient properties if needed

            userRepository.save(user); // Save the user entity to the repository
        }

        // Retrieves a RegisteredClient by its ID
        @Override
        @Nullable
        public RegisteredClient findById(String id) {
            return userRepository.findById(Long.valueOf(id))
                    .map(this::toRegisteredClient) // Map the User entity to RegisteredClient
                    .orElse(null);
        }

        @Override
        @Nullable
        public RegisteredClient findByClientId(String clientId) {
            return userRepository.findByClientId(clientId)
                    .map(this::toRegisteredClient)
                    .orElse(null);
        }


        // Converts a User entity to a RegisteredClient object
        private RegisteredClient toRegisteredClient(User user) {
            return RegisteredClient.withId(String.valueOf(user.getId()))
                    .clientId(user.getUsername()) // Assuming username is used as clientId
                    .clientSecret(user.getPassword())  // Password should be hashed before storing
                    .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_POST)  // Use client_secret_post for authentication
                    .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS) // Set grant type as client_credentials
                    .scopes((java.util.function.Consumer<Set<String>>) Set.of()) // Add any specific scopes if needed
                    .build();
        }
    }
}