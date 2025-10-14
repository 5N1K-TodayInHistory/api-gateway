-- Create refresh_tokens table for secure token management
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
    ip_created       INET,
    ua_created       TEXT
);

-- Create indexes for performance and uniqueness
CREATE UNIQUE INDEX uq_refresh_token_hash ON refresh_tokens(token_hash);
CREATE INDEX idx_refresh_user_session ON refresh_tokens(user_id, session_id);
CREATE INDEX idx_refresh_user_active ON refresh_tokens(user_id) WHERE revoked = FALSE AND used = FALSE;
CREATE INDEX idx_refresh_expires ON refresh_tokens(expires_at) WHERE revoked = FALSE;
