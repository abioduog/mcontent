INSERT INTO hello(name) VALUES ('Spring Boot World');
INSERT INTO hello(name) VALUES ('Nolwenture');

INSERT INTO users (username, password, email) VALUES ('admin', '$2a$10$4cX3TGhLiME0K0MJ8AgrA.QdiH/KGv4BOUCNZLisoCMIj0pEeuiGS', 'admin@admin.com');
INSERT INTO roles (name) VALUES ('ADMIN');
INSERT INTO users_roles (users_id, roles_id) VALUES (
	(SELECT id FROM users WHERE username='admin'),
	(SELECT id FROM roles WHERE name='ADMIN')
);

INSERT INTO users (username, password, email) VALUES ('provider', '$2a$10$4cX3TGhLiME0K0MJ8AgrA.QdiH/KGv4BOUCNZLisoCMIj0pEeuiGS', 'provider@provider.com');
INSERT INTO roles (name) VALUES ('PROVIDER');
INSERT INTO users_roles (users_id, roles_id) VALUES (
	(SELECT id FROM users WHERE username='provider'),
	(SELECT id FROM roles WHERE name='PROVIDER')
);

INSERT INTO services (keyword, short_code, operator, provider_id, subscription_period, delivery_time) VALUES ('READ', 33070, 'MTN', (SELECT id FROM users WHERE username LIKE 'provider'), 7, 'T0800');
