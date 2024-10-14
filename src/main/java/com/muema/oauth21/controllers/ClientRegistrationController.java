package com.muema.oauth21.controllers;

import com.muema.oauth21.services.ClientRegistrationDto;
import com.muema.oauth21.services.ClientRegistrationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/clients")
public class ClientRegistrationController {

    private final ClientRegistrationService clientRegistrationService;

    public ClientRegistrationController(ClientRegistrationService clientRegistrationService) {
        this.clientRegistrationService = clientRegistrationService;
    }

    @PostMapping
    public ResponseEntity<RegisteredClient> registerClient(@RequestBody ClientRegistrationDto registrationDto) {
        // Create RegisteredClient from DTO
        RegisteredClient registeredClient = RegisteredClient.withId(UUID.randomUUID().toString())
                .clientId(registrationDto.getClientId())
                .clientSecret(registrationDto.getClientSecret())
                .clientAuthenticationMethod(new ClientAuthenticationMethod(registrationDto.getClientAuthenticationMethod()))
                .authorizationGrantType(new AuthorizationGrantType(registrationDto.getAuthorizationGrantType()))
                .scopes(scopes -> scopes.addAll(registrationDto.getScopes()))
                .build();

        clientRegistrationService.save(registeredClient);
        return ResponseEntity.ok(registeredClient);
    }
}
