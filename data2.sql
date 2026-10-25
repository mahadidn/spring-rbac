CREATE TABLE products (
    id          BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name        VARCHAR(150)   NOT NULL,
    category    VARCHAR(50)    NOT NULL,
    price       NUMERIC(15, 2) NOT NULL CHECK (price >= 0),
    stock       INTEGER        NOT NULL DEFAULT 0 CHECK (stock >= 0),
    created_at  TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO products (name, category, price, stock) VALUES
    ('Baut Hex M12 x 50',          'BAUT',      3500.00,    1200),
    ('Baut Hex M16 x 70',          'BAUT',      6200.00,    800),
    ('Mur Hex M12',                'BAUT',      1200.00,    2500),
    ('Plat Baja A36 10mm',         'PLAT',      1850000.00, 40),
    ('Plat Baja A36 12mm',         'PLAT',      2200000.00, 35),
    ('Plat Baja A516 20mm',        'PLAT',      4100000.00, 12),
    ('Pipa Seamless 4 inch SCH40', 'PIPA',      2750000.00, 60),
    ('Pipa Seamless 6 inch SCH80', 'PIPA',      5900000.00, 18),
    ('Kawat Las E7018 3.2mm',      'KAWAT_LAS', 285000.00,  150),
    ('Kawat Las E6013 2.6mm',      'KAWAT_LAS', 195000.00,  220),
    ('Gasket Spiral Wound 4 inch', 'GASKET',    175000.00,  90),
    ('Flange Weld Neck 4 inch',    'FLANGE',    950000.00,  25);