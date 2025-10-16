-- =====================================================
-- V6: Create countries table for mobile app data (simplified)
-- =====================================================

-- Create countries table
CREATE TABLE countries (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(3) NOT NULL UNIQUE,
    name JSONB NOT NULL, -- Multilingual names: {"en": "Turkey", "tr": "Türkiye", "es": "Turquía"}
    flag VARCHAR(10) NOT NULL, -- Flag emoji or URL
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create indexes for better performance
CREATE INDEX idx_countries_code ON countries(code);
CREATE INDEX idx_countries_name_gin ON countries USING GIN (name);

-- Add comments for documentation
COMMENT ON TABLE countries IS 'Countries table for mobile app with multilingual support';
COMMENT ON COLUMN countries.code IS 'ISO 3166-1 alpha-2 or alpha-3 country code';
COMMENT ON COLUMN countries.name IS 'Multilingual country names stored as JSONB';
COMMENT ON COLUMN countries.flag IS 'Flag emoji or URL for the country';

-- Insert sample countries data
INSERT INTO countries (code, name, flag) VALUES
-- Turkey
('TR', '{"en": "Turkey", "tr": "Türkiye", "es": "Turquía", "fr": "Turquie", "de": "Türkei"}', '🇹🇷'),

-- United States
('US', '{"en": "United States", "tr": "Amerika Birleşik Devletleri", "es": "Estados Unidos", "fr": "États-Unis", "de": "Vereinigte Staaten"}', '🇺🇸'),

-- United Kingdom
('GB', '{"en": "United Kingdom", "tr": "Birleşik Krallık", "es": "Reino Unido", "fr": "Royaume-Uni", "de": "Vereinigtes Königreich"}', '🇬🇧'),

-- Germany
('DE', '{"en": "Germany", "tr": "Almanya", "es": "Alemania", "fr": "Allemagne", "de": "Deutschland"}', '🇩🇪'),

-- France
('FR', '{"en": "France", "tr": "Fransa", "es": "Francia", "fr": "France", "de": "Frankreich"}', '🇫🇷'),

-- Spain
('ES', '{"en": "Spain", "tr": "İspanya", "es": "España", "fr": "Espagne", "de": "Spanien"}', '🇪🇸'),

-- Italy
('IT', '{"en": "Italy", "tr": "İtalya", "es": "Italia", "fr": "Italie", "de": "Italien"}', '🇮🇹'),

-- Russia
('RU', '{"en": "Russia", "tr": "Rusya", "es": "Rusia", "fr": "Russie", "de": "Russland"}', '🇷🇺'),

-- China
('CN', '{"en": "China", "tr": "Çin", "es": "China", "fr": "Chine", "de": "China"}', '🇨🇳'),

-- Japan
('JP', '{"en": "Japan", "tr": "Japonya", "es": "Japón", "fr": "Japon", "de": "Japan"}', '🇯🇵'),

-- Brazil
('BR', '{"en": "Brazil", "tr": "Brezilya", "es": "Brasil", "fr": "Brésil", "de": "Brasilien"}', '🇧🇷'),

-- India
('IN', '{"en": "India", "tr": "Hindistan", "es": "India", "fr": "Inde", "de": "Indien"}', '🇮🇳'),

-- Australia
('AU', '{"en": "Australia", "tr": "Avustralya", "es": "Australia", "fr": "Australie", "de": "Australien"}', '🇦🇺'),

-- Canada
('CA', '{"en": "Canada", "tr": "Kanada", "es": "Canadá", "fr": "Canada", "de": "Kanada"}', '🇨🇦'),

-- Mexico
('MX', '{"en": "Mexico", "tr": "Meksika", "es": "México", "fr": "Mexique", "de": "Mexiko"}', '🇲🇽'),

-- Argentina
('AR', '{"en": "Argentina", "tr": "Arjantin", "es": "Argentina", "fr": "Argentine", "de": "Argentinien"}', '🇦🇷'),

-- Egypt
('EG', '{"en": "Egypt", "tr": "Mısır", "es": "Egipto", "fr": "Égypte", "de": "Ägypten"}', '🇪🇬'),

-- South Africa
('ZA', '{"en": "South Africa", "tr": "Güney Afrika", "es": "Sudáfrica", "fr": "Afrique du Sud", "de": "Südafrika"}', '🇿🇦'),

-- Nigeria
('NG', '{"en": "Nigeria", "tr": "Nijerya", "es": "Nigeria", "fr": "Nigéria", "de": "Nigeria"}', '🇳🇬'),

-- Global/All countries
('ALL', '{"en": "Global", "tr": "Küresel", "es": "Global", "fr": "Mondial", "de": "Global"}', '🌍');

-- Create trigger for updated_at timestamp
CREATE OR REPLACE FUNCTION update_countries_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

CREATE TRIGGER update_countries_updated_at
    BEFORE UPDATE ON countries
    FOR EACH ROW
    EXECUTE FUNCTION update_countries_updated_at();
