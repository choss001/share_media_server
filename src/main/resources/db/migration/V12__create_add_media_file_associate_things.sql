ALTER TABLE media_file
ADD public_yn char(1);

ALTER TABLE media_file
ADD title varchar(255) not null;

ALTER TABLE media_file
ADD content TEXT not null;

update media_file set public_yn ='Y';

CREATE TABLE `media_reply` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `media_file_id` BIGINT NOT NULL,
  `user_id` BIGINT NOT NULL,
  `content` TEXT NOT NULL,
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  FOREIGN KEY (`media_file_id`) REFERENCES `media_file` (`id`) ON DELETE CASCADE,
  FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE
);

CREATE TABLE `media_favorite` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `media_file_id` BIGINT NOT NULL,
  `user_id` BIGINT NOT NULL,
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  UNIQUE (`media_file_id`, `user_id`),  -- Prevent duplicate likes from the same user
  PRIMARY KEY (`id`),
  FOREIGN KEY (`media_file_id`) REFERENCES `media_file` (`id`) ON DELETE CASCADE,
  FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE
);