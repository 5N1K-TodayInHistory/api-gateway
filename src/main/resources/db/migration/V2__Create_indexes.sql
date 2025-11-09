-- =====================================================
-- V2: Create Indexes for Performance
-- =====================================================

-- Users indexes
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_auth_provider ON users(auth_provider);
CREATE INDEX idx_users_created_at ON users(created_at);

-- Refresh tokens indexes
CREATE INDEX idx_refresh_tokens_user_id ON refresh_tokens(user_id);
CREATE INDEX idx_refresh_tokens_token_hash ON refresh_tokens(token_hash);
CREATE INDEX idx_refresh_tokens_expires_at ON refresh_tokens(expires_at);
CREATE INDEX idx_refresh_tokens_revoked ON refresh_tokens(revoked);

-- Countries indexes
CREATE INDEX idx_countries_code ON countries(code);
CREATE INDEX idx_countries_created_at ON countries(created_at);

-- Languages indexes
CREATE INDEX idx_languages_code ON languages(code);
CREATE INDEX idx_languages_created_at ON languages(created_at);

-- Event types indexes
CREATE INDEX idx_event_types_created_at ON event_types(created_at);

-- Events indexes
CREATE INDEX idx_events_date ON events(date);
CREATE INDEX idx_events_type ON events(type);
CREATE INDEX idx_events_country ON events(country);
CREATE INDEX idx_events_created_at ON events(created_at);
CREATE INDEX idx_events_month_day ON events(month_day);
CREATE INDEX idx_events_score ON events(score);
CREATE INDEX idx_events_likes_count ON events(likes_count);
CREATE INDEX idx_events_comments_count ON events(comments_count);

-- Event likes indexes
CREATE INDEX idx_event_likes_event_id ON event_likes(event_id);
CREATE INDEX idx_event_likes_user_id ON event_likes(user_id);
CREATE INDEX idx_event_likes_created_at ON event_likes(created_at);

-- Event references indexes
CREATE INDEX idx_event_references_event_id ON event_references(event_id);
CREATE INDEX idx_event_references_created_at ON event_references(created_at);

-- Comments indexes
CREATE INDEX idx_comments_event_id ON comments(event_id);
CREATE INDEX idx_comments_user_id ON comments(user_id);
CREATE INDEX idx_comments_parent_id ON comments(parent_id);
CREATE INDEX idx_comments_created_at ON comments(created_at);
CREATE INDEX idx_comments_is_approved ON comments(is_approved);
