package com.muema.oauth21.controllers;


import com.muema.oauth21.model.Client;
import com.muema.oauth21.services.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class HomeController {

    @Autowired
    private ClientService clientService;

    @PostMapping("/register")
    public ResponseEntity<Client> registerClient(@RequestBody Client client) {
        try {
            Client registeredClient = clientService.registerClient(client);
            return new ResponseEntity<>(registeredClient, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/clients")
    public ResponseEntity<?> getClients() {
        // Retrieve the authenticated token from the SecurityContext
        Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // Extract the clientId from the JWT claims
        String clientId = jwt.getSubject(); // Use 'sub' as clientId or use jwt.getClaim("sub")

        // Fetch client details from the database
        Client client = clientService.getClientByClientId(clientId);

        // Return client details as a response
        return ResponseEntity.ok(client);
    }
}
