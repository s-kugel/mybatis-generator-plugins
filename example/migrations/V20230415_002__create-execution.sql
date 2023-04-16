CREATE TABLE IF NOT EXISTS execution (
    execution_id VARCHAR(26) NOT NULL COMMENT '約定ID',
    execution_datetime TIMESTAMP COMMENT '約定日時',
    amount DECIMAL(17, 4) COMMENT '約定金額',
    quantity DECIMAL(17, 4) COMMENT '約定数量',
    created_at DATETIME COMMENT '作成日時',
    created_by VARCHAR(26) COMMENT '作成者',
    updated_at DATETIME COMMENT '更新日時',
    updated_by VARCHAR(26) COMMENT '更新者',
    version INT DEFAULT 0 COMMENT 'バージョン',
    PRIMARY KEY (execution_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='約定';
