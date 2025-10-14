-- Add username column to users table
ALTER TABLE users ADD COLUMN username VARCHAR(255) UNIQUE NOT NULL DEFAULT '';

-- Update existing users with email as username (for OAuth2 users)
UPDATE users SET username = email WHERE email IS NOT NULL;

-- For users without email (guest users), generate unique usernames
UPDATE users SET username = 'guest_' || id WHERE email IS NULL;

-- Remove the default constraint
ALTER TABLE users ALTER COLUMN username DROP DEFAULT;

-- Create index for username
CREATE INDEX idx_users_username ON users(username);
