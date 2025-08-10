ALTER TABLE files
    DROP COLUMN bucket_name,
    DROP COLUMN content_type,
    DROP COLUMN gcs_filename,
    DROP COLUMN public_url,
    DROP COLUMN size;
