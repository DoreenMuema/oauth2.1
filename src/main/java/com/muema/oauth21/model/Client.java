package com.muema.oauth21.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "registered_client")
@Data
@NoArgsConstructor
@AllArgsConstructor

public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String clientId;
    private String clientSecret;
    private String clientAuthenticationMethod;
    private String authorizationGrantType;
    private String scopes;

}
