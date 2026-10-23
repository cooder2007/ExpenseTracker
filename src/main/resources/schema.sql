CREATE DATABASE IF NOT EXISTS expensetracer;
USE expensetracer;

CREATE TABLE IF NOT EXISTS transactions (
    id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    type VARCHAR(10) NOT NULL,
    amount DECIMAL(12,2) NOT NULL,
    category VARCHAR(100) NOT NULL,
    transaction_date DATE NOT NULL,
    note VARCHAR(500),
    CONSTRAINT chk_transaction_type CHECK (type IN ('INCOME', 'EXPENSE')),
    CONSTRAINT chk_transaction_amount CHECK (amount > 0)
);
