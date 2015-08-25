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

INSERT INTO services (keyword, short_code, operator, provider_id, subscription_period, delivery_time) VALUES ('READ', 33070, 'MTN', (SELECT id FROM users WHERE username LIKE 'provider'), 7, 'T1600');

INSERT INTO contents (content_type) VALUES ('CUSTOM');
INSERT INTO custom_contents (id, short_uuid, content) VALUES (
        (SELECT id FROM contents WHERE content_type='CUSTOM'),
        '9d37c676-2991-4267-9fcc-1bb133489c8c',
        'testi-content'
);

INSERT INTO deliverables (content_id, service_id, deliverable_type) VALUES (
        (SELECT id FROM custom_contents WHERE short_uuid='9d37c676-2991-4267-9fcc-1bb133489c8c'),
        (SELECT id FROM services WHERE keyword='READ' AND short_code=33070 AND operator='MTN'),
        'SCHEDULED'
);
INSERT INTO scheduled_deliverables (id, delivery_date) VALUES (
        (SELECT id FROM deliverables WHERE
                content_id=(SELECT id FROM custom_contents WHERE short_uuid='9d37c676-2991-4267-9fcc-1bb133489c8c') AND
                service_id=(SELECT id FROM services WHERE keyword='READ' AND short_code=33070 AND operator='MTN') AND
                deliverable_type='SCHEDULED'
        ),
        CURDATE()
);