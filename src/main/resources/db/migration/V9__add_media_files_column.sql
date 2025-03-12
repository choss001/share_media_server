ALTER TABLE media_file ADD required_role bigint NOT NULL;
<!--UPDATE media_file SET required_role = 3;-->
ALTER TABLE media_file
ADD CONSTRAINT fk_required_role
FOREIGN KEY (required_role)
REFERENCES sec_role(id);

ALTER TABLE media_file ADD user_id bigint NOT NULL;
<!--UPDATE media_file SET user_id = (SELECT id FROM `user` u LIMIT 1);-->
ALTER TABLE media_file
ADD CONSTRAINT fk_user_id
FOREIGN KEY (user_id)
REFERENCES user(id);


