CREATE TABLE `User` (
	`id` bigint NOT NULL AUTO_INCREMENT,
	`name` varchar(255) CHARACTER SET utf8mb4 DEFAULT NULL,
	`email` varchar(255) CHARACTER SET utf8mb4 DEFAULT NULL,
	`password` varchar(255) CHARACTER SET utf8mb4 DEFAULT NULL,
	`passwordSalt` varchar(100) CHARACTER SET utf8mb4 DEFAULT NULL,
	`confirmationToken` varchar(100) CHARACTER SET utf8mb4 DEFAULT NULL,
	`confirmationTokenTime` timestamp NULL DEFAULT NULL,
	`recoveryToken` varchar(100) CHARACTER SET utf8mb4 DEFAULT NULL,
	`recoveryTokenTime` timestamp NULL DEFAULT NULL,
	`disabled` bit(1) NOT NULL,
	`disabledNote` varchar(255) CHARACTER SET utf8mb4 DEFAULT NULL,
	`creationDate` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
	`lastModifiedDate` timestamp NULL DEFAULT NULL,
	PRIMARY KEY (`id`),
	UNIQUE KEY `email` (`email`)
);
