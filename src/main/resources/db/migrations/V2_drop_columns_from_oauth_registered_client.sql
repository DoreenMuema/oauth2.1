
ALTER TABLE oauth2_registered_client
DROP COLUMN IF EXISTS redirect_uris,
DROP COLUMN IF EXISTS post_logout_redirect_uris,
DROP COLUMN IF EXISTS scopes,
DROP COLUMN IF EXISTS client_settings,
DROP COLUMN IF EXISTS token_settings;
