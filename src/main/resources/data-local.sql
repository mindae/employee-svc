INSERT INTO employee (id, name, department)
SELECT '101', 'mindae', 'Finance'
WHERE NOT EXISTS (
    SELECT 1 FROM employee WHERE id = '101'
);

INSERT INTO employee (id, name, department)
SELECT '102', 'Alice', 'Engineering'
WHERE NOT EXISTS (
    SELECT 1 FROM employee WHERE id = '102'
);

INSERT INTO employee (id, name, department)
SELECT '103', 'Bob', 'Human Resources'
WHERE NOT EXISTS (
    SELECT 1 FROM employee WHERE id = '103'
);

INSERT INTO employee (id, name, department)
SELECT '104', 'Charlie', 'Engineering'
WHERE NOT EXISTS (
    SELECT 1 FROM employee WHERE id = '104'
);
