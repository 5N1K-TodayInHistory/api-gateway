-- =====================================================
-- V8: Add event images column and remove image_url
-- =====================================================
-- This migration adds support for multiple images per event
-- Each image has: type (medium, large, large2x), image_url, is_default
-- Removes the old image_url column

-- Add images column to events table
ALTER TABLE events 
ADD COLUMN IF NOT EXISTS images JSONB DEFAULT '[]'::jsonb;

-- Create index on images column for JSONB queries (optional, for performance)
CREATE INDEX IF NOT EXISTS idx_events_images ON events USING GIN (images);

-- Migrate existing image_url data to images array format
-- This migration script converts single image_url to images array format
-- Only events with image_url but no images will be migrated
UPDATE events
SET images = jsonb_build_array(
    jsonb_build_object(
        'type', 'large',
        'image_url', image_url,
        'is_default', true
    )
)
WHERE image_url IS NOT NULL 
  AND (images IS NULL OR images = '[]'::jsonb);

-- Remove the old image_url column
ALTER TABLE events 
DROP COLUMN IF EXISTS image_url;

-- Add comment to document the column
COMMENT ON COLUMN events.images IS 'Array of event images with type (medium, large, large2x), image_url, and is_default flag. Format: [{"type": "medium", "image_url": "...", "is_default": true}, ...]';

