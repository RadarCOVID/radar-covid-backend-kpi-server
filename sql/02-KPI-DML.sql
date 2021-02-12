INSERT INTO KPI.KPI_TYPES (DE_KPI_NAME, IN_ENABLED)
VALUES
('DAYS_SINCE_LAST_EXPOSURE', FALSE),
('MATCHED_KEY_COUNT', FALSE),
('MAXIMUM_RISK_SCORE', FALSE),
('ATTENUATION_DURATIONS_1', FALSE),
('ATTENUATION_DURATIONS_2', FALSE),
('ATTENUATION_DURATIONS_3', FALSE),
('SUMMATION_RISK_SCORE', FALSE),
('MATCH_CONFIRMED', TRUE),
('BLUETOOTH_ACTIVATED', TRUE)
;


INSERT INTO KPI.CCAA (DE_CCAA_ID, DE_CCAA_NUM_ID, DE_CCAA_NAME, FC_START_DATE)
VALUES
('ES-AN', '01', 'Andalucía', '2020-08-19'),
('ES-AR', '02', 'Aragón', '2020-08-19'),
('ES-AS', '03', 'Principado de Asturias', '2020-09-04'),
('ES-IB', '04', 'Islas Baleares', '2020-08-24'),
('ES-CN', '05', 'Canarias', '2020-08-20'),
('ES-CB', '06', 'Cantabria', '2020-08-19'),
('ES-CL', '07', 'Castilla y León', '2020-08-20'),
('ES-CM', '08', 'Castilla-La Mancha', '2020-09-18'),
('ES-CT', '09', 'Cataluña', '2020-10-27'),
('ES-VC', '10', 'Comunidad Valenciana', '2020-09-08'),
('ES-EX', '11', 'Extremadura', '2020-08-19'),
('ES-GA', '12', 'Galicia', '2020-09-14'),
('ES-MD', '13', 'Comunidad de Madrid', '2020-09-01'),
('ES-MC', '14', 'Región de Murcia', '2020-08-25'),
('ES-NC', '15', 'Comunidad Foral Navarra', '2020-09-01'),
('ES-PV', '16', 'País Vasco', '2020-09-21'),
('ES-RI', '17', 'La Rioja', '2020-09-03'),
('ES-CE', '18', 'Ceuta', '2020-09-24'),
('ES-ML', '19', 'Melilla', '2020-09-14')
;

