ALTER TABLE contacts
    ADD COLUMN IF NOT EXISTS profile_image_url VARCHAR(2048);
