CREATE TABLE oauth2_registered_client (
                                          id UUID NOT NULL DEFAULT gen_random_uuid(),  -- Use UUID data type and auto-generate
                                          client_id VARCHAR(255) NOT NULL UNIQUE,      -- Client ID (unique)
                                          client_id_issued_at TIMESTAMP,               -- Timestamp for when the client ID was issued
                                          client_secret VARCHAR(255),                   -- Client secret
                                          client_secret_expires_at TIMESTAMP,          -- Timestamp for when the client secret expires
                                          client_name VARCHAR(255),                     -- Client name
                                          client_authentication_methods VARCHAR(255),   -- Authentication methods
                                          authorization_grant_types VARCHAR(255),      -- Authorization grant types
                                          PRIMARY KEY (id)                              -- Primary key
);
