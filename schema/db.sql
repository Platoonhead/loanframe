# --- !Ups
CREATE TABLE covid19_users
(
  id         BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY,
  country    VARCHAR(254) NOT NULL,
  name       VARCHAR(254) NOT NULL,
  age        INT          NOT NULL,
  isAffected BOOLEAN      NOT NULL
);

# --- !Downs