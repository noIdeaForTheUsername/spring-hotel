INSERT INTO rooms (number, max_persons, class)
VALUES
    (101, 2, 'STANDARD'),
    (102, 3, 'STANDARD'),
    (103, 3, 'PREMIUM'),
    (201, 4, 'STANDARD'),
    (202, 5, 'STANDARD'),
    (203, 4, 'PREMIUM')
ON CONFLICT DO NOTHING;

INSERT INTO clients (id, first_name, last_name, phone_number)
VALUES
    (1, 'John', 'Smith', '+48567567567'),
    (2, 'Marty', 'McFly', '+48567567568'),
    (3, 'Catharina', 'Bakers', '+48567567569'),
    (4, 'Joe', 'Burrows', '+48567567560')
ON CONFLICT DO NOTHING;

INSERT INTO reservations (client_id, room_number, first_day, last_day)
VALUES
    (1, 101, '2026-08-09', '2026-08-12'),
    (2, 102, '2026-08-09', '2026-08-13'),
    (3, 103, '2026-08-01', '2026-08-09'),
    (4, 103, '2026-08-09', '2026-08-11'),
    (1, 101, '2025-01-01', '2025-01-21'),
    (3, 101, '2026-09-20', '2026-10-07')
ON CONFLICT DO NOTHING;