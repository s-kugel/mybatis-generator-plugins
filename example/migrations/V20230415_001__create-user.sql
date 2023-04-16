CREATE TABLE IF NOT EXISTS user (
    user_id VARCHAR(26) NOT NULL COMMENT 'ユーザーID',
    user_name VARCHAR(20) COMMENT 'ユーザー名',
    email VARCHAR(256) COMMENT 'メールアドレス',
    created_at DATETIME COMMENT '作成日時',
    created_by VARCHAR(26) COMMENT '作成者',
    updated_at DATETIME COMMENT '更新日時',
    updated_by VARCHAR(26) COMMENT '更新者',
    version INT DEFAULT 0 COMMENT 'バージョン',
    PRIMARY KEY (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='ユーザー';
