# --- !Ups

ALTER TABLE images ADD COLUMN file_uid VARCHAR(254);

# -- !Downs

ALTER TABLE images DROP COLUMN file_uid
