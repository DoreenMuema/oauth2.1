package com.muema.oauth21.config;



import com.muema.oauth21.model.Client;
import com.muema.oauth21.repo.ClientRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.Nullable;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Set;

@Configuration
public class AuthorizationServerConfig {

    private final ClientRepository clientRepository;

    // Constructor for injecting the ClientRepository
    public AuthorizationServerConfig(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    // Configures the security filter chain for the authorization server
    @Bean
    public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http) throws Exception {
        // Apply default security settings for the OAuth2 authorization server
        OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http);
        // Enable JWT authentication for the resource server
        http.oauth2ResourceServer().jwt();
        return http.build();
    }

    // Defines the settings for the authorization server, including the issuer URL
    @Bean
    public AuthorizationServerSettings authorizationServerSettings() {
        return AuthorizationServerSettings.builder()
                .issuer("http://localhost:8080/oauth2/token") // Set the issuer URL for tokens
                .build();
    }

    // Creates a RegisteredClientRepository that uses JPA for client storage
    @Bean
    public RegisteredClientRepository registeredClientRepository() {
        return new JpaRegisteredClientRepository(clientRepository);
    }

    // Custom implementation of RegisteredClientRepository using JPA
    private static class JpaRegisteredClientRepository implements RegisteredClientRepository {

        private final ClientRepository clientRepository;

        // Constructor for injecting the ClientRepository
        public JpaRegisteredClientRepository(ClientRepository clientRepository) {
            this.clientRepository = clientRepository;
        }

        // Saves a new RegisteredClient to the database
        @Override
        public void save(RegisteredClient registeredClient) {
            Client client = new Client();
            client.setClientId(registeredClient.getClientId());
            client.setClientSecret(registeredClient.getClientSecret()); // Store client secret (should be hashed in production)
            client.setClientAuthenticationMethod(registeredClient.getClientAuthenticationMethods().toString());
            client.setAuthorizationGrantType(registeredClient.getAuthorizationGrantTypes().stream().findFirst().orElse(null).getValue());
            client.setScopes(String.join(",", registeredClient.getScopes())); // Convert set of scopes to a comma-separated string

            clientRepository.save(client); // Save the client entity to the repository
        }

        // Retrieves a RegisteredClient by its ID
        @Override
        @Nullable
        public RegisteredClient findById(String id) {
            return clientRepository.findById(Long.valueOf(id))
                    .map(this::toRegisteredClient) // Map the Client entity to RegisteredClient
                    .orElse(null);
        }

        // Retrieves a RegisteredClient by its client ID
        @Override
        @Nullable
        public RegisteredClient findByClientId(String clientId) {
            return clientRepository.findByClientId(clientId)
                    .map(this::toRegisteredClient) // Map the Client entity to RegisteredClient
                    .orElse(null);
        }

        // Converts a Client entity to a RegisteredClient object
        private RegisteredClient toRegisteredClient(Client client) {
            return RegisteredClient.withId(String.valueOf(client.getId()))
                    .clientId(client.getClientId())
                    .clientSecret(client.getClientSecret())  // client secret should be hashed before storing
                    .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_POST)  // Use client_secret_post for authentication
                    .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS) // Set grant type as client_credentials
                    .scopes(scopes -> scopes.addAll(Set.of(client.getScopes().split(",")))) // Set scopes from comma-separated string
                    .build();
        }
    }
}
