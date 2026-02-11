-- Tabela Transactions
CREATE TABLE transactions (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    group_id UUID NOT NULL REFERENCES groups(id) ON DELETE CASCADE,
    user_id UUID NOT NULL REFERENCES users(id),
    type VARCHAR(50) NOT NULL,
    category VARCHAR(50),
    name VARCHAR(255),
    amount DECIMAL(10,2),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_transactions_group_id
ON transactions(group_id, created_at DESC);

CREATE INDEX idx_transactions_user_id
ON transactions(user_id);

-- Adicionar category em expenses
ALTER TABLE expenses
ADD COLUMN category VARCHAR(100);
