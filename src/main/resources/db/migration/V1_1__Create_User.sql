CREATE TABLE `User` (
	`id` bigint NOT NULL AUTO_INCREMENT,
	`name` varchar(255) CHARACTER SET utf8mb4 DEFAULT NULL,
	`email` varchar(255) CHARACTER SET utf8mb4 DEFAULT NULL,
	`roles` varchar(255) CHARACTER SET utf8mb4 DEFAULT NULL,
	`password` varchar(255) CHARACTER SET utf8mb4 DEFAULT NULL,
	`password_salt` varchar(100) CHARACTER SET utf8mb4 DEFAULT NULL,
	`confirmation_token` varchar(100) CHARACTER SET utf8mb4 DEFAULT NULL,
	`confirmation_token_time` timestamp NULL DEFAULT NULL,
	`recovery_token` varchar(100) CHARACTER SET utf8mb4 DEFAULT NULL,
	`recovery_token_time` timestamp NULL DEFAULT NULL,
	`disabled` bit(1) NOT NULL,
	`disabled_note` varchar(255) CHARACTER SET utf8mb4 DEFAULT NULL,
	`creation_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
	`last_modified_date` timestamp NULL DEFAULT NULL,
	PRIMARY KEY (`id`),
	UNIQUE KEY `email` (`email`)
);
