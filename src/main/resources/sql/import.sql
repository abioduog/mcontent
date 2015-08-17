INSERT INTO hello(name) VALUES ('Spring Boot World');
INSERT INTO hello(name) VALUES ('Nolwenture');

INSERT INTO users (username, password, email) VALUES ('admin', '$2a$10$4cX3TGhLiME0K0MJ8AgrA.QdiH/KGv4BOUCNZLisoCMIj0pEeuiGS', 'admin@nolwenture.com');
INSERT INTO roles (role_name) VALUES ('ADMIN');

INSERT INTO users_roles (users_id, roles_id) VALUES (
	(SELECT id FROM users WHERE username LIKE 'admin'),
	(SELECT id FROM roles WHERE role_name LIKE 'ADMIN')
);
