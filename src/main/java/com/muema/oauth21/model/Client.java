package com.muema.oauth21.model;

import jakarta.persistence.*;
import lombok.*;



@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "clients")
@Getter
@Setter
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String clientId;

    @Column(nullable = false)
    private String clientSecret;

    @Column(name = "code_verifier")
    private String codeVerifier;

    @Column(name = "code_challenge")
    private String codeChallenge;

    @Column(name = "redirect_Uri")
    private String redirectUri;

    @Column(name = "scope")
    private String scope;

    @Column(name = "authorization_code")
    private String authorizationCode;

    @Column( name = "mfa_secret") // Mark as nullable if you want it optional for existing clients
    private String mfaSecret;

}
