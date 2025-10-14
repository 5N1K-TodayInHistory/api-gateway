-- =====================================================
-- V2: Create refresh_tokens table for secure token management
-- =====================================================

CREATE TABLE refresh_tokens (
    id               BIGSERIAL PRIMARY KEY,
    user_id          BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    session_id       UUID   NOT NULL,         -- device/session based
    device_id        TEXT,                    -- optional: client side uuid
    token_hash       TEXT  NOT NULL,          -- SHA-256(base64url(token))
    issued_at        TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    expires_at       TIMESTAMPTZ NOT NULL,
    used             BOOLEAN NOT NULL DEFAULT FALSE,
    revoked          BOOLEAN NOT NULL DEFAULT FALSE,
    rotated_from_id  BIGINT REFERENCES refresh_tokens(id) ON DELETE SET NULL,
    ip_created       VARCHAR(45),             -- IPv4 or IPv6 address
    ua_created       TEXT                     -- User agent string
);

-- =====================================================
-- Create indexes for performance and uniqueness
-- =====================================================

CREATE UNIQUE INDEX uq_refresh_token_hash ON refresh_tokens(token_hash);
CREATE INDEX idx_refresh_user_session ON refresh_tokens(user_id, session_id);
CREATE INDEX idx_refresh_user_active ON refresh_tokens(user_id) WHERE revoked = FALSE AND used = FALSE;
CREATE INDEX idx_refresh_expires ON refresh_tokens(expires_at) WHERE revoked = FALSE;
CREATE INDEX idx_refresh_session_id ON refresh_tokens(session_id);
CREATE INDEX idx_refresh_issued_at ON refresh_tokens(issued_at DESC);

-- =====================================================
-- Add table comments for documentation
-- =====================================================

COMMENT ON TABLE refresh_tokens IS 'Secure refresh token management with rotation and reuse detection';
COMMENT ON COLUMN refresh_tokens.session_id IS 'Unique session identifier for device/session based token management';
COMMENT ON COLUMN refresh_tokens.device_id IS 'Optional client-side device identifier';
COMMENT ON COLUMN refresh_tokens.token_hash IS 'SHA-256 hash of the refresh token for security';
COMMENT ON COLUMN refresh_tokens.used IS 'Whether this token has been used for rotation';
COMMENT ON COLUMN refresh_tokens.revoked IS 'Whether this token has been revoked';
COMMENT ON COLUMN refresh_tokens.rotated_from_id IS 'Reference to the token this was rotated from';
COMMENT ON COLUMN refresh_tokens.ip_created IS 'IP address of the client that created the token';
COMMENT ON COLUMN refresh_tokens.ua_created IS 'User agent string of the client that created the token';
