CREATE TABLE `board` (
  `board_id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `contents` text NOT NULL,
  `title` varchar(255) NOT NULL,
  `created_at` timestamp NOT NULL,
  `update_at` timestamp NOT NULL,
  `hits` bigint DEFAULT '0',
  PRIMARY KEY (`board_id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `board_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;