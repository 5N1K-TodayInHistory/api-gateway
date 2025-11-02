-- =====================================================
-- V1: Complete Database Schema for API Gateway
-- This migration creates the entire database schema from scratch
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
        "countries": ["TR"],
        "categories": ["science", "politics", "sports", "history", "entertainment"],
        "language": "en",
        "timezone": "UTC",
        "notifications": {
            "daily": true,
            "breaking": true
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
    device_id TEXT,
    token_hash TEXT NOT NULL,
    issued_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    expires_at TIMESTAMPTZ NOT NULL,
    used BOOLEAN NOT NULL DEFAULT FALSE,
    revoked BOOLEAN NOT NULL DEFAULT FALSE,
    rotated_from_id BIGINT REFERENCES refresh_tokens(id) ON DELETE SET NULL,
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
    flag VARCHAR(10) NOT NULL,
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
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- =====================================================
-- 9. COMMENTS TABLE
-- =====================================================

CREATE TABLE comments (
    id BIGSERIAL PRIMARY KEY,
    event_id BIGINT NOT NULL REFERENCES events(id) ON DELETE CASCADE,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    content TEXT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- =====================================================
-- CREATE INDEXES FOR PERFORMANCE
-- =====================================================

-- Users indexes
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_is_active ON users(is_active);
CREATE INDEX idx_users_auth_provider ON users(auth_provider);
CREATE INDEX idx_users_created_at ON users(created_at DESC);
CREATE INDEX idx_users_preferences ON users USING GIN (preferences);
CREATE INDEX idx_users_devices ON users USING GIN (devices);

-- Refresh tokens indexes
CREATE UNIQUE INDEX uq_refresh_token_hash ON refresh_tokens(token_hash);
CREATE INDEX idx_refresh_user_session ON refresh_tokens(user_id, session_id);
CREATE INDEX idx_refresh_user_active ON refresh_tokens(user_id) WHERE revoked = FALSE AND used = FALSE;
CREATE INDEX idx_refresh_expires ON refresh_tokens(expires_at) WHERE revoked = FALSE;
CREATE INDEX idx_refresh_session_id ON refresh_tokens(session_id);
CREATE INDEX idx_refresh_issued_at ON refresh_tokens(issued_at DESC);

-- Countries indexes
CREATE INDEX idx_countries_code ON countries(code);
CREATE INDEX idx_countries_name_gin ON countries USING GIN (name);

-- Languages indexes
CREATE INDEX idx_languages_code ON languages(code);
CREATE INDEX idx_languages_name_gin ON languages USING GIN (name);

-- Event types indexes
CREATE INDEX idx_event_types_code ON event_types(code);
CREATE INDEX idx_event_types_name_gin ON event_types USING GIN (name);

-- Events indexes
CREATE INDEX idx_events_date ON events(date DESC);
CREATE INDEX idx_events_type ON events(type);
CREATE INDEX idx_events_country ON events(country);
CREATE INDEX idx_events_date_type_country ON events(date DESC, type, country);
CREATE INDEX idx_events_likes_count ON events(likes_count DESC);
CREATE INDEX idx_events_title_gin ON events USING GIN (title);
CREATE INDEX idx_events_description_gin ON events USING GIN (description);
CREATE INDEX idx_events_content_gin ON events USING GIN (content);

-- Event likes indexes
CREATE INDEX idx_event_likes_event_id ON event_likes(event_id);
CREATE INDEX idx_event_likes_user_id ON event_likes(user_id);
CREATE INDEX idx_event_likes_created_at ON event_likes(created_at DESC);

-- Event references indexes
CREATE INDEX idx_event_references_event_id ON event_references(event_id);
CREATE INDEX idx_event_references_created_at ON event_references(created_at DESC);

-- Comments indexes
CREATE INDEX idx_comments_event_created ON comments(event_id, created_at DESC);
CREATE INDEX idx_comments_user_created ON comments(user_id, created_at DESC);

-- =====================================================
-- CREATE TRIGGERS FOR UPDATED_AT
-- =====================================================

-- Function for updating updated_at timestamp
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Triggers for updated_at
CREATE TRIGGER update_users_updated_at
    BEFORE UPDATE ON users
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_countries_updated_at
    BEFORE UPDATE ON countries
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_languages_updated_at
    BEFORE UPDATE ON languages
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_event_types_updated_at
    BEFORE UPDATE ON event_types
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_events_updated_at
    BEFORE UPDATE ON events
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

-- =====================================================
-- INSERT INITIAL DATA
-- =====================================================

-- Insert countries
INSERT INTO countries (code, name, flag) VALUES
('TR', '{"en": "Turkey", "tr": "Türkiye", "es": "Turquía", "fr": "Turquie", "de": "Türkei"}', '🇹🇷'),
('US', '{"en": "United States", "tr": "Amerika Birleşik Devletleri", "es": "Estados Unidos", "fr": "États-Unis", "de": "Vereinigte Staaten"}', '🇺🇸'),
('GB', '{"en": "United Kingdom", "tr": "Birleşik Krallık", "es": "Reino Unido", "fr": "Royaume-Uni", "de": "Vereinigtes Königreich"}', '🇬🇧'),
('DE', '{"en": "Germany", "tr": "Almanya", "es": "Alemania", "fr": "Allemagne", "de": "Deutschland"}', '🇩🇪'),
('FR', '{"en": "France", "tr": "Fransa", "es": "Francia", "fr": "France", "de": "Frankreich"}', '🇫🇷'),
('ES', '{"en": "Spain", "tr": "İspanya", "es": "España", "fr": "Espagne", "de": "Spanien"}', '🇪🇸'),
('IT', '{"en": "Italy", "tr": "İtalya", "es": "Italia", "fr": "Italie", "de": "Italien"}', '🇮🇹'),
('RU', '{"en": "Russia", "tr": "Rusya", "es": "Rusia", "fr": "Russie", "de": "Russland"}', '🇷🇺'),
('CN', '{"en": "China", "tr": "Çin", "es": "China", "fr": "Chine", "de": "China"}', '🇨🇳'),
('JP', '{"en": "Japan", "tr": "Japonya", "es": "Japón", "fr": "Japon", "de": "Japan"}', '🇯🇵'),
('BR', '{"en": "Brazil", "tr": "Brezilya", "es": "Brasil", "fr": "Brésil", "de": "Brasilien"}', '🇧🇷'),
('IN', '{"en": "India", "tr": "Hindistan", "es": "India", "fr": "Inde", "de": "Indien"}', '🇮🇳'),
('AU', '{"en": "Australia", "tr": "Avustralya", "es": "Australia", "fr": "Australie", "de": "Australien"}', '🇦🇺'),
('CA', '{"en": "Canada", "tr": "Kanada", "es": "Canadá", "fr": "Canada", "de": "Kanada"}', '🇨🇦'),
('MX', '{"en": "Mexico", "tr": "Meksika", "es": "México", "fr": "Mexique", "de": "Mexiko"}', '🇲🇽'),
('AR', '{"en": "Argentina", "tr": "Arjantin", "es": "Argentina", "fr": "Argentine", "de": "Argentinien"}', '🇦🇷'),
('EG', '{"en": "Egypt", "tr": "Mısır", "es": "Egipto", "fr": "Égypte", "de": "Ägypten"}', '🇪🇬'),
('ZA', '{"en": "South Africa", "tr": "Güney Afrika", "es": "Sudáfrica", "fr": "Afrique du Sud", "de": "Südafrika"}', '🇿🇦'),
('NG', '{"en": "Nigeria", "tr": "Nijerya", "es": "Nigeria", "fr": "Nigéria", "de": "Nigeria"}', '🇳🇬'),
('ALL', '{"en": "Global", "tr": "Küresel", "es": "Global", "fr": "Mondial", "de": "Global"}', '🌍');

-- Insert languages
INSERT INTO languages (code, name) VALUES
('en', '{"en": "English", "tr": "İngilizce", "es": "Inglés", "de": "Englisch", "fr": "Anglais", "ar": "الإنجليزية"}'),
('tr', '{"en": "Turkish", "tr": "Türkçe", "es": "Turco", "de": "Türkisch", "fr": "Turc", "ar": "التركية"}'),
('es', '{"en": "Spanish", "tr": "İspanyolca", "es": "Español", "de": "Spanisch", "fr": "Espagnol", "ar": "الإسبانية"}'),
('de', '{"en": "German", "tr": "Almanca", "es": "Alemán", "de": "Deutsch", "fr": "Allemand", "ar": "الألمانية"}'),
('fr', '{"en": "French", "tr": "Fransızca", "es": "Francés", "de": "Französisch", "fr": "Français", "ar": "الفرنسية"}'),
('ar', '{"en": "Arabic", "tr": "Arapça", "es": "Árabe", "de": "Arabisch", "fr": "Arabe", "ar": "العربية"}');

-- Insert event types
INSERT INTO event_types (code, name) VALUES
('politics', '{"en": "Politics & Power Shifts", "tr": "Siyaset ve İktidar Değişimleri", "es": "Política y Cambios de Poder", "de": "Politik und Machtverschiebungen", "fr": "Politique et Changements de Pouvoir", "ar": "السياسة وتحولات القوة"}'),
('discovery', '{"en": "Discovery & Innovation", "tr": "Keşif ve İnovasyon", "es": "Descubrimiento e Innovación", "de": "Entdeckung und Innovation", "fr": "Découverte et Innovation", "ar": "الاكتشاف والابتكار"}'),
('conflict', '{"en": "Wars, Revolts & Crises", "tr": "Savaşlar, İsyanlar ve Krizler", "es": "Guerras, Revueltas y Crisis", "de": "Kriege, Revolten und Krisen", "fr": "Guerres, Révoltes et Crises", "ar": "الحروب والثورات والأزمات"}'),
('culture', '{"en": "Art, Media & Society", "tr": "Sanat, Medya ve Toplum", "es": "Arte, Medios y Sociedad", "de": "Kunst, Medien und Gesellschaft", "fr": "Art, Médias et Société", "ar": "الفن والإعلام والمجتمع"}'),
('economy', '{"en": "Economy & Industry", "tr": "Ekonomi ve Sanayi", "es": "Economía e Industria", "de": "Wirtschaft und Industrie", "fr": "Économie et Industrie", "ar": "الاقتصاد والصناعة"}'),
('space', '{"en": "Space & Exploration", "tr": "Uzay ve Keşif", "es": "Espacio y Exploración", "de": "Raum und Exploration", "fr": "Espace et Exploration", "ar": "الفضاء والاستكشاف"}'),
('tragedy', '{"en": "Disasters & Human Loss", "tr": "Felaketler ve İnsan Kaybı", "es": "Desastres y Pérdidas Humanas", "de": "Katastrophen und Menschenverluste", "fr": "Catastrophes et Pertes Humaines", "ar": "الكوارث والخسائر البشرية"}'),
('milestone', '{"en": "Milestones & Anniversaries", "tr": "Dönüm Noktaları ve Yıldönümleri", "es": "Hitos y Aniversarios", "de": "Meilensteine und Jahrestage", "fr": "Jalons et Anniversaires", "ar": "المعالم والذكرى السنوية"}'),
('justice', '{"en": "Justice & Rights", "tr": "Adalet ve Haklar", "es": "Justicia y Derechos", "de": "Gerechtigkeit und Rechte", "fr": "Justice et Droits", "ar": "العدالة والحقوق"}'),
('innovation', '{"en": "Technology & Future", "tr": "Teknoloji ve Gelecek", "es": "Tecnología y Futuro", "de": "Technologie und Zukunft", "fr": "Technologie et Avenir", "ar": "التكنولوجيا والمستقبل"}');

-- Insert sample events
INSERT INTO events (title, description, content, date, type, country, image_url, video_urls, audio_urls, likes_count, comments_count) VALUES
(
    '{"en": "International Trade Agreement", "tr": "Uluslararası Ticaret Anlaşması", "es": "Acuerdo Comercial Internacional"}',
    '{"en": "New trade agreement signed between multiple countries.", "tr": "Birden fazla ülke arasında yeni ticaret anlaşması imzalandı.", "es": "Nuevo acuerdo comercial firmado entre múltiples países."}',
    '{"en": "A comprehensive trade agreement has been signed between several countries, promising to boost economic cooperation and reduce trade barriers.", "tr": "Birkaç ülke arasında kapsamlı bir ticaret anlaşması imzalandı ve bu anlaşma ekonomik işbirliğini artırmayı ve ticaret engellerini azaltmayı vaat ediyor.", "es": "Se ha firmado un acuerdo comercial integral entre varios países, prometiendo impulsar la cooperación económica y reducir las barreras comerciales."}',
    NOW() + INTERVAL '1 day',
    'economy',
    'TR',
    'https://picsum.photos/400/300?random=1',
    '[]'::jsonb,
    '["https://www.soundjay.com/misc/sounds/bell-ringing-05.wav"]'::jsonb,
    1800,
    110
),
(
    '{"en": "Scientific Discovery", "tr": "Bilimsel Keşif", "es": "Descubrimiento Científico"}',
    '{"en": "Breakthrough in renewable energy technology.", "tr": "Yenilenebilir enerji teknolojisinde çığır açan gelişme.", "es": "Avance en tecnología de energía renovable."}',
    '{"en": "Scientists have made a significant breakthrough in renewable energy technology that could revolutionize how we generate clean power.", "tr": "Bilim insanları, temiz enerji üretme şeklimizi devrim yaratabilecek yenilenebilir enerji teknolojisinde önemli bir atılım yaptı.", "es": "Los científicos han logrado un avance significativo en tecnología de energía renovable que podría revolucionar cómo generamos energía limpia."}',
    NOW(),
    'discovery',
    'US',
    'https://picsum.photos/400/300?random=2',
    '["https://example.com/video1.mp4"]'::jsonb,
    '[]'::jsonb,
    2500,
    89
),
(
    '{"en": "Championship Final", "tr": "Şampiyonluk Finali", "es": "Final del Campeonato"}',
    '{"en": "Exciting championship final match results.", "tr": "Heyecan verici şampiyonluk final maçı sonuçları.", "es": "Resultados emocionantes del partido final del campeonato."}',
    '{"en": "The championship final delivered an exciting match with unexpected results that will be remembered for years to come.", "tr": "Şampiyonluk finali, yıllarca hatırlanacak beklenmedik sonuçlarla heyecan verici bir maç sundu.", "es": "La final del campeonato ofreció un partido emocionante con resultados inesperados que serán recordados por años."}',
    NOW() - INTERVAL '1 day',
    'conflict',
    'ALL',
    'https://picsum.photos/400/300?random=3',
    '["https://example.com/sports-video.mp4"]'::jsonb,
    '["https://example.com/sports-audio.mp3"]'::jsonb,
    3200,
    156
),
(
    '{"en": "Historical Discovery", "tr": "Tarihi Keşif", "es": "Descubrimiento Histórico"}',
    '{"en": "Ancient artifacts discovered in archaeological site.", "tr": "Arkeolojik alanda antik eserler keşfedildi.", "es": "Artefactos antiguos descubiertos en sitio arqueológico."}',
    '{"en": "Archaeologists have uncovered remarkable ancient artifacts that provide new insights into early civilizations.", "tr": "Arkeologlar, erken medeniyetler hakkında yeni bilgiler sağlayan dikkat çekici antik eserler ortaya çıkardı.", "es": "Los arqueólogos han descubierto artefactos antiguos notables que proporcionan nuevas perspectivas sobre las civilizaciones tempranas."}',
    NOW(),
    'discovery',
    'EG',
    'https://picsum.photos/400/300?random=4',
    '[]'::jsonb,
    '["https://example.com/history-audio.mp3"]'::jsonb,
    1200,
    45
),
(
    '{"en": "Entertainment News", "tr": "Eğlence Haberleri", "es": "Noticias de Entretenimiento"}',
    '{"en": "Major entertainment industry announcement.", "tr": "Büyük eğlence endüstrisi duyurusu.", "es": "Importante anuncio de la industria del entretenimiento."}',
    '{"en": "The entertainment industry has announced exciting new projects and collaborations that will shape the future of media.", "tr": "Eğlence endüstrisi, medyanın geleceğini şekillendirecek heyecan verici yeni projeler ve işbirlikleri duyurdu.", "es": "La industria del entretenimiento ha anunciado emocionantes nuevos proyectos y colaboraciones que darán forma al futuro de los medios."}',
    NOW() + INTERVAL '1 day',
    'culture',
    'US',
    'https://picsum.photos/400/300?random=5',
    '["https://example.com/entertainment-video.mp4"]'::jsonb,
    '[]'::jsonb,
    890,
    23
);

-- Insert sample references for events
INSERT INTO event_references (event_id, title, url) VALUES
(1, 'Trade Agreement Details', 'https://example.com/trade-agreement'),
(1, 'Economic Impact Analysis', 'https://example.com/economic-analysis'),
(2, 'Research Paper', 'https://example.com/research-paper'),
(2, 'Technology Documentation', 'https://example.com/tech-docs'),
(3, 'Match Highlights', 'https://example.com/match-highlights'),
(3, 'Player Statistics', 'https://example.com/player-stats'),
(4, 'Archaeological Report', 'https://example.com/archaeological-report'),
(4, 'Historical Analysis', 'https://example.com/historical-analysis'),
(5, 'Industry Report', 'https://example.com/industry-report'),
(5, 'Media Analysis', 'https://example.com/media-analysis');

-- =====================================================
-- ADD TABLE COMMENTS FOR DOCUMENTATION
-- =====================================================

COMMENT ON TABLE users IS 'User accounts and profiles with preferences and device information';
COMMENT ON TABLE refresh_tokens IS 'Secure refresh token management with rotation and reuse detection';
COMMENT ON TABLE countries IS 'Countries table for mobile app with multilingual support';
COMMENT ON TABLE languages IS 'Languages table for mobile app with multilingual support';
COMMENT ON TABLE event_types IS 'Event types table for mobile app with multilingual support';
COMMENT ON TABLE events IS 'Events table with multilingual support and new structure';
COMMENT ON TABLE event_likes IS 'Tracks individual likes for events';
COMMENT ON TABLE event_references IS 'References and links related to events';
COMMENT ON TABLE comments IS 'User comments on events';

COMMENT ON COLUMN users.is_active IS 'Whether the user account is active';
COMMENT ON COLUMN users.preferences IS 'User preferences stored as JSONB';
COMMENT ON COLUMN users.devices IS 'User device information stored as JSONB';
COMMENT ON COLUMN refresh_tokens.session_id IS 'Unique session identifier for device/session based token management';
COMMENT ON COLUMN refresh_tokens.device_id IS 'Optional client-side device identifier';
COMMENT ON COLUMN refresh_tokens.token_hash IS 'SHA-256 hash of the refresh token for security';
COMMENT ON COLUMN refresh_tokens.used IS 'Whether this token has been used for rotation';
COMMENT ON COLUMN refresh_tokens.revoked IS 'Whether this token has been revoked';
COMMENT ON COLUMN refresh_tokens.rotated_from_id IS 'Reference to the token this was rotated from';
COMMENT ON COLUMN refresh_tokens.ip_created IS 'IP address of the client that created the token';
COMMENT ON COLUMN refresh_tokens.ua_created IS 'User agent string of the client that created the token';
COMMENT ON COLUMN countries.code IS 'ISO 3166-1 alpha-2 or alpha-3 country code';
COMMENT ON COLUMN countries.name IS 'Multilingual country names stored as JSONB';
COMMENT ON COLUMN countries.flag IS 'Flag emoji or URL for the country';
COMMENT ON COLUMN languages.code IS 'ISO 639-1 language code (e.g., "en", "tr", "es")';
COMMENT ON COLUMN languages.name IS 'Multilingual language names stored as JSONB';
COMMENT ON COLUMN event_types.code IS 'Event type code (e.g., "politics", "discovery", "conflict", "culture", "economy", "space", "tragedy", "milestone", "justice", "innovation")';
COMMENT ON COLUMN event_types.name IS 'Multilingual event type names stored as JSONB';
COMMENT ON COLUMN events.title IS 'Multilingual event titles stored as JSONB';
COMMENT ON COLUMN events.description IS 'Multilingual event descriptions stored as JSONB';
COMMENT ON COLUMN events.content IS 'Multilingual event content stored as JSONB';
COMMENT ON COLUMN events.type IS 'Event type code (politics, discovery, conflict, culture, economy, space, tragedy, milestone, justice, innovation)';
COMMENT ON COLUMN events.country IS 'Country code (TR, US, ALL, etc.)';
COMMENT ON COLUMN events.image_url IS 'Main image URL for the event';
COMMENT ON COLUMN events.video_urls IS 'Array of video URLs stored as JSONB';
COMMENT ON COLUMN events.audio_urls IS 'Array of audio URLs stored as JSONB';
COMMENT ON COLUMN events.likes_count IS 'Total number of likes for this event';
COMMENT ON COLUMN events.comments_count IS 'Total number of comments for this event';
COMMENT ON COLUMN event_likes.event_id IS 'Reference to the event that was liked';
COMMENT ON COLUMN event_likes.user_id IS 'ID of the user who liked the event';
COMMENT ON COLUMN event_references.event_id IS 'Reference to the event';
COMMENT ON COLUMN event_references.title IS 'Title of the reference';
COMMENT ON COLUMN event_references.url IS 'URL of the reference';
