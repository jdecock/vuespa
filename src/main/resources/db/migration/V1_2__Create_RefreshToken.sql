CREATE TABLE `refresh_token` (
	`id` bigint NOT NULL AUTO_INCREMENT,
	`user_id` bigint NOT NULL,
	`token` varchar(255) CHARACTER SET utf8mb4 NOT NULL,
	`description` varchar(255) CHARACTER SET utf8mb4 DEFAULT NULL,
	`token_expiration` timestamp NULL DEFAULT NULL,
	`creation_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
	PRIMARY KEY (`id`),
	INDEX `FK_REFRESHTOKEN_USER_IDX` (`user_id` ASC) VISIBLE,
	CONSTRAINT `FK_REFRESHTOKEN_USER` FOREIGN KEY (`user_id`) REFERENCES `User` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
);
