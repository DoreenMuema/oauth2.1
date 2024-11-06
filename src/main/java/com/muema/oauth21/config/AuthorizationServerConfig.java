package com.muema.oauth21.config;

import com.muema.oauth21.model.Client;
import com.muema.oauth21.repo.ClientRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;

import java.util.Set;
import java.util.UUID;

@Configuration
public class AuthorizationServerConfig {

    private final ClientRepository clientRepository;

    public AuthorizationServerConfig(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @Bean
    public RegisteredClientRepository registeredClientRepository() {
        return new JpaRegisteredClientRepository(clientRepository);
    }

    private static class JpaRegisteredClientRepository implements RegisteredClientRepository {

        private final ClientRepository clientRepository;

        public JpaRegisteredClientRepository(ClientRepository clientRepository) {
            this.clientRepository = clientRepository;
        }

        @Override
        public void save(RegisteredClient registeredClient) {
            Client client = new Client();
            client.setClientId(registeredClient.getClientId());
            client.setClientSecret(registeredClient.getClientSecret());
            client.setRedirectUri("http://localhost:8080/callback"); // Update as needed
            client.setScope("read write"); // Add required scopes
            clientRepository.save(client);
        }

        @Override
        public RegisteredClient findById(String id) {
            return clientRepository.findById(UUID.fromString(id))
                    .map(this::toRegisteredClient)
                    .orElse(null);
        }

        @Override
        public RegisteredClient findByClientId(String clientId) {
            return clientRepository.findByClientId(clientId)
                    .map(this::toRegisteredClient)
                    .orElse(null);
        }

        private RegisteredClient toRegisteredClient(Client client) {
            return RegisteredClient.withId(String.valueOf(client.getId()))
                    .clientId(client.getClientId())
                    .clientSecret(client.getClientSecret())
                    .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                    .redirectUri(client.getRedirectUri())
                    .scopes((java.util.function.Consumer<Set<String>>) Set.of(client.getScope().split(" ")))
                    .build();
        }
    }
}