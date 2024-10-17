package com.muema.oauth21.controllers;

import com.muema.oauth21.model.OAuth2RegisteredClientDTO;
import com.muema.oauth21.services.ClientRegistrationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;

import java.util.UUID;

@RestController
@RequestMapping("/register")
public class ClientRegistrationController {

    private final ClientRegistrationService clientRegistrationService;

    public ClientRegistrationController(ClientRegistrationService clientRegistrationService) {
        this.clientRegistrationService = clientRegistrationService;
    }

    @PostMapping
    public ResponseEntity<RegisteredClient> registerClient(@RequestBody OAuth2RegisteredClientDTO registrationDto) {
        // Create a RegisteredClient from the DTO
        RegisteredClient registeredClient = RegisteredClient.withId(UUID.randomUUID().toString())
                .clientId(registrationDto.getClientId())
                .clientSecret("{noop}" + registrationDto.getClientSecret())  // Use {noop} for plaintext
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                .build();

        // Save the DTO using the service
        clientRegistrationService.save(registrationDto);  // Use registrationDto here
        return ResponseEntity.status(HttpStatus.CREATED).body(registeredClient);
    }
}
