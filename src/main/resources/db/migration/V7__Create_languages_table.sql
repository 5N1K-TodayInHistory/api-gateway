-- Create languages table for mobile app with multilingual support
CREATE TABLE languages (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(5) NOT NULL UNIQUE, -- Language code (e.g., "en", "tr", "es")
    name JSONB NOT NULL, -- Multilingual names: {"en": "English", "tr": "İngilizce", "es": "Inglés"}
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create indexes
CREATE INDEX idx_languages_code ON languages(code);
CREATE INDEX idx_languages_name_gin ON languages USING GIN (name);

-- Add comments
COMMENT ON TABLE languages IS 'Languages table for mobile app with multilingual support';
COMMENT ON COLUMN languages.code IS 'ISO 639-1 language code (e.g., "en", "tr", "es")';
COMMENT ON COLUMN languages.name IS 'Multilingual language names stored as JSONB';

-- Insert supported languages
INSERT INTO languages (code, name) VALUES
('en', '{"en": "English", "tr": "İngilizce", "es": "Inglés", "de": "Englisch", "fr": "Anglais", "ar": "الإنجليزية"}'),
('tr', '{"en": "Turkish", "tr": "Türkçe", "es": "Turco", "de": "Türkisch", "fr": "Turc", "ar": "التركية"}'),
('es', '{"en": "Spanish", "tr": "İspanyolca", "es": "Español", "de": "Spanisch", "fr": "Espagnol", "ar": "الإسبانية"}'),
('de', '{"en": "German", "tr": "Almanca", "es": "Alemán", "de": "Deutsch", "fr": "Allemand", "ar": "الألمانية"}'),
('fr', '{"en": "French", "tr": "Fransızca", "es": "Francés", "de": "Französisch", "fr": "Français", "ar": "الفرنسية"}'),
('ar', '{"en": "Arabic", "tr": "Arapça", "es": "Árabe", "de": "Arabisch", "fr": "Arabe", "ar": "العربية"}');

-- Create trigger for updated_at
CREATE OR REPLACE FUNCTION update_languages_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER update_languages_updated_at_trigger
    BEFORE UPDATE ON languages
    FOR EACH ROW
    EXECUTE FUNCTION update_languages_updated_at();
