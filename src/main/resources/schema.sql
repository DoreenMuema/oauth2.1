CREATE TABLE registered_client (
                                   id SERIAL PRIMARY KEY,
                                   client_id VARCHAR(255) NOT NULL UNIQUE,
                                   client_secret VARCHAR(255) NOT NULL,
                                   client_authentication_method VARCHAR(255) NOT NULL,
                                   authorization_grant_type VARCHAR(255) NOT NULL,
                                   scopes VARCHAR(255)
);
CREATE TABLE otp (
                     client_id VARCHAR(255) PRIMARY KEY NOT NULL UNIQUE,
                     otp VARCHAR(255),
                     otp_expiration TIMESTAMP
);
ALTER TABLE users1
    DROP COLUMN otp,
    DROP COLUMN otp_expiration;