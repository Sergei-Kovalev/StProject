DELETE FROM jc_register_office;
DELETE FROM jc_passport_office;
DELETE FROM jc_country_struct;
DELETE FROM jc_street;

INSERT INTO jc_street (street_code, street_name) VALUES
(1, 'Sadovaya st.'),
(2, 'Nevsky prospect'),
(3, 'Stakhanovcev st.'),
(4, 'Gorohovoya st.'),
(5, 'Veteranov prospect');

INSERT INTO jc_university (university_id, university_name) VALUES
(1, 'Belorussian state economic university'),
(2, 'Belorussian medic university'),
(3, 'Agriculture institute');

INSERT INTO jc_country_struct (area_id, area_name) VALUES
('010000000000', 'City'),
('010010000000', 'City District 1'),
('010020000000', 'City District 2'),
('010030000000', 'City District 3'),
('010040000000', 'City District 4'),

('020000000000', 'Country'),
('020010000000', 'Country Region 1'),
('020010010000', 'Country Region 1 District 1'),
('020010010001', 'Country Region 1 District 1 Settlement 1'),
('020010010002', 'Country Region 1 District 1 Settlement 2'),
('020010020000', 'Country Region 1 District 2'),
('020010020001', 'Country Region 1 District 2 Settlement 1'),
('020010020002', 'Country Region 1 District 2 Settlement 2'),
('020010020003', 'Country Region 1 District 2 Settlement 3'),
('020020000000', 'Country Region 2'),
('020020010000', 'Country Region 2 District 1'),
('020020010001', 'Country Region 2 District 1 Settlement 1'),
('020020010002', 'Country Region 2 District 1 Settlement 2'),
('020020010003', 'Country Region 2 District 1 Settlement 3'),
('020020020000', 'Country Region 2 District 2'),
('020020020001', 'Country Region 2 District 2 Settlement 1'),
('020020020002', 'Country Region 2 District 2 Settlement 2');

INSERT INTO jc_passport_office (p_office_id, p_office_area_id, p_office_name) VALUES
(1, '010010000000', 'Passport department District 1 City'),
(2, '010020000000', 'Passport department #1 District 2 City'),
(3, '010020000000', 'Passport department #2 District 2 City'),
(4, '010010000000', 'Passport department District 3 City'),
(5, '020010010001', 'Passport department Region 1 Settlement 1'),
(6, '020010010002', 'Passport department Region 1 Settlement 2'),
(7, '020020010000', 'Passport department Region 2 District 1'),
(8, '020020020000', 'Passport department Region 2 District 2');

INSERT INTO jc_register_office (r_office_id, r_office_area_id, r_office_name) VALUES
(1, '010010000000', 'ZAGS #1 District 1 City'),
(2, '010010000000', 'ZAGS #2 District 1 City'),
(3, '010020000000', 'ZAGS District 2 City'),
(4, '020010010001', 'ZAGS Region 1 Settlement 1'),
(5, '020010010002', 'ZAGS Region 1 Settlement 2'),
(6, '020020010000', 'ZAGS Region 2 District 1'),
(7, '020020020000', 'ZAGS Region 2 District 2');