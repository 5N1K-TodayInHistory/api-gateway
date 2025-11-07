-- =====================================================
-- V7: Add is_active column to countries table
-- =====================================================

ALTER TABLE countries 
ADD COLUMN is_active BOOLEAN NOT NULL DEFAULT TRUE;

