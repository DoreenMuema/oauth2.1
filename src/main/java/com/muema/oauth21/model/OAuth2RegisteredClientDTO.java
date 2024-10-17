package com.muema.oauth21.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "oauth2_registered_client")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OAuth2RegisteredClientDTO {

    @Id
    @Column(columnDefinition = "UUID")
    private UUID id = UUID.randomUUID(); // Initialize UUID

    @Column(name = "client_id", nullable = false, unique = true)
    private String clientId;

    @Column(name = "client_id_issued_at")
    private Instant clientIdIssuedAt;

    @Column(name = "client_secret")
    private String clientSecret;

    @Column(name = "client_secret_expires_at")
    private Instant clientSecretExpiresAt;

    @Column(name = "client_name")
    private String clientName;

    @Column(name = "client_authentication_methods")
    private String clientAuthenticationMethods;

    @Column(name = "authorization_grant_types")
    private String authorizationGrantTypes;


}
