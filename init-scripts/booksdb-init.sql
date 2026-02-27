CREATE TABLE IF NOT EXISTS book (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    author VARCHAR(255) NOT NULL,
    price NUMERIC(10,2) NOT NULL
    );

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint WHERE conname = 'unique_title_author'
    ) THEN
ALTER TABLE book
    ADD CONSTRAINT unique_title_author UNIQUE (title, author);
END IF;
END
$$;

CREATE INDEX IF NOT EXISTS idx_book_title ON book(title);
CREATE INDEX IF NOT EXISTS idx_book_author ON book(author);

INSERT INTO book (title, author, price)
SELECT * FROM (VALUES
                   ('Clean Code', 'Robert C. Martin', 45.99),
                   ('Spring in Action', 'Craig Walls', 39.50),
                   ('Reactive Spring', 'Josh Long', 49.00)
              ) AS v(title, author, price)
WHERE NOT EXISTS (SELECT 1 FROM book);