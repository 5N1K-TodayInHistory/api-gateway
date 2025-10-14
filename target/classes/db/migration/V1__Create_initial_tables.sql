-- Create users table
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) UNIQUE,
    auth_provider VARCHAR(20) NOT NULL,
    password_hash VARCHAR(255),
    display_name VARCHAR(255),
    avatar_url VARCHAR(500),
    preferences JSONB,
    devices JSONB,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create user_roles table
CREATE TABLE user_roles (
    user_id BIGINT NOT NULL,
    role VARCHAR(20) NOT NULL,
    PRIMARY KEY (user_id, role),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Create events table
CREATE TABLE events (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(500) NOT NULL,
    summary TEXT NOT NULL,
    content TEXT NOT NULL,
    date TIMESTAMP NOT NULL,
    category VARCHAR(50) NOT NULL,
    country VARCHAR(10) NOT NULL,
    media JSONB,
    engagement JSONB,
    i18n JSONB,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create comments table
CREATE TABLE comments (
    id BIGSERIAL PRIMARY KEY,
    event_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    content TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (event_id) REFERENCES events(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Create likes table
CREATE TABLE likes (
    id BIGSERIAL PRIMARY KEY,
    event_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(event_id, user_id),
    FOREIGN KEY (event_id) REFERENCES events(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Create indexes
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_fcm_token ON users USING GIN (devices);
CREATE INDEX idx_events_date_country_category ON events(date DESC, country, category);
CREATE INDEX idx_events_created_at ON events(created_at DESC);
CREATE INDEX idx_comments_event_created ON comments(event_id, created_at DESC);
CREATE INDEX idx_likes_event_user ON likes(event_id, user_id);

-- Create full-text search index for events
CREATE INDEX idx_events_search ON events USING GIN (
    to_tsvector('english', title || ' ' || summary || ' ' || content)
);

-- Insert default categories and countries data
INSERT INTO events (title, summary, content, date, category, country, engagement) VALUES
('Welcome to 5N1K', 'Your gateway to historical events', 'This is the first event in the 5N1K system. Welcome to our platform where you can discover and learn about historical events from around the world.', NOW(), 'HISTORY', 'ALL', '{"likes": 0, "comments": 0, "shares": 0}'),
('Technology News', 'Latest in tech', 'Stay updated with the latest technology news and innovations.', NOW(), 'TECHNOLOGY', 'US', '{"likes": 0, "comments": 0, "shares": 0}'),
('Sports Update', 'Today in sports', 'Get the latest updates from the world of sports.', NOW(), 'SPORTS', 'TR', '{"likes": 0, "comments": 0, "shares": 0}');
