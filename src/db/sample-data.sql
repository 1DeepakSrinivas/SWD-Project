-- Employee Management System Sample Data
-- Safe to re-run: Uses INSERT IGNORE and ON DUPLICATE KEY UPDATE

INSERT IGNORE INTO division (division_id, name) VALUES
    (1, 'Engineering'),
    (2, 'Sales'),
    (3, 'Marketing'),
    (4, 'Human Resources'),
    (5, 'Finance');

INSERT IGNORE INTO job_titles (job_title_id, title) VALUES
    (1, 'Software Engineer'),
    (2, 'Senior Software Engineer'),
    (3, 'Lead Software Engineer'),
    (4, 'Sales Representative'),
    (5, 'Sales Manager'),
    (6, 'Marketing Specialist'),
    (7, 'Marketing Manager'),
    (8, 'HR Coordinator'),
    (9, 'HR Manager'),
    (10, 'Financial Analyst'),
    (11, 'Finance Manager'),
    (12, 'Product Manager'),
    (13, 'Quality Assurance Engineer'),
    (14, 'DevOps Engineer'),
    (15, 'Business Analyst');

INSERT IGNORE INTO employees (employee_id, first_name, last_name, SSN, email) VALUES
    (1, 'John', 'Smith', '123456789', 'john.smith@company.com'),
    (2, 'Jane', 'Doe', '234567890', 'jane.doe@company.com'),
    (3, 'Michael', 'Johnson', '345678901', 'michael.johnson@company.com'),
    (4, 'Emily', 'Williams', '456789012', 'emily.williams@company.com'),
    (5, 'David', 'Brown', '567890123', 'david.brown@company.com'),
    (6, 'Sarah', 'Davis', '678901234', 'sarah.davis@company.com'),
    (7, 'Robert', 'Miller', '789012345', 'robert.miller@company.com'),
    (8, 'Jessica', 'Wilson', '890123456', 'jessica.wilson@company.com'),
    (9, 'Christopher', 'Moore', '901234567', 'christopher.moore@company.com'),
    (10, 'Amanda', 'Taylor', '012345678', 'amanda.taylor@company.com'),
    (11, 'Daniel', 'Anderson', '112233445', 'daniel.anderson@company.com'),
    (12, 'Lisa', 'Thomas', '223344556', 'lisa.thomas@company.com'),
    (13, 'Matthew', 'Jackson', '334455667', 'matthew.jackson@company.com'),
    (14, 'Ashley', 'White', '445566778', 'ashley.white@company.com'),
    (15, 'James', 'Harris', '556677889', 'james.harris@company.com');

INSERT IGNORE INTO employee_division (employee_id, division_id) VALUES
    (1, 1), (2, 1), (3, 1), (4, 1), (5, 1),
    (6, 2), (7, 2), (8, 2),
    (9, 3), (10, 3), (11, 3),
    (12, 4), (13, 4),
    (14, 5), (15, 5);

INSERT IGNORE INTO employee_job_titles (employee_id, job_title_id) VALUES
    (1, 1), (2, 2), (3, 3), (4, 1), (5, 13),
    (6, 4), (7, 5), (8, 4),
    (9, 6), (10, 7), (11, 12),
    (12, 8), (13, 9),
    (14, 10), (15, 11);

INSERT INTO payroll (employee_id, amount, pay_period_start, pay_period_end)
VALUES
    (1, 5000.00, '2025-01-01', '2025-01-15'),
    (1, 5000.00, '2025-01-16', '2025-01-31'),
    (2, 6500.00, '2025-01-01', '2025-01-15'),
    (2, 6500.00, '2025-01-16', '2025-01-31'),
    (3, 8000.00, '2025-01-01', '2025-01-15'),
    (4, 5000.00, '2025-01-01', '2025-01-15'),
    (5, 5500.00, '2025-01-01', '2025-01-15'),
    (6, 4500.00, '2025-01-01', '2025-01-15'),
    (6, 4500.00, '2025-01-16', '2025-01-31'),
    (7, 6000.00, '2025-01-01', '2025-01-15'),
    (8, 4500.00, '2025-01-01', '2025-01-15'),
    (9, 4800.00, '2025-01-01', '2025-01-15'),
    (10, 7000.00, '2025-01-01', '2025-01-15'),
    (11, 7500.00, '2025-01-01', '2025-01-15'),
    (12, 4200.00, '2025-01-01', '2025-01-15'),
    (13, 5800.00, '2025-01-01', '2025-01-15'),
    (14, 5200.00, '2025-01-01', '2025-01-15'),
    (15, 6800.00, '2025-01-01', '2025-01-15'),
    (1, 5000.00, '2025-02-01', '2025-02-15'),
    (2, 6500.00, '2025-02-01', '2025-02-15'),
    (3, 8000.00, '2025-02-01', '2025-02-15'),
    (6, 4500.00, '2025-02-01', '2025-02-15'),
    (7, 6000.00, '2025-02-01', '2025-02-15'),
    (9, 4800.00, '2025-02-01', '2025-02-15'),
    (10, 7000.00, '2025-02-01', '2025-02-15'),
    (11, 7500.00, '2025-02-01', '2025-02-15'),
    (14, 5200.00, '2025-02-01', '2025-02-15')
ON DUPLICATE KEY UPDATE
    amount = VALUES(amount),
    pay_period_start = VALUES(pay_period_start),
    pay_period_end = VALUES(pay_period_end);

