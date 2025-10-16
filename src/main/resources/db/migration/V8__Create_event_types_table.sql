-- Create event_types table for mobile app with multilingual support
CREATE TABLE event_types (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(20) NOT NULL UNIQUE, -- Event type code (e.g., "politics", "science", "sports")
    name JSONB NOT NULL, -- Multilingual names: {"en": "Politics", "tr": "Siyaset", "es": "Política"}
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create indexes
CREATE INDEX idx_event_types_code ON event_types(code);
CREATE INDEX idx_event_types_name_gin ON event_types USING GIN (name);

-- Add comments
COMMENT ON TABLE event_types IS 'Event types table for mobile app with multilingual support';
COMMENT ON COLUMN event_types.code IS 'Event type code (e.g., "politics", "science", "sports")';
COMMENT ON COLUMN event_types.name IS 'Multilingual event type names stored as JSONB';

-- Insert supported event types
INSERT INTO event_types (code, name) VALUES
('politics', '{"en": "Politics", "tr": "Siyaset", "es": "Política", "de": "Politik", "fr": "Politique", "ar": "السياسة"}'),
('science', '{"en": "Science", "tr": "Bilim", "es": "Ciencia", "de": "Wissenschaft", "fr": "Science", "ar": "العلوم"}'),
('sports', '{"en": "Sports", "tr": "Spor", "es": "Deportes", "de": "Sport", "fr": "Sport", "ar": "الرياضة"}'),
('history', '{"en": "History", "tr": "Tarih", "es": "Historia", "de": "Geschichte", "fr": "Histoire", "ar": "التاريخ"}'),
('entertainment', '{"en": "Entertainment", "tr": "Eğlence", "es": "Entretenimiento", "de": "Unterhaltung", "fr": "Divertissement", "ar": "الترفيه"}'),
('all', '{"en": "All", "tr": "Tümü", "es": "Todos", "de": "Alle", "fr": "Tous", "ar": "الكل"}');

-- Create trigger for updated_at
CREATE OR REPLACE FUNCTION update_event_types_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER update_event_types_updated_at_trigger
    BEFORE UPDATE ON event_types
    FOR EACH ROW
    EXECUTE FUNCTION update_event_types_updated_at();
