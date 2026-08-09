TRUNCATE TABLE rooms CASCADE;

INSERT INTO rooms (number, max_persons, class)
VALUES
    (101, 2, 'STANDARD'),
    (102, 3, 'STANDARD'),
    (103, 3, 'PREMIUM'),
    (201, 4, 'STANDARD'),
    (202, 5, 'STANDARD'),
    (203, 4, 'PREMIUM');