CREATE TABLE IF NOT EXISTS book (
                                    id SERIAL PRIMARY KEY,
                                    title VARCHAR(255),
    author VARCHAR(255),
    price NUMERIC
    );