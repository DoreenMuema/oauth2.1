package com.muema.oauth21.services;


import com.muema.oauth21.model.Client;
import com.muema.oauth21.repo.ClientRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class ClientService {

    private final ClientRepository clientRepository;
    private final PasswordEncoder passwordEncoder;  // Inject PasswordEncoder

    public ClientService(ClientRepository clientRepository, PasswordEncoder passwordEncoder) {
        this.clientRepository = clientRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Client registerClient(Client client) {
        if (clientRepository.findByClientId(client.getClientId()).isPresent()) {
            throw new IllegalArgumentException("Client ID already registered");
        }

        // Encode client secret before saving to the database
        client.setClientSecret(passwordEncoder.encode(client.getClientSecret()));
        return clientRepository.save(client);
    }


    public Client getClientByClientId(String clientId) {
        return clientRepository.findByClientId(clientId).orElse(null);
    }
}
