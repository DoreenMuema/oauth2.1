CREATE TABLE oauth2_registered_client (
                                          id VARCHAR(255) PRIMARY KEY,
                                          client_id VARCHAR(255) NOT NULL UNIQUE,
                                          client_id_issued_at TIMESTAMP,
                                          client_secret VARCHAR(255),
                                          client_secret_expires_at TIMESTAMP,
                                          client_name VARCHAR(255),
                                          client_authentication_methods TEXT,
                                          authorization_grant_types TEXT,
                                          redirect_uris TEXT,
                                          post_logout_redirect_uris TEXT,
                                          scopes TEXT,
                                          client_settings TEXT,
                                          token_settings TEXT
);
