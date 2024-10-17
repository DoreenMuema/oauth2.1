package com.muema.oauth21.services;

import com.muema.oauth21.model.OAuth2RegisteredClientDTO;
import com.muema.oauth21.repo.RegisteredClientRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ClientRegistrationService {
    private final RegisteredClientRepository registeredClientRepository;

    public ClientRegistrationService(RegisteredClientRepository registeredClientRepository) {
        this.registeredClientRepository = registeredClientRepository;
    }

    public Optional<OAuth2RegisteredClientDTO> findById(UUID clientId) {
        return registeredClientRepository.findById( clientId);
    }

    public void save(OAuth2RegisteredClientDTO clientRegistration) {
        registeredClientRepository.save(clientRegistration);
    }

    public List<OAuth2RegisteredClientDTO> getAllClients() {
        return (List<OAuth2RegisteredClientDTO>) registeredClientRepository.findAll();
    }
}
