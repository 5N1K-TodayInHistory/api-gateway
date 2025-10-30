-- =====================================================
-- V1: Create Basic Tables
-- =====================================================

-- =====================================================
-- 1. USERS TABLE
-- =====================================================

CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) UNIQUE,
    username VARCHAR(255) UNIQUE NOT NULL,
    auth_provider VARCHAR(20) NOT NULL,
    password_hash VARCHAR(255),
    display_name VARCHAR(255),
    avatar_url VARCHAR(500),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    preferences JSONB DEFAULT '{
        "viewMode": "GRID",
        "language": "en",
        "notifications": {
            "email": true,
            "push": true,
            "sms": false
        },
        "privacy": {
            "profileVisibility": "PUBLIC",
            "showEmail": false,
            "showLocation": false
        }
    }'::jsonb,
    devices JSONB DEFAULT '[]'::jsonb,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- =====================================================
-- 2. REFRESH TOKENS TABLE
-- =====================================================

CREATE TABLE refresh_tokens (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    session_id UUID NOT NULL,
    device_id VARCHAR(255),
    token_hash VARCHAR(255) NOT NULL UNIQUE,
    issued_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    expires_at TIMESTAMPTZ NOT NULL,
    used BOOLEAN NOT NULL DEFAULT FALSE,
    revoked BOOLEAN NOT NULL DEFAULT FALSE,
    rotated_from_id BIGINT REFERENCES refresh_tokens(id),
    ip_created VARCHAR(45),
    ua_created TEXT
);

-- =====================================================
-- 3. COUNTRIES TABLE
-- =====================================================

CREATE TABLE countries (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(3) NOT NULL UNIQUE,
    name JSONB NOT NULL,
    flag_url VARCHAR(500),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- =====================================================
-- 4. LANGUAGES TABLE
-- =====================================================

CREATE TABLE languages (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(5) NOT NULL UNIQUE,
    name JSONB NOT NULL,
    native_name VARCHAR(100),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- =====================================================
-- 5. EVENT TYPES TABLE
-- =====================================================

CREATE TABLE event_types (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(20) NOT NULL UNIQUE,
    name JSONB NOT NULL,
    description JSONB,
    icon_url VARCHAR(500),
    color VARCHAR(7),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- =====================================================
-- 6. EVENTS TABLE
-- =====================================================

CREATE TABLE events (
    id BIGSERIAL PRIMARY KEY,
    title JSONB NOT NULL,
    description JSONB NOT NULL,
    content JSONB NOT NULL,
    date TIMESTAMPTZ NOT NULL,
    type VARCHAR(20) NOT NULL,
    country VARCHAR(3) NOT NULL,
    image_url VARCHAR(500),
    video_urls JSONB DEFAULT '[]'::jsonb,
    audio_urls JSONB DEFAULT '[]'::jsonb,
    likes_count INTEGER DEFAULT 0,
    comments_count INTEGER DEFAULT 0,
    month_day VARCHAR(5),
    score SMALLINT CHECK (score >= 1 AND score <= 100),
    importance_reason JSONB,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- =====================================================
-- 7. EVENT LIKES TABLE
-- =====================================================

CREATE TABLE event_likes (
    id BIGSERIAL PRIMARY KEY,
    event_id BIGINT NOT NULL REFERENCES events(id) ON DELETE CASCADE,
    user_id BIGINT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE(event_id, user_id)
);

-- =====================================================
-- 8. EVENT REFERENCES TABLE
-- =====================================================

CREATE TABLE event_references (
    id BIGSERIAL PRIMARY KEY,
    event_id BIGINT NOT NULL REFERENCES events(id) ON DELETE CASCADE,
    title VARCHAR(500) NOT NULL,
    url VARCHAR(1000) NOT NULL,
    source VARCHAR(100),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- =====================================================
-- 9. COMMENTS TABLE
-- =====================================================

CREATE TABLE comments (
    id BIGSERIAL PRIMARY KEY,
    event_id BIGINT NOT NULL REFERENCES events(id) ON DELETE CASCADE,
    user_id BIGINT NOT NULL,
    content TEXT NOT NULL,
    parent_id BIGINT REFERENCES comments(id) ON DELETE CASCADE,
    is_approved BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);