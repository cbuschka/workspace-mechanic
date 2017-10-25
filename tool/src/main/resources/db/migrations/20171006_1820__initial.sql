CREATE TABLE migration_event (
  id             BIGINT PRIMARY KEY AUTO_INCREMENT,
  event_time     BIGINT       NOT NULL,
  migration_name VARCHAR(200) NOT NULL,
  event_type     VARCHAR(40)  NOT NULL
    CHECK (event_type IN ('MIGRATION_STARTED', 'MIGRATION_FAILED', 'MIGRATION_SUCCEEDED'))
);
