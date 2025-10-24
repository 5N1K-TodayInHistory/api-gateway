-- =====================================================
-- V4: Add Event Performance Fields
-- This migration adds month_day, score, and importance_reason fields to events table
-- for optimized "Today/Yesterday/Tomorrow" queries and event importance scoring
-- =====================================================

-- =====================================================
-- 1. ADD NEW COLUMNS TO EVENTS TABLE
-- =====================================================

-- Add month_day as generated column (MM-DD format)
ALTER TABLE events 
ADD COLUMN month_day CHAR(5) 
GENERATED ALWAYS AS (to_char(date, 'MM-DD')) STORED;

-- Add score field for importance (1-100)
ALTER TABLE events 
ADD COLUMN score SMALLINT 
CHECK (score >= 1 AND score <= 100);

-- Add importance_reason as JSONB for multilingual explanations
ALTER TABLE events 
ADD COLUMN importance_reason JSONB 
DEFAULT '{}'::jsonb;

-- =====================================================
-- 2. CREATE PERFORMANCE INDEXES
-- =====================================================

-- Index for month_day queries (single column)
CREATE INDEX idx_events_month_day ON events(month_day);

-- Composite index for country + month_day queries (most important for performance)
CREATE INDEX idx_events_country_month_day ON events(country, month_day);

-- Index for score-based ordering (DESC for highest scores first)
CREATE INDEX idx_events_score_desc ON events(score DESC);

-- Composite index for country + month_day + score (optimal for our use case)
CREATE INDEX idx_events_country_month_day_score ON events(country, month_day, score DESC);

-- =====================================================
-- 3. UPDATE EXISTING DATA WITH DEFAULT VALUES
-- =====================================================

-- Set default score for existing events (50 = medium importance)
UPDATE events 
SET score = 50 
WHERE score IS NULL;

-- Set default importance_reason for existing events
UPDATE events 
SET importance_reason = '{"en": "Historical event", "tr": "Tarihi olay", "es": "Evento histórico"}'::jsonb
WHERE importance_reason = '{}'::jsonb;

-- =====================================================
-- 4. ADD COLUMN COMMENTS FOR DOCUMENTATION
-- =====================================================

COMMENT ON COLUMN events.month_day IS 'Generated column storing date in MM-DD format for efficient date-based queries';
COMMENT ON COLUMN events.score IS 'Importance score from 1-100, higher values indicate more important events';
COMMENT ON COLUMN events.importance_reason IS 'Multilingual explanation of why this event is important, stored as JSONB';

-- =====================================================
-- 5. VERIFY SCHEMA CHANGES
-- =====================================================

-- This query can be used to verify the new columns exist
-- SELECT column_name, data_type, is_nullable, column_default 
-- FROM information_schema.columns 
-- WHERE table_name = 'events' 
-- AND column_name IN ('month_day', 'score', 'importance_reason')
-- ORDER BY column_name;
