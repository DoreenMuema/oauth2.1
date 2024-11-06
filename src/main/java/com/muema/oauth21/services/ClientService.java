package com.muema.oauth21.services;

import com.muema.oauth21.model.Client;
import com.muema.oauth21.repo.ClientRepository;
import com.muema.oauth21.util.PKCEUtils;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.util.Optional;


@Service
public class ClientService {

    private final ClientRepository clientRepository;

    @Autowired
    public ClientService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    public Optional<Client> findClientById(String clientId) {
        return clientRepository.findByClientId(clientId);
    }

    public Client saveClient(Client client) throws NoSuchAlgorithmException {

        String codeVerifier = PKCEUtils.generateCodeVerifier();
        String codeChallenge = PKCEUtils.generateCodeChallenge(codeVerifier);
        client.setCodeChallenge(codeChallenge);
        client.setCodeVerifier(codeVerifier);
        return clientRepository.save(client);
    }
    public Client updateClient(Client client) throws NoSuchAlgorithmException {
        return clientRepository.save(client);
    }


}
