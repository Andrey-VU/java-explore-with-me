
CREATE TABLE IF NOT EXISTS STAT_ENTITY (
  ID BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
  APP VARCHAR(100) NOT NULL,
  URI VARCHAR(500) NOT NULL,
  IP VARCHAR(50) NOT NULL,
  DATE_TIME TIMESTAMP,
  CONSTRAINT PK_STAT_ENTITY PRIMARY KEY (ID)
);