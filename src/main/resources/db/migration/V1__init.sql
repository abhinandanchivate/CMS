CREATE TABLE users (
    id BINARY(16) PRIMARY KEY,
    email VARCHAR(191) NOT NULL UNIQUE,
    password_hash VARCHAR(191) NOT NULL,
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    mobile VARCHAR(32),
    dob DATE,
    address VARCHAR(255),
    enabled BOOLEAN NOT NULL DEFAULT FALSE,
    mfa_enabled BOOLEAN NOT NULL DEFAULT FALSE,
    transaction_pin_hash VARCHAR(191),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE roles (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) UNIQUE NOT NULL
);

CREATE TABLE user_roles (
    user_id BINARY(16) NOT NULL,
    role_id INT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_user_roles_user FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_user_roles_role FOREIGN KEY (role_id) REFERENCES roles (id)
);

CREATE TABLE refresh_tokens (
    token CHAR(36) PRIMARY KEY,
    user_id BINARY(16) NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    revoked BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_refresh_token_user FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE TABLE accounts (
    id BINARY(16) PRIMARY KEY,
    user_id BINARY(16) NOT NULL,
    type ENUM('SAVINGS','CURRENT') NOT NULL,
    number_mask VARCHAR(32) NOT NULL,
    ifsc VARCHAR(16) NOT NULL,
    branch VARCHAR(128),
    balance DECIMAL(18,2) NOT NULL DEFAULT 0,
    currency CHAR(3) NOT NULL DEFAULT 'INR',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_account_user FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE TABLE transactions (
    id BINARY(16) PRIMARY KEY,
    account_id BINARY(16) NOT NULL,
    type ENUM('CREDIT','DEBIT') NOT NULL,
    narration VARCHAR(255),
    amount DECIMAL(18,2) NOT NULL,
    balance_after DECIMAL(18,2) NOT NULL,
    posted_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_transaction_account FOREIGN KEY (account_id) REFERENCES accounts (id),
    INDEX idx_transactions_account_posted_at (account_id, posted_at)
);

CREATE TABLE beneficiaries (
    id BINARY(16) PRIMARY KEY,
    user_id BINARY(16) NOT NULL,
    name VARCHAR(150) NOT NULL,
    account_number VARCHAR(64) NOT NULL,
    ifsc VARCHAR(16) NOT NULL,
    bank_name VARCHAR(128),
    verified BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_beneficiary_user FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE TABLE transfers (
    id BINARY(16) PRIMARY KEY,
    user_id BINARY(16) NOT NULL,
    from_account BINARY(16) NOT NULL,
    beneficiary_id BINARY(16) NOT NULL,
    amount DECIMAL(18,2) NOT NULL,
    currency CHAR(3) NOT NULL,
    purpose VARCHAR(255),
    status ENUM('PROCESSING','SETTLED','FAILED','REVERSED') NOT NULL,
    reason VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_transfer_user FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_transfer_from_account FOREIGN KEY (from_account) REFERENCES accounts (id),
    CONSTRAINT fk_transfer_beneficiary FOREIGN KEY (beneficiary_id) REFERENCES beneficiaries (id)
);

CREATE TABLE cards (
    id BINARY(16) PRIMARY KEY,
    user_id BINARY(16) NOT NULL,
    masked VARCHAR(32) NOT NULL,
    type ENUM('DEBIT','CREDIT') NOT NULL,
    expires VARCHAR(7) NOT NULL,
    locked BOOLEAN NOT NULL DEFAULT FALSE,
    international BOOLEAN NOT NULL DEFAULT TRUE,
    ecom BOOLEAN NOT NULL DEFAULT TRUE,
    atm_daily_limit INT,
    pos_daily_limit INT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_card_user FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE TABLE tickets (
    id BINARY(16) PRIMARY KEY,
    user_id BINARY(16) NOT NULL,
    reference VARCHAR(32) NOT NULL UNIQUE,
    category ENUM('TRANSACTION','CARD','APP','OTHER','ACCOUNT') NOT NULL,
    subject VARCHAR(255) NOT NULL,
    status ENUM('OPEN','IN_PROGRESS','RESOLVED','CLOSED') NOT NULL DEFAULT 'OPEN',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_ticket_user FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE TABLE ticket_messages (
    id BINARY(16) PRIMARY KEY,
    ticket_id BINARY(16) NOT NULL,
    by_role ENUM('CUSTOMER','SUPPORT','TELLER','ADMIN') NOT NULL,
    message TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_ticket_message_ticket FOREIGN KEY (ticket_id) REFERENCES tickets (id)
);

CREATE INDEX idx_ticket_user ON tickets (user_id);
CREATE UNIQUE INDEX ux_ticket_reference ON tickets (reference);
CREATE INDEX idx_card_user ON cards (user_id);
CREATE INDEX idx_beneficiary_user ON beneficiaries (user_id);
