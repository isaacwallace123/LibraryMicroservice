INSERT INTO book (bookid, authorid, title, genre, publisher, released)
VALUES
    ('c1e2b3d4-5f6e-7a8b-9c0d-a112b2c3d4e5', '123e4567-e89b-12d3-a456-556642440000', 'The Lost World', 'Adventure', 'Penguin Books', CURRENT_TIMESTAMP),
    ('d3f4b5c6-7f8e-9d2b-0c1d-b223b4c5d6f7', '223e4567-e89b-12d3-a456-556642440001', 'Damaged Chronicles', 'Mystery', 'HarperCollins', CURRENT_TIMESTAMP),
    ('e4g5c6d7-8f9e-0d3b-1c2d-c334d5e6f7g8', '323e4567-e89b-12d3-a456-556642440002', 'Borrowed Time', 'Thriller', 'Random House', CURRENT_TIMESTAMP),
    ('f5h6d7e8-9f1e-2d4b-3c2d-d445e6f7g809', '423e4567-e89b-12d3-a456-556642440003', 'Adventure Calls', 'Adventure', 'Scholastic', CURRENT_TIMESTAMP),
    ('g6i7e8f9-0f2e-3d5b-4c3d-e556f7g8i9j0', '523e4567-e89b-12d3-a456-556642440004', 'The Damaged Tome', 'Fantasy', 'Simon & Schuster', CURRENT_TIMESTAMP),
    ('h7j8f9g0-1f3e-4d6b-5c4d-f667g8i0j1k1', '623e4567-e89b-12d3-a456-556642440005', 'Inventory Mystery', 'Drama', 'Macmillan', CURRENT_TIMESTAMP),
    ('i8k9g0h1-2f4e-5d7b-6c5d-g778h9j1k212', '723e4567-e89b-12d3-a456-556642440006', 'Borrowed Hearts', 'Suspense', 'Bloomsbury', CURRENT_TIMESTAMP),
    ('j9l0h1i2-3f5e-6d8b-7c6d-h889i0j2k313', '823e4567-e89b-12d3-a456-556642440007', 'Shelf Life', 'Non-fiction', 'Oxford Press', CURRENT_TIMESTAMP),
    ('k0m1i2j3-4f6e-7d9b-8c7d-i990j1k3l414', '923e4567-e89b-12d3-a456-556642440008', 'Broken Stories', 'Horror', 'Tor Books', CURRENT_TIMESTAMP),
    ('l1n2j3k4-5f7e-8d0b-9c8d-j101k2l4n515', 'a23e4567-e89b-12d3-a456-556642440009', 'Stocked Future', 'Sci-Fi', 'Orbit', CURRENT_TIMESTAMP);

INSERT INTO inventories (inventoryid, bookid, quantity)
VALUES
    ('d0e5678d-20d1-4d22-9512-b99df9e6f101', 'c1e2b3d4-5f6e-7a8b-9c0d-a112b2c3d4e5', 10),
    ('e2f5689e-5f7d-4c92-9522-c22df9e7g102', 'd3f4b5c6-7f8e-9d2b-0c1d-b223b4c5d6f7', 5),
    ('f3g6791f-6f8d-5d03-9633-d33df0g8h203', 'e4g5c6d7-8f9e-0d3b-1c2d-c334d5e6f7g8', 2),
    ('g4h7802g-7f9e-6e14-9744-e44df1h9i304', 'f5h6d7e8-9f1e-2d4b-3c2d-d445e6f7g809', 8),
    ('h5i8913h-8f0e-7f25-9855-f55df2i0j405', 'g6i7e8f9-0f2e-3d5b-4c3d-e556f7g8i9j0', 3),
    ('i6j9024i-9f1e-8f36-9966-g66df3j1k506', 'h7j8f9g0-1f3e-4d6b-5c4d-f667g8i0j1k1', 15),
    ('j7k0135j-0f2e-9f47-a007-h77df4k2l607', 'i8k9g0h1-2f4e-5d7b-6c5d-g778h9j1k212', 7),
    ('k8l1246k-1f3e-0f58-b118-i88df5l3m708', 'j9l0h1i2-3f5e-6d8b-7c6d-h889i0j2k313', 20),
    ('l9m2357l-2f4e-1f69-c229-j99df6m4n809', 'k0m1i2j3-4f6e-7d9b-8c7d-i990j1k3l414', 0),
    ('m0n3468m-3f5e-2f70-d339-k00df7n5o910', 'l1n2j3k4-5f7e-8d0b-9c8d-j101k2l4n515', 12);