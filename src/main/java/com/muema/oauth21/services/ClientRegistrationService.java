package com.muema.oauth21.services;

import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;
@Service
public class ClientRegistrationService {
    private final RegisteredClientRepository registeredClientRepository;

    public ClientRegistrationService(RegisteredClientRepository registeredClientRepository) {
        this.registeredClientRepository = registeredClientRepository;
    }

    public Optional<RegisteredClient> findById(String id) {
        return Optional.ofNullable(registeredClientRepository.findById(id));
    }

    public void save(RegisteredClient registeredClient) {
        registeredClientRepository.save(registeredClient);
    }

}
