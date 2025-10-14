-- Fix ip_created column type from inet to varchar
-- This resolves the Hibernate type mismatch issue

ALTER TABLE refresh_tokens 
ALTER COLUMN ip_created TYPE VARCHAR(45);

-- Add comment for clarity
COMMENT ON COLUMN refresh_tokens.ip_created IS 'IP address of the client that created the token (IPv4 or IPv6)';
