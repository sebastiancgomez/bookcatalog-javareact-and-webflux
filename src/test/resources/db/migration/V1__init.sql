-- Crear tabla 'book' si no existe
CREATE TABLE IF NOT EXISTS book (
                                    id SERIAL PRIMARY KEY,
                                    title VARCHAR(255) NOT NULL,
    author VARCHAR(255) NOT NULL,
    price NUMERIC(10,2) NOT NULL
    );

-- Opcional: insertar algunos registros iniciales
INSERT INTO book (title, author, price) VALUES
                                            ('Clean Code', 'Robert C. Martin', 45.99),
                                            ('Spring in Action', 'Craig Walls', 39.50),
                                            ('Reactive Spring', 'Josh Long', 49.00);