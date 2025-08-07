DROP TABLE IF EXISTS complaints;

CREATE TABLE complaints (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_id BIGINT NOT NULL,
    complaint_text VARCHAR(1000) NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'OPEN',
    handling_time_seconds DOUBLE,
    created_at TIMESTAMP NOT NULL,
    resolved_at TIMESTAMP NULL
);
