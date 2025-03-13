ALTER TABLE media_file ADD required_role bigint NOT NULL;
UPDATE media_file SET required_role = 3;
ALTER TABLE media_file
ADD CONSTRAINT fk_required_role
FOREIGN KEY (required_role)
REFERENCES sec_role(id);

insert into share_media.user values(1, 'anonymous', 'anonymous@email.com', '$2a$10$FS0iWKmIh3d1QrCCsLypb.Gs/3tH54EPpuqzQdQFBQ3kieww4xYiy');

ALTER TABLE media_file ADD user_id bigint NOT NULL;
UPDATE media_file SET user_id = (SELECT id FROM `user` u LIMIT 1);
ALTER TABLE media_file
ADD CONSTRAINT fk_user_id
FOREIGN KEY (user_id)
REFERENCES user(id);


