-- Drop existing events table and recreate with new design
DROP TABLE IF EXISTS events CASCADE;

-- Create new events table with redesigned structure
CREATE TABLE events (
    id BIGSERIAL PRIMARY KEY,
    title JSONB NOT NULL, -- Multilingual titles: {"en": "Title", "tr": "Başlık"}
    description JSONB NOT NULL, -- Multilingual descriptions: {"en": "Description", "tr": "Açıklama"}
    content JSONB NOT NULL, -- Multilingual content: {"en": "Content", "tr": "İçerik"}
    date TIMESTAMP NOT NULL,
    type VARCHAR(20) NOT NULL, -- Event type code (politics, science, sports, etc.)
    country VARCHAR(3) NOT NULL, -- Country code (TR, US, ALL, etc.)
    image_url VARCHAR(500), -- Main image URL
    video_urls JSONB DEFAULT '[]'::jsonb, -- Array of video URLs
    audio_urls JSONB DEFAULT '[]'::jsonb, -- Array of audio URLs
    likes_count INTEGER DEFAULT 0,
    comments_count INTEGER DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create indexes for performance
CREATE INDEX idx_events_date ON events(date DESC);
CREATE INDEX idx_events_type ON events(type);
CREATE INDEX idx_events_country ON events(country);
CREATE INDEX idx_events_date_type_country ON events(date DESC, type, country);
CREATE INDEX idx_events_likes_count ON events(likes_count DESC);
CREATE INDEX idx_events_title_gin ON events USING GIN (title);
CREATE INDEX idx_events_description_gin ON events USING GIN (description);
CREATE INDEX idx_events_content_gin ON events USING GIN (content);

-- Add comments
COMMENT ON TABLE events IS 'Events table with multilingual support and new structure';
COMMENT ON COLUMN events.title IS 'Multilingual event titles stored as JSONB';
COMMENT ON COLUMN events.description IS 'Multilingual event descriptions stored as JSONB';
COMMENT ON COLUMN events.content IS 'Multilingual event content stored as JSONB';
COMMENT ON COLUMN events.type IS 'Event type code (politics, science, sports, history, entertainment, all)';
COMMENT ON COLUMN events.country IS 'Country code (TR, US, ALL, etc.)';
COMMENT ON COLUMN events.image_url IS 'Main image URL for the event';
COMMENT ON COLUMN events.video_urls IS 'Array of video URLs stored as JSONB';
COMMENT ON COLUMN events.audio_urls IS 'Array of audio URLs stored as JSONB';
COMMENT ON COLUMN events.likes_count IS 'Total number of likes for this event';
COMMENT ON COLUMN events.comments_count IS 'Total number of comments for this event';

-- Create event_likes table for tracking individual likes
CREATE TABLE event_likes (
    id BIGSERIAL PRIMARY KEY,
    event_id BIGINT NOT NULL REFERENCES events(id) ON DELETE CASCADE,
    user_id BIGINT NOT NULL, -- User ID who liked the event
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(event_id, user_id) -- Prevent duplicate likes from same user
);

-- Create indexes for event_likes
CREATE INDEX idx_event_likes_event_id ON event_likes(event_id);
CREATE INDEX idx_event_likes_user_id ON event_likes(user_id);
CREATE INDEX idx_event_likes_created_at ON event_likes(created_at DESC);

-- Add comments for event_likes
COMMENT ON TABLE event_likes IS 'Tracks individual likes for events';
COMMENT ON COLUMN event_likes.event_id IS 'Reference to the event that was liked';
COMMENT ON COLUMN event_likes.user_id IS 'ID of the user who liked the event';

-- Create event_references table for event references/links
CREATE TABLE event_references (
    id BIGSERIAL PRIMARY KEY,
    event_id BIGINT NOT NULL REFERENCES events(id) ON DELETE CASCADE,
    title VARCHAR(500) NOT NULL,
    url VARCHAR(1000) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create indexes for event_references
CREATE INDEX idx_event_references_event_id ON event_references(event_id);
CREATE INDEX idx_event_references_created_at ON event_references(created_at DESC);

-- Add comments for event_references
COMMENT ON TABLE event_references IS 'References and links related to events';
COMMENT ON COLUMN event_references.event_id IS 'Reference to the event';
COMMENT ON COLUMN event_references.title IS 'Title of the reference';
COMMENT ON COLUMN event_references.url IS 'URL of the reference';

-- Create trigger for updated_at
CREATE OR REPLACE FUNCTION update_events_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER update_events_updated_at_trigger
    BEFORE UPDATE ON events
    FOR EACH ROW
    EXECUTE FUNCTION update_events_updated_at();

-- Insert sample events with new structure
INSERT INTO events (title, description, content, date, type, country, image_url, video_urls, audio_urls, likes_count, comments_count) VALUES
(
    '{"en": "International Trade Agreement", "tr": "Uluslararası Ticaret Anlaşması", "es": "Acuerdo Comercial Internacional"}',
    '{"en": "New trade agreement signed between multiple countries.", "tr": "Birden fazla ülke arasında yeni ticaret anlaşması imzalandı.", "es": "Nuevo acuerdo comercial firmado entre múltiples países."}',
    '{"en": "A comprehensive trade agreement has been signed between several countries, promising to boost economic cooperation and reduce trade barriers.", "tr": "Birkaç ülke arasında kapsamlı bir ticaret anlaşması imzalandı ve bu anlaşma ekonomik işbirliğini artırmayı ve ticaret engellerini azaltmayı vaat ediyor.", "es": "Se ha firmado un acuerdo comercial integral entre varios países, prometiendo impulsar la cooperación económica y reducir las barreras comerciales."}',
    CURRENT_TIMESTAMP,
    'politics',
    'TR',
    'https://picsum.photos/400/300?random=37',
    '[]'::jsonb,
    '["https://www.soundjay.com/misc/sounds/bell-ringing-05.wav"]'::jsonb,
    1800,
    110
),
(
    '{"en": "Scientific Discovery", "tr": "Bilimsel Keşif", "es": "Descubrimiento Científico"}',
    '{"en": "Breakthrough in renewable energy technology.", "tr": "Yenilenebilir enerji teknolojisinde çığır açan gelişme.", "es": "Avance en tecnología de energía renovable."}',
    '{"en": "Scientists have made a significant breakthrough in renewable energy technology that could revolutionize how we generate clean power.", "tr": "Bilim insanları, temiz enerji üretme şeklimizi devrim yaratabilecek yenilenebilir enerji teknolojisinde önemli bir atılım yaptı.", "es": "Los científicos han logrado un avance significativo en tecnología de energía renovable que podría revolucionar cómo generamos energía limpia."}',
    CURRENT_TIMESTAMP - INTERVAL '1 day',
    'science',
    'US',
    'https://picsum.photos/400/300?random=38',
    '["https://example.com/video1.mp4"]'::jsonb,
    '[]'::jsonb,
    2500,
    89
),
(
    '{"en": "Championship Final", "tr": "Şampiyonluk Finali", "es": "Final del Campeonato"}',
    '{"en": "Exciting championship final match results.", "tr": "Heyecan verici şampiyonluk final maçı sonuçları.", "es": "Resultados emocionantes del partido final del campeonato."}',
    '{"en": "The championship final delivered an exciting match with unexpected results that will be remembered for years to come.", "tr": "Şampiyonluk finali, yıllarca hatırlanacak beklenmedik sonuçlarla heyecan verici bir maç sundu.", "es": "La final del campeonato ofreció un partido emocionante con resultados inesperados que serán recordados por años."}',
    CURRENT_TIMESTAMP - INTERVAL '2 days',
    'sports',
    'ALL',
    'https://picsum.photos/400/300?random=39',
    '["https://example.com/sports-video.mp4"]'::jsonb,
    '["https://example.com/sports-audio.mp3"]'::jsonb,
    3200,
    156
);

-- Insert sample references for the first event
INSERT INTO event_references (event_id, title, url) VALUES
(1, 'Trade Agreement Details', 'https://example.com/trade-agreement'),
(1, 'Economic Impact Analysis', 'https://example.com/economic-analysis'),
(2, 'Research Paper', 'https://example.com/research-paper'),
(2, 'Technology Documentation', 'https://example.com/tech-docs'),
(3, 'Match Highlights', 'https://example.com/match-highlights'),
(3, 'Player Statistics', 'https://example.com/player-stats');
