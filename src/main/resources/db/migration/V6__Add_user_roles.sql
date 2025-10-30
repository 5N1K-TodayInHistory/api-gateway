-- Add role column to users table
ALTER TABLE users ADD COLUMN role VARCHAR(20) NOT NULL DEFAULT 'USER';

-- Create index on role column for better query performance
CREATE INDEX idx_users_role ON users(role);

-- Update existing users to have USER role (this is already the default, but explicit for clarity)
UPDATE users SET role = 'USER' WHERE role IS NULL;

-- Set admin role for specific email
UPDATE users SET role = 'ADMIN' WHERE email = 'cagdaskarademir@gmail.com';

-- If the user doesn't exist yet, we'll handle this in the application code
-- This migration ensures the column exists and sets up the structure
