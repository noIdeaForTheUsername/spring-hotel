TRUNCATE TABLE reservations, clients, rooms CASCADE;

INSERT INTO rooms (number, max_persons, class)
VALUES
    (101, 2, 'STANDARD'),
    (102, 3, 'STANDARD'),
    (103, 3, 'PREMIUM'),
    (201, 4, 'STANDARD'),
    (202, 5, 'STANDARD'),
    (203, 4, 'PREMIUM');


INSERT INTO clients (id, first_name, last_name, phone_number)
VALUES
    (1, 'Client', 'One', '+48111111111'),
    (2, 'Client', 'Two', '+48222222222'),
    (3, 'Client', 'Three', '+48333333333'),
    (4, 'Client', 'Four', '+48444444444');


INSERT INTO reservations (id, client_id, room_number, first_day, last_day)
VALUES
    (1, 1, 101, '2026-08-10', '2026-08-15'),
    (2, 2, 102, '2026-08-20', '2026-08-25'),
    (3, 4, 101, '2026-08-20', '2026-08-25');


SELECT setval(
    pg_get_serial_sequence('clients', 'id'),
    (SELECT MAX(id) FROM clients)
);

SELECT setval(
    pg_get_serial_sequence('reservations', 'id'),
    (SELECT MAX(id) FROM reservations)
);