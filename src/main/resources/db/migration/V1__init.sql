CREATE TABLE IF NOT EXISTS account (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS facility_type (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    tier INT NOT NULL,
    max_slots INT DEFAULT 1,
    base_output_interval INT DEFAULT 60,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS resource_def (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    type VARCHAR(50) NOT NULL,
    rarity INT DEFAULT 0,
    icon VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS rarity_def (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    color VARCHAR(20) NOT NULL,
    trait_count_min INT NOT NULL,
    trait_count_max INT NOT NULL,
    bonus_min DECIMAL(5,2) NOT NULL,
    bonus_max DECIMAL(5,2) NOT NULL
);

CREATE TABLE IF NOT EXISTS "character" (
    id BIGSERIAL PRIMARY KEY,
    account_id BIGINT NOT NULL REFERENCES account(id),
    name VARCHAR(100) NOT NULL,
    rarity_id BIGINT NOT NULL REFERENCES rarity_def(id),
    level INT DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS facility (
    id BIGSERIAL PRIMARY KEY,
    account_id BIGINT NOT NULL REFERENCES account(id),
    type_id BIGINT NOT NULL REFERENCES facility_type(id),
    name VARCHAR(100) NOT NULL,
    level INT DEFAULT 1,
    last_produce_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS trait_def (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    icon VARCHAR(255),
    facility_type_id BIGINT REFERENCES facility_type(id),
    bonus_percentage DECIMAL(5,2) NOT NULL
);

CREATE TABLE IF NOT EXISTS char_trait (
    id BIGSERIAL PRIMARY KEY,
    character_id BIGINT NOT NULL REFERENCES "character"(id),
    trait_id BIGINT NOT NULL REFERENCES trait_def(id),
    bonus_value DECIMAL(5,2) NOT NULL
);

CREATE TABLE IF NOT EXISTS facility_slot (
    id BIGSERIAL PRIMARY KEY,
    facility_id BIGINT NOT NULL REFERENCES facility(id),
    slot_index INT NOT NULL,
    character_id BIGINT REFERENCES "character"(id),
    assigned_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS conversion (
    id BIGSERIAL PRIMARY KEY,
    facility_type_id BIGINT NOT NULL REFERENCES facility_type(id),
    input_json JSONB NOT NULL,
    output_resource_id BIGINT NOT NULL REFERENCES resource_def(id),
    output_qty INT NOT NULL,
    base_time INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS warehouse (
    id BIGSERIAL PRIMARY KEY,
    account_id BIGINT NOT NULL REFERENCES account(id),
    resource_id BIGINT NOT NULL REFERENCES resource_def(id),
    quantity BIGINT NOT NULL DEFAULT 0,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (account_id, resource_id)
);

CREATE TABLE IF NOT EXISTS production_log (
    id BIGSERIAL PRIMARY KEY,
    facility_id BIGINT NOT NULL REFERENCES facility(id),
    output_resource_id BIGINT NOT NULL REFERENCES resource_def(id),
    output_qty INT NOT NULL,
    produced_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_account_username ON account(username);
CREATE INDEX IF NOT EXISTS idx_character_account ON "character"(account_id);
CREATE INDEX IF NOT EXISTS idx_char_trait_character ON char_trait(character_id);
CREATE INDEX IF NOT EXISTS idx_facility_account ON facility(account_id);
CREATE INDEX IF NOT EXISTS idx_facility_slot_facility ON facility_slot(facility_id);
CREATE UNIQUE INDEX IF NOT EXISTS idx_facility_slot_unique ON facility_slot(facility_id, slot_index);
CREATE INDEX IF NOT EXISTS idx_conversion_facility ON conversion(facility_type_id);
CREATE INDEX IF NOT EXISTS idx_warehouse_account ON warehouse(account_id);
CREATE INDEX IF NOT EXISTS idx_production_log_facility ON production_log(facility_id);

INSERT INTO facility_type (id, name, description, tier, max_slots, base_output_interval) VALUES
    (1, '矿场', '开采矿石的基础设施', 1, 1, 60),
    (2, '伐木场', '采集木材的基础设施', 1, 1, 60),
    (3, '冶炼厂', '将矿石加工为金属锭', 2, 2, 120),
    (4, '木工坊', '将木材加工为木板', 2, 2, 120),
    (5, '铸造厂', '将金属锭铸造成武器', 3, 3, 180),
    (6, '炼金工坊', '生产药剂与高级制品', 3, 3, 180)
ON CONFLICT (id) DO NOTHING;

INSERT INTO resource_def (id, name, type, rarity, icon) VALUES
    (1, '矿石', 'raw', 0, 'ore'),
    (2, '木材', 'raw', 0, 'wood'),
    (3, '金属锭', 'intermediate', 1, 'ingot'),
    (4, '木板', 'intermediate', 1, 'plank'),
    (5, '铁剑', 'final', 2, 'sword'),
    (6, '生命药水', 'final', 2, 'potion')
ON CONFLICT (id) DO NOTHING;

INSERT INTO rarity_def (id, name, color, trait_count_min, trait_count_max, bonus_min, bonus_max) VALUES
    (1, 'N', '#AAAAAA', 1, 1, 5.00, 10.00),
    (2, 'R', '#00FF00', 1, 2, 10.00, 20.00),
    (3, 'SR', '#0000FF', 2, 2, 15.00, 30.00),
    (4, 'SSR', '#FF00FF', 2, 3, 25.00, 50.00)
ON CONFLICT (id) DO NOTHING;

INSERT INTO trait_def (id, name, icon, facility_type_id, bonus_percentage) VALUES
    (1, '矿工', 'miner', 1, 30.00),
    (2, '伐木工', 'lumberjack', 2, 30.00),
    (3, '铁匠', 'blacksmith', 3, 30.00),
    (4, '木匠', 'carpenter', 4, 30.00),
    (5, '铸造师', 'foundry', 5, 30.00),
    (6, '炼金师', 'alchemist', 6, 30.00),
    (7, '多才多艺', 'versatile', NULL, 15.00)
ON CONFLICT (id) DO NOTHING;

INSERT INTO conversion (id, facility_type_id, input_json, output_resource_id, output_qty, base_time) VALUES
    (1, 1, '[{"resource_id":null,"qty":0}]'::jsonb, 1, 10, 60),
    (2, 2, '[{"resource_id":null,"qty":0}]'::jsonb, 2, 8, 60),
    (3, 3, '[{"resource_id":1,"qty":2}]'::jsonb, 3, 1, 120),
    (4, 4, '[{"resource_id":2,"qty":3}]'::jsonb, 4, 1, 120),
    (5, 5, '[{"resource_id":3,"qty":2}]'::jsonb, 5, 1, 180),
    (6, 6, '[{"resource_id":1,"qty":1},{"resource_id":3,"qty":1}]'::jsonb, 6, 1, 180)
ON CONFLICT (id) DO NOTHING;

INSERT INTO account (id, username, password) VALUES
    (1, 'test', '$2b$10$8nD9fCTqiEHVwat2.HP2kOvhOMM9qS5DdtUQy13fJYMVaJaefUcA.')
ON CONFLICT (id) DO NOTHING;

SELECT setval(pg_get_serial_sequence('account', 'id'), GREATEST(COALESCE((SELECT MAX(id) FROM account), 1), 1), true);
SELECT setval(pg_get_serial_sequence('facility_type', 'id'), GREATEST(COALESCE((SELECT MAX(id) FROM facility_type), 1), 1), true);
SELECT setval(pg_get_serial_sequence('resource_def', 'id'), GREATEST(COALESCE((SELECT MAX(id) FROM resource_def), 1), 1), true);
SELECT setval(pg_get_serial_sequence('rarity_def', 'id'), GREATEST(COALESCE((SELECT MAX(id) FROM rarity_def), 1), 1), true);
SELECT setval(pg_get_serial_sequence('trait_def', 'id'), GREATEST(COALESCE((SELECT MAX(id) FROM trait_def), 1), 1), true);
SELECT setval(pg_get_serial_sequence('conversion', 'id'), GREATEST(COALESCE((SELECT MAX(id) FROM conversion), 1), 1), true);
