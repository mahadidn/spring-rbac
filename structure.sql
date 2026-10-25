-- =====================================================
-- User table
-- =====================================================
CREATE TABLE users (
    id             BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    username       VARCHAR(50)  NOT NULL UNIQUE,
    email          VARCHAR(100) NOT NULL UNIQUE,
    password_hash  VARCHAR(255) NOT NULL,
    enabled        BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at     TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at     TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- =====================================================
-- Role Table
-- =====================================================
CREATE TABLE roles (
    id           BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name         VARCHAR(50)  NOT NULL UNIQUE,
    description  VARCHAR(255)
);

-- =====================================================
-- Permission Table
-- =====================================================
CREATE TABLE permissions (
    id           BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name         VARCHAR(100) NOT NULL UNIQUE,
    description  VARCHAR(255)
);

-- =====================================================
-- user <-> role (many-to-many)
-- =====================================================
CREATE TABLE user_roles (
    user_id  BIGINT NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    role_id  BIGINT NOT NULL REFERENCES roles (id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, role_id)
);

-- =====================================================
-- role <-> permission (many-to-many)
-- =====================================================
CREATE TABLE role_permissions (
    role_id        BIGINT NOT NULL REFERENCES roles (id) ON DELETE CASCADE,
    permission_id  BIGINT NOT NULL REFERENCES permissions (id) ON DELETE CASCADE,
    PRIMARY KEY (role_id, permission_id)
);