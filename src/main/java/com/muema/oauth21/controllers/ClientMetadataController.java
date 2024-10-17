package com.muema.oauth21.controllers;


import com.muema.oauth21.model.OAuth2RegisteredClientDTO;
import com.muema.oauth21.services.ClientRegistrationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/client")
public class ClientMetadataController {

    private final ClientRegistrationService clientRegistrationService;

    public ClientMetadataController(ClientRegistrationService clientRegistrationService) {
        this.clientRegistrationService = clientRegistrationService;
    }

    // Endpoint to fetch all clients
    @GetMapping("/all")
    public ResponseEntity<List<RegisteredClient>> getAllClients() {
        List<OAuth2RegisteredClientDTO> clientDTOs = clientRegistrationService.getAllClients();

        // Convert DTOs to RegisteredClients
        List<RegisteredClient> clients = clientDTOs.stream().map(dto ->
                RegisteredClient.withId(dto.getClientId())
                        .clientId(dto.getClientId())
                        .clientSecret("{noop}" + dto.getClientSecret())
                        .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                        .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                        .build()
        ).collect(Collectors.toList());

        return ResponseEntity.ok(clients); // Return the converted clients
    }


    //@GetMapping("/claims")
    //public ResponseEntity<RegisteredClient> getClientMetadata() {
   //    return ResponseEntity.status(HttpStatus.ACCEPTED).body(null);
//        if (jwt == null) {
//            System.out.println("JWT is null");
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
//        }
//
//        System.out.println("JWT Claims: " + jwt.getClaims());
//
//        String clientId = jwt.getClaimAsString("client_id");
//        System.out.println("Client ID: " + clientId);
//
//        if (clientId == null) {
//            System.out.println("Client ID is missing");
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null); // 400 if client_id not present
//        }
//
//        Optional<RegisteredClient> client = clientRegistrationService.findById(clientId);
//        if (client.isPresent()) {
//            return ResponseEntity.ok(client.get());
//        } else {
//            System.out.println("Client not found for ID: " + clientId);
//            return ResponseEntity.notFound().build(); // 404 if client not found
//        }
    }


