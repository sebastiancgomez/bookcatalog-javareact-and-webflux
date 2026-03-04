-- 1️⃣ Fix existing rows
UPDATE book
SET publish_date = '2000-01-01'
WHERE publish_date IS NULL;

-- 2️⃣ Enforce constraint
ALTER TABLE book
    ALTER COLUMN publish_date SET NOT NULL;