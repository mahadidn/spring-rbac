-- Permission
INSERT INTO permissions (name, description) VALUES
    ('user:read',   'Melihat data user'),
    ('user:create', 'Membuat user baru');

-- Role
INSERT INTO roles (name, description) VALUES
    ('ADMIN', 'Administrator'),
    ('STAFF', 'Staf biasa');

-- ADMIN get all permissions
INSERT INTO role_permissions (role_id, permission_id)
SELECT roles.id, permissions.id
FROM roles, permissions
WHERE roles.name = 'ADMIN';

-- STAFF only get user:read
INSERT INTO role_permissions (role_id, permission_id)
SELECT roles.id, permissions.id
FROM roles, permissions
WHERE roles.name = 'STAFF' AND permissions.name = 'user:read';

-- User example
INSERT INTO users (username, email, password_hash) VALUES
    ('budi', 'budi@mail.com', 'dummy-hash');

-- user budi get roles admin and staff
INSERT INTO user_roles (user_id, role_id)
SELECT users.id, roles.id
FROM users, roles
WHERE users.username = 'budi' AND roles.name IN ('ADMIN', 'STAFF');