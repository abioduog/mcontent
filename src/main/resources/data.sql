INSERT INTO roles (name) VALUES ('ADMIN');
INSERT INTO roles (name) VALUES ('PROVIDER');

INSERT INTO users (username, password, active) VALUES (
        'admin',
        '$2a$10$4cX3TGhLiME0K0MJ8AgrA.QdiH/KGv4BOUCNZLisoCMIj0pEeuiGS',
        true
);
INSERT INTO users_roles (users_id, roles_id) VALUES (
	(SELECT id FROM users WHERE username='admin'),
	(SELECT id FROM roles WHERE name='ADMIN')
);

INSERT INTO users (username, password, active) VALUES (
        'provider',
        '$2a$10$4cX3TGhLiME0K0MJ8AgrA.QdiH/KGv4BOUCNZLisoCMIj0pEeuiGS',
        true
);
INSERT INTO users_roles (users_id, roles_id) VALUES (
	(SELECT id FROM users WHERE username='provider'),
	(SELECT id FROM roles WHERE name='PROVIDER')
);
INSERT INTO emails (address) VALUES ('popular@provider.com');
INSERT INTO emails (address) VALUES ('contact.popular@provider.com');
INSERT INTO phonenumbers (number) VALUES ('1234567890');
INSERT INTO phonenumbers (number) VALUES ('1234567891');
INSERT INTO providers (
        name,
        state,
        country,
        email_id,
        phone_id,
        user_id,
        name_of_contact_person,
        position_of_contact_person,
        email_of_contact_person_id,
        phone_of_contact_person_id,
        content_name,
        content_description)
VALUES (
        'Popular Provider',
        'East State',
        'Finland',
        (SELECT id FROM emails WHERE address='popular@provider.com'),
        (SELECT id FROM phonenumbers WHERE number='1234567890'),
        (SELECT id FROM users WHERE username='provider'),
        'Popular Contact',
        'Head of Contacts',
        (SELECT id FROM emails WHERE address='contact.popular@provider.com'),
        (SELECT id FROM phonenumbers WHERE number='1234567891'),
        'Popular Content',
        'Provides the most popular content shared on the Social Media.'
);
INSERT INTO binary_contents (name, content_type, content) VALUES (
        'popularity_agreement.txt',
        'text/plain',
        'testisopimus'
);
INSERT INTO providers_correspondences (
        providers_id,
        correspondences_id)
VALUES (
        (SELECT id FROM providers WHERE name='Popular Provider'),
        (SELECT id FROM binary_contents WHERE name='popularity_agreement.txt')
);

INSERT INTO users (username, password, active) VALUES (
        'a_provider',
        '$2a$10$4cX3TGhLiME0K0MJ8AgrA.QdiH/KGv4BOUCNZLisoCMIj0pEeuiGS',
        true
);
INSERT INTO users_roles (users_id, roles_id) VALUES (
	(SELECT id FROM users WHERE username='a_provider'),
	(SELECT id FROM roles WHERE name='PROVIDER')
);
INSERT INTO emails (address) VALUES ('adorable@provider.com');
INSERT INTO emails (address) VALUES ('contact.adorable@provider.com');
INSERT INTO phonenumbers (number) VALUES ('1234567892');
INSERT INTO phonenumbers (number) VALUES ('1234567893');
INSERT INTO providers (
        name,
        state,
        country,
        email_id,
        phone_id,
        user_id,
        name_of_contact_person,
        position_of_contact_person,
        email_of_contact_person_id,
        phone_of_contact_person_id,
        content_name,
        content_description)
VALUES (
        'Adorable Provider',
        'West State',
        'Finland',
        (SELECT id FROM emails WHERE address='adorable@provider.com'),
        (SELECT id FROM phonenumbers WHERE number='1234567892'),
        (SELECT id FROM users WHERE username='a_provider'),
        'Adorable Contact',
        'Head of Contacts',
        (SELECT id FROM emails WHERE address='contact.adorable@provider.com'),
        (SELECT id FROM phonenumbers WHERE number='1234567893'),
        'Adorable Content',
        'Most adorable cat photos from the late 90s.'
);

INSERT INTO users (username, password, active) VALUES (
        'z_provider',
        '$2a$10$4cX3TGhLiME0K0MJ8AgrA.QdiH/KGv4BOUCNZLisoCMIj0pEeuiGS',
        true
);
INSERT INTO users_roles (users_id, roles_id) VALUES (
	(SELECT id FROM users WHERE username='z_provider'),
	(SELECT id FROM roles WHERE name='PROVIDER')
);
INSERT INTO emails (address) VALUES ('zestful@provider.com');
INSERT INTO emails (address) VALUES ('contact.zestful@provider.com');
INSERT INTO phonenumbers (number) VALUES ('1234567894');
INSERT INTO phonenumbers (number) VALUES ('1234567895');
INSERT INTO providers (
        name,
        state,
        country,
        email_id,
        phone_id,
        user_id,
        name_of_contact_person,
        position_of_contact_person,
        email_of_contact_person_id,
        phone_of_contact_person_id,
        content_name,
        content_description)
