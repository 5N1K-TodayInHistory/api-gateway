-- =====================================================
-- V4: Add ratio field to events table for importance ranking
-- =====================================================

-- Add ratio column to events table
ALTER TABLE events ADD COLUMN ratio INTEGER NOT NULL DEFAULT 50;

-- Add index for ratio field for better query performance
CREATE INDEX idx_events_ratio ON events(ratio DESC);

-- Update existing events with different importance values based on category and country
UPDATE events 
SET ratio = CASE 
    WHEN category = 'HISTORY' AND country = 'ALL' THEN 90
    WHEN category = 'SCIENCE' AND country = 'US' THEN 85
    WHEN category = 'SPORTS' AND country = 'TR' THEN 80
    WHEN category = 'POLITICS' THEN 75
    WHEN category = 'TECHNOLOGY' THEN 70
    WHEN category = 'HEALTH' THEN 65
    WHEN category = 'BUSINESS' THEN 60
    WHEN category = 'ENTERTAINMENT' THEN 55
    ELSE 50
END;

-- Update the composite index to include ratio for better performance
DROP INDEX IF EXISTS idx_events_date_country_category;
CREATE INDEX idx_events_ratio_date_country_category ON events(ratio DESC, date DESC, country, category);

-- Add comment for documentation
COMMENT ON COLUMN events.ratio IS 'Event importance ratio (1-100) for ranking and prioritization';
