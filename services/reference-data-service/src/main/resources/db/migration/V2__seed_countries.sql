INSERT INTO countries (id, code, name, dial_code, active)
VALUES (gen_random_uuid(), 'VN', 'Viet Nam', '+84', TRUE),
       (gen_random_uuid(), 'SG', 'Singapore', '+65', TRUE),
       (gen_random_uuid(), 'JP', 'Japan', '+81', TRUE),
       (gen_random_uuid(), 'US', 'United States', '+1', TRUE),
       (gen_random_uuid(), 'DE', 'Germany', '+49', TRUE),
       (gen_random_uuid(), 'AQ', 'Antarctica', '+672', FALSE);