VALUES (
        'Zestful Provider',
        'Lapland State',
        'Finland',
        (SELECT id FROM emails WHERE address='zestful@provider.com'),
        (SELECT id FROM phonenumbers WHERE number='1234567894'),
        (SELECT id FROM users WHERE username='z_provider'),
        'Zestful Contact',
        'Head of Contacts',
        (SELECT id FROM emails WHERE address='contact.zestful@provider.com'),
        (SELECT id FROM phonenumbers WHERE number='1234567895'),
        'Zestful Content',
        'Presenting Zestful lifestyle of the Sami people'
);

INSERT INTO delivery_pipes (name, deliverable_type, theme) VALUES ('Test pipe','SERIES', 'comics');
INSERT INTO delivery_pipes_providers (delivery_pipes_id, providers_id) VALUES (
        (SELECT id FROM delivery_pipes WHERE name='Test pipe'),
        (SELECT id FROM users WHERE username='provider'));

INSERT INTO services (
        keyword,
        short_code,
        operator,
        unsubscribe_keyword,
        welcome_message,
        renew_message,
        expire_message,
        unsubscribe_message,
        subscription_period,
        delivery_pipe_id,
        delivery_time)
VALUES (
        'READ',
        33070,
        'MTN',
        'UNSUBSCRIBE_READ',
        'Welcome to an excellent SMS service 1. Your subscription is now valid for the next %d days.',
        'You have renewed subscription for the excellent SMS service 1. Your subscription is now valid %d more days.',
        'Your SMS service 1 subscription is expiring in %d days. Please renew your subscription, if you wish to receive messages also in the future.',
        'You have unsubscribed from the excellent SMS service 1. Have a nice day and welcome back soon!',
        7,
        (SELECT id FROM delivery_pipes WHERE name='Test pipe'),
        'T1600'
);

INSERT INTO contents (content_type, short_uuid) VALUES ('CUSTOM', '9d37c676-2991-4267-9fcc-1bb133489c8c');
INSERT INTO custom_contents (id, title, content) VALUES (
        (SELECT id FROM contents WHERE short_uuid='9d37c676-2991-4267-9fcc-1bb133489c8c'),
        'testi-title',
        'testi-content'
);

INSERT INTO deliverables (content_id, delivery_pipe_id, deliverable_type, status) VALUES (
        (SELECT id FROM contents WHERE short_uuid='9d37c676-2991-4267-9fcc-1bb133489c8c'),
        (SELECT id FROM delivery_pipes WHERE name='Test pipe'),
        'SERIES',
        'PENDING_APPROVAL'
);
INSERT INTO series_deliverables (id, delivery_days_after_subscription) VALUES (
        (SELECT id FROM deliverables WHERE
                content_id=(SELECT id FROM contents WHERE short_uuid='9d37c676-2991-4267-9fcc-1bb133489c8c') AND
                delivery_pipe_id=(SELECT id FROM delivery_pipes WHERE name='Test pipe') AND
                deliverable_type='SERIES'
        ),
        1
);

INSERT INTO delivery_pipes (name, deliverable_type, theme) VALUES ('Test pipe 2','SCHEDULED', 'fashion');
INSERT INTO delivery_pipes_providers (delivery_pipes_id, providers_id) VALUES (
        (SELECT id FROM delivery_pipes WHERE name='Test pipe 2'),
        (SELECT id FROM users WHERE username='a_provider'));

INSERT INTO delivery_pipes_providers (delivery_pipes_id, providers_id) VALUES (
        (SELECT id FROM delivery_pipes WHERE name='Test pipe 2'),
        (SELECT id FROM users WHERE username='z_provider'));

INSERT INTO services (
        keyword,
        short_code,
        operator,
        unsubscribe_keyword,
        welcome_message,
        renew_message,
        expire_message,
        unsubscribe_message,
        subscription_period,
        delivery_pipe_id,
        delivery_time)
VALUES (
        'READ2',
        33070,
        'XYZ',
        'UNSUBSCRIBE_READ2',
        'Welcome to an excellent SMS service 2. Your subscription is now valid for the next %d days.',
        'You have renewed subscription for the excellent SMS service 2. Your subscription is now valid %d more days.',
        'Your SMS service 2 subscription is expiring in %d days. Please renew your subscription, if you wish to receive messages also in the future.',
        'You have unsubscribed from the excellent SMS service 2. Have a nice day and welcome back soon!',
        14,
        (SELECT id FROM delivery_pipes WHERE name='Test pipe 2'),
        'T1000'
);

INSERT INTO contents (content_type, short_uuid) VALUES ('CUSTOM', '82bd331d-c1bb-4788-801a-ff8f99631a9f');
INSERT INTO custom_contents (id, title, content) VALUES (
        (SELECT id FROM contents WHERE short_uuid='82bd331d-c1bb-4788-801a-ff8f99631a9f'),
        'testi-title2',
        'testi-content2'
);

INSERT INTO deliverables (content_id, delivery_pipe_id, deliverable_type, status) VALUES (
        (SELECT id FROM contents WHERE short_uuid='82bd331d-c1bb-4788-801a-ff8f99631a9f'),
        (SELECT id FROM delivery_pipes WHERE name='Test pipe 2'),
        'SCHEDULED',
        'PENDING_APPROVAL'
);
INSERT INTO scheduled_deliverables (id, delivery_date) VALUES (
        (SELECT id FROM deliverables WHERE
                content_id=(SELECT id FROM contents WHERE short_uuid='82bd331d-c1bb-4788-801a-ff8f99631a9f') AND
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
