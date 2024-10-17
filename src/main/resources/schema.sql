CREATE EXTENSION IF NOT EXISTS "pgcrypto"; -- Required for generating UUIDs in PostgreSQL

CREATE TABLE IF NOT EXISTS oauth2_registered_client (
                                                          id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                                          client_id VARCHAR(255) NOT NULL UNIQUE,
                                                          client_id_issued_at TIMESTAMP,
                                                          client_secret VARCHAR(255),
                                                          client_secret_expires_at TIMESTAMP,
                                                          client_name VARCHAR(255),
                                                          client_authentication_methods TEXT,
                                                          authorization_grant_types TEXT
                                                                          -- Token-related settings (e.g., expiration, token type)
);

DROP TABLE IF EXISTS public.oauth2_registered_client CASCADE;
DROP TABLE IF EXISTS hibernate_migration;
DROP TABLE IF EXISTS flyway_schema_history;

