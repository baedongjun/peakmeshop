-- 기존 테이블 백업
CREATE TABLE product_options_backup AS SELECT * FROM product_options;
CREATE TABLE product_option_values_backup AS SELECT * FROM product_option_values;
CREATE TABLE product_attributes_backup AS SELECT * FROM product_attributes;

-- product_options 테이블 업데이트
ALTER TABLE product_options
    ADD COLUMN type VARCHAR(50) NOT NULL DEFAULT 'text',
    ADD COLUMN required BOOLEAN NOT NULL DEFAULT false,
    ADD COLUMN multiple BOOLEAN NOT NULL DEFAULT false,
    ADD COLUMN min_selection INTEGER,
    ADD COLUMN max_selection INTEGER,
    ADD COLUMN default_value VARCHAR(255),
    ADD COLUMN show_in_product_listing BOOLEAN NOT NULL DEFAULT false,
    ADD COLUMN show_in_cart BOOLEAN NOT NULL DEFAULT false,
    ADD COLUMN show_in_order BOOLEAN NOT NULL DEFAULT false;

-- product_option_values 테이블 업데이트
ALTER TABLE product_option_values
    ADD COLUMN display_value VARCHAR(255),
    ADD COLUMN color_code VARCHAR(20),
    ADD COLUMN image_url VARCHAR(255),
    DROP COLUMN name;

-- product_attribute_options 테이블 생성
CREATE TABLE product_attribute_options (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    display_name VARCHAR(100),
    description TEXT,
    type VARCHAR(50) NOT NULL,
    required BOOLEAN NOT NULL DEFAULT false,
    filterable BOOLEAN NOT NULL DEFAULT false,
    searchable BOOLEAN NOT NULL DEFAULT false,
    comparable BOOLEAN NOT NULL DEFAULT false,
    show_in_product_listing BOOLEAN NOT NULL DEFAULT false,
    sort_order INTEGER,
    enabled BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- product_attributes 테이블 업데이트
ALTER TABLE product_attributes
    DROP COLUMN name,
    DROP COLUMN code,
    DROP COLUMN type,
    DROP COLUMN required,
    DROP COLUMN filterable,
    DROP COLUMN searchable,
    DROP COLUMN comparable,
    DROP COLUMN show_in_product_listing,
    ADD COLUMN attribute_option_id BIGINT,
    ADD COLUMN value VARCHAR(255) NOT NULL,
    ADD COLUMN sort_order INTEGER,
    ADD FOREIGN KEY (attribute_option_id) REFERENCES product_attribute_options(id);

-- 기존 데이터 마이그레이션
-- product_options 데이터 마이그레이션
UPDATE product_options po
SET type = CASE 
    WHEN LOWER(name) LIKE '%color%' OR LOWER(name) LIKE '%색상%' THEN 'color'
    WHEN LOWER(name) LIKE '%size%' OR LOWER(name) LIKE '%사이즈%' THEN 'size'
    ELSE 'text'
END,
required = true,
multiple = false,
show_in_product_listing = true,
show_in_cart = true,
show_in_order = true;

-- product_option_values 데이터 마이그레이션
UPDATE product_option_values pov
SET display_value = pov.value,
    color_code = CASE 
        WHEN EXISTS (
            SELECT 1 FROM product_options po 
            WHERE po.id = pov.option_id 
            AND (LOWER(po.name) LIKE '%color%' OR LOWER(po.name) LIKE '%색상%')
        ) THEN pov.value
        ELSE NULL
    END;

-- product_attributes 데이터 마이그레이션
INSERT INTO product_attribute_options (name, display_name, type, required, filterable, searchable, comparable, show_in_product_listing)
SELECT DISTINCT 
    name,
    name,
    'text',
    true,
    true,
    true,
    true,
    true
FROM product_attributes_backup;

UPDATE product_attributes pa
SET attribute_option_id = (
    SELECT id FROM product_attribute_options pao 
    WHERE pao.name = (
        SELECT name FROM product_attributes_backup pab 
        WHERE pab.id = pa.id
    )
),
value = (
    SELECT GROUP_CONCAT(option_value) 
    FROM product_attributes_backup pab 
    WHERE pab.id = pa.id
);

-- 인덱스 생성
CREATE INDEX idx_product_options_type ON product_options(type);
CREATE INDEX idx_product_option_values_display_value ON product_option_values(display_value);
CREATE INDEX idx_product_attribute_options_type ON product_attribute_options(type);
CREATE INDEX idx_product_attributes_value ON product_attributes(value);

-- 백업 테이블 삭제 (필요한 경우 주석 처리)
-- DROP TABLE product_options_backup;
-- DROP TABLE product_option_values_backup;
-- DROP TABLE product_attributes_backup; 