-- =====================================================
-- V1: Create initial core tables
-- =====================================================

-- Create users table with optimized structure
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) UNIQUE,
    username VARCHAR(255) UNIQUE NOT NULL,
    auth_provider VARCHAR(20) NOT NULL,
    password_hash VARCHAR(255),
    display_name VARCHAR(255),
    avatar_url VARCHAR(500),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    preferences JSONB,
    devices JSONB,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Create user_roles table
CREATE TABLE user_roles (
    user_id BIGINT NOT NULL,
    role VARCHAR(20) NOT NULL,
    PRIMARY KEY (user_id, role),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Create events table with optimized structure
CREATE TABLE events (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(500) NOT NULL,
    summary TEXT NOT NULL,
    content TEXT NOT NULL,
    date TIMESTAMPTZ NOT NULL,
    category VARCHAR(50) NOT NULL,
    country VARCHAR(10) NOT NULL,
    media JSONB,
    engagement JSONB,
    i18n JSONB,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Create comments table
CREATE TABLE comments (
    id BIGSERIAL PRIMARY KEY,
    event_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    content TEXT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    FOREIGN KEY (event_id) REFERENCES events(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Create likes table
CREATE TABLE likes (
    id BIGSERIAL PRIMARY KEY,
    event_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE(event_id, user_id),
    FOREIGN KEY (event_id) REFERENCES events(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- =====================================================
-- Create optimized indexes
-- =====================================================

-- User indexes
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_is_active ON users(is_active);
CREATE INDEX idx_users_auth_provider ON users(auth_provider);
CREATE INDEX idx_users_created_at ON users(created_at DESC);

-- Event indexes
CREATE INDEX idx_events_date_country_category ON events(date DESC, country, category);
CREATE INDEX idx_events_created_at ON events(created_at DESC);
CREATE INDEX idx_events_category ON events(category);
CREATE INDEX idx_events_country ON events(country);

-- Comment indexes
CREATE INDEX idx_comments_event_created ON comments(event_id, created_at DESC);
CREATE INDEX idx_comments_user_created ON comments(user_id, created_at DESC);

-- Like indexes
CREATE INDEX idx_likes_event_user ON likes(event_id, user_id);
CREATE INDEX idx_likes_user_created ON likes(user_id, created_at DESC);

-- Full-text search index for events
CREATE INDEX idx_events_search ON events USING GIN (
    to_tsvector('english', title || ' ' || summary || ' ' || content)
);

-- JSONB indexes for better performance
CREATE INDEX idx_users_preferences ON users USING GIN (preferences);
CREATE INDEX idx_users_devices ON users USING GIN (devices);
CREATE INDEX idx_events_media ON events USING GIN (media);
CREATE INDEX idx_events_engagement ON events USING GIN (engagement);
CREATE INDEX idx_events_i18n ON events USING GIN (i18n);

-- =====================================================
-- Insert initial data
-- =====================================================

-- Insert sample events
INSERT INTO events (title, summary, content, date, category, country, engagement) VALUES
('Welcome to 5N1K', 'Your gateway to historical events', 'This is the first event in the 5N1K system. Welcome to our platform where you can discover and learn about historical events from around the world.', NOW(), 'HISTORY', 'ALL', '{"likes": 0, "comments": 0, "shares": 0}'),
('Technology News', 'Latest in tech', 'Stay updated with the latest technology news and innovations.', NOW(), 'TECHNOLOGY', 'US', '{"likes": 0, "comments": 0, "shares": 0}'),
('Sports Update', 'Today in sports', 'Get the latest updates from the world of sports.', NOW(), 'SPORTS', 'TR', '{"likes": 0, "comments": 0, "shares": 0}');

-- =====================================================
-- Add table comments for documentation
-- =====================================================

COMMENT ON TABLE users IS 'User accounts and profiles';
COMMENT ON TABLE user_roles IS 'User role assignments';
COMMENT ON TABLE events IS 'Historical events and news';
COMMENT ON TABLE comments IS 'User comments on events';
COMMENT ON TABLE likes IS 'User likes on events';

COMMENT ON COLUMN users.is_active IS 'Whether the user account is active';
COMMENT ON COLUMN users.preferences IS 'User preferences stored as JSON';
COMMENT ON COLUMN users.devices IS 'User device information stored as JSON';
COMMENT ON COLUMN events.engagement IS 'Event engagement metrics (likes, comments, shares)';
COMMENT ON COLUMN events.i18n IS 'Internationalization content for events';
