ALTER TABLE transactions
ADD COLUMN expense_id UUID;

ALTER TABLE transactions
ADD CONSTRAINT fk_transactions_expense
FOREIGN KEY (expense_id)
REFERENCES expenses(id)
ON DELETE CASCADE;

CREATE INDEX idx_transactions_expense_id
ON transactions(expense_id);
