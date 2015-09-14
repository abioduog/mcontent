INSERT INTO roles (name) VALUES ('ADMIN');
INSERT INTO roles (name) VALUES ('PROVIDER');

INSERT INTO users (username, password, email, active) VALUES ('admin', '$2a$10$4cX3TGhLiME0K0MJ8AgrA.QdiH/KGv4BOUCNZLisoCMIj0pEeuiGS', 'admin@admin.com', true);
INSERT INTO users_roles (users_id, roles_id) VALUES (
	(SELECT id FROM users WHERE username='admin'),
	(SELECT id FROM roles WHERE name='ADMIN')
);

INSERT INTO users (username, password, email, active) VALUES ('provider', '$2a$10$4cX3TGhLiME0K0MJ8AgrA.QdiH/KGv4BOUCNZLisoCMIj0pEeuiGS', 'provider@provider.com', true);
INSERT INTO users_roles (users_id, roles_id) VALUES (
	(SELECT id FROM users WHERE username='provider'),
	(SELECT id FROM roles WHERE name='PROVIDER')
);

INSERT INTO users (username, password, email, active) VALUES ('a_provider', '$2a$10$4cX3TGhLiME0K0MJ8AgrA.QdiH/KGv4BOUCNZLisoCMIj0pEeuiGS', 'a_provider@provider.com', true);
INSERT INTO users_roles (users_id, roles_id) VALUES (
	(SELECT id FROM users WHERE username='a_provider'),
	(SELECT id FROM roles WHERE name='PROVIDER')
);

INSERT INTO users (username, password, email, active) VALUES ('z_provider', '$2a$10$4cX3TGhLiME0K0MJ8AgrA.QdiH/KGv4BOUCNZLisoCMIj0pEeuiGS', 'provider_z@provider.com', true);
INSERT INTO users_roles (users_id, roles_id) VALUES (
	(SELECT id FROM users WHERE username='z_provider'),
	(SELECT id FROM roles WHERE name='PROVIDER')
);

INSERT INTO delivery_pipes (name, deliverable_type) VALUES ('Test pipe','SCHEDULED');
INSERT INTO delivery_pipes_providers (delivery_pipes_id, providers_id) VALUES (
        (SELECT id FROM delivery_pipes WHERE name='Test pipe'),
        (SELECT id FROM users WHERE username='provider'));

INSERT INTO services (keyword, short_code, operator, unsubscribe_keyword, subscription_period, delivery_pipe_id, delivery_time) VALUES ('READ', 33070, 'MTN', 'UNSUBSCRIBE READ', 7, (SELECT id FROM delivery_pipes WHERE name='Test pipe'), 'T1600');

INSERT INTO contents (content_type) VALUES ('CUSTOM');
INSERT INTO custom_contents (id, short_uuid, content) VALUES (
        (SELECT id FROM contents WHERE content_type='CUSTOM'),
        '9d37c676-2991-4267-9fcc-1bb133489c8c',
        'testi-content'
);

INSERT INTO deliverables (content_id, delivery_pipe_id, deliverable_type) VALUES (
        (SELECT id FROM custom_contents WHERE short_uuid='9d37c676-2991-4267-9fcc-1bb133489c8c'),
        (SELECT id FROM delivery_pipes WHERE name='Test pipe'),
        'SCHEDULED'
);
INSERT INTO scheduled_deliverables (id, delivery_date) VALUES (
        (SELECT id FROM deliverables WHERE
                content_id=(SELECT id FROM custom_contents WHERE short_uuid='9d37c676-2991-4267-9fcc-1bb133489c8c') AND
                delivery_pipe_id=(SELECT id FROM delivery_pipes WHERE name='Test pipe') AND
                deliverable_type='SCHEDULED'
        ),
        CURDATE()
);

INSERT INTO delivery_pipes (name, deliverable_type) VALUES ('Test pipe 2','SCHEDULED');
INSERT INTO delivery_pipes_providers (delivery_pipes_id, providers_id) VALUES (
        (SELECT id FROM delivery_pipes WHERE name='Test pipe 2'),
        (SELECT id FROM users WHERE username='a_provider'));

INSERT INTO delivery_pipes_providers (delivery_pipes_id, providers_id) VALUES (
        (SELECT id FROM delivery_pipes WHERE name='Test pipe 2'),
        (SELECT id FROM users WHERE username='z_provider'));

INSERT INTO services (keyword, short_code, operator, unsubscribe_keyword, subscription_period, delivery_pipe_id, delivery_time) VALUES ('READ2', 33070, 'MTN', 'UNSUBSCRIBE READ2', 14, (SELECT id FROM delivery_pipes WHERE name='Test pipe 2'), 'T1000');

INSERT INTO contents (content_type) VALUES ('RSS');
INSERT INTO rss_contents (id, title, description, link) VALUES (
        (SELECT id FROM contents WHERE content_type='RSS'),
        'testi-rss',
        'tämä on RSS-testiviesti',
        'http://www.mnewservice.com/'
);

INSERT INTO deliverables (content_id, delivery_pipe_id, deliverable_type) VALUES (
        (SELECT id FROM rss_contents WHERE title='testi-rss'),
        (SELECT id FROM delivery_pipes WHERE name='Test pipe 2'),
        'SCHEDULED'
);
INSERT INTO scheduled_deliverables (id, delivery_date) VALUES (
        (SELECT id FROM deliverables WHERE
                content_id=(SELECT id FROM rss_contents WHERE title='testi-rss') AND
                delivery_pipe_id=(SELECT id FROM delivery_pipes WHERE name='Test pipe 2') AND
                deliverable_type='SCHEDULED'
        ),
        CURDATE()
);

INSERT INTO subscriptionperiods (start, end, message, message_id, operator, original_timestamp, sender, short_code) VALUES (
        CURDATE(),
        CURDATE() + INTERVAL 7 DAY,
        "READ",
        1,
        "MTN",
        "8/24/2015 15:37 AM",
        "2348183525258",
        33070
);

INSERT INTO phonenumbers (number) VALUES ("2348183525258");
INSERT INTO subscribers (phone_id) VALUES (
        (SELECT id FROM phonenumbers WHERE number='2348183525258')
);

INSERT INTO subscriptions (service_id, subscriber_id) VALUES (
        (SELECT id FROM services WHERE keyword='READ' AND short_code=33070 AND operator='MTN'),
        (SELECT S.id FROM subscribers S JOIN phonenumbers P ON S.phone_id=P.id WHERE P.number='2348183525258')
);

INSERT INTO subscriptions_periods (subscriptions_id, periods_id) VALUES (
        (SELECT SUBP.id
            FROM subscriptions SUBP
                JOIN services SERV ON SUBP.service_id=SERV.id
                JOIN subscribers SUBB ON SUBP.subscriber_id=SUBB.id
                JOIN phonenumbers PHON ON SUBB.phone_id=PHON.id
            WHERE SERV.keyword='READ' AND SERV.short_code=33070 AND SERV.operator='MTN' AND PHON.number='2348183525258'),
        (SELECT id FROM subscriptionperiods WHERE message='READ' AND short_code=33070 AND operator='MTN')
);
