CREATE TABLE `users` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `email` varchar(50) NOT NULL,
  `createdDate` int(11) NOT NULL,
  `lastLogin` datetime DEFAULT NULL,
  `firstName` varchar(45) DEFAULT NULL,
  `lastName` varchar(45) DEFAULT NULL,
  `globalHandle` varchar(45) DEFAULT NULL,
  `role` varchar(11) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `useridentity` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `users_id` int(11) NOT NULL,
  `userIdentifier` varchar(50) NOT NULL,
  `providerId` varchar(45) NOT NULL,
  `email` varchar(45) DEFAULT NULL,
  `password` varchar(100) DEFAULT NULL,
  `firstName` varchar(45) DEFAULT NULL,
  `lastName` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`),
  KEY `user_identity_id` (`users_id`),
  CONSTRAINT `user_identity_id` FOREIGN KEY (`users_id`) REFERENCES `users` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

CREATE TABLE `userplatformprofile` (
  `users_id` int(11) NOT NULL,
  `platform` varchar(45) NOT NULL,
  `identifier` varchar(45) NOT NULL,
  PRIMARY KEY (`users_id`),
  KEY `user_platform_id_idx` (`users_id`),
  CONSTRAINT `user_platform_id` FOREIGN KEY (`users_id`) REFERENCES `users` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `events` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `eventType` varchar(15) NOT NULL,
  `name` varchar(45) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `eventdetails` (
  `events_id` int(11) NOT NULL AUTO_INCREMENT,
  `address` varchar(45) DEFAULT NULL,
  `city` varchar(45) DEFAULT NULL,
  `state` varchar(45) DEFAULT NULL,
  `timeStart` int(11) DEFAULT NULL,
  `timeEnd` int(11) DEFAULT NULL,
  `description` longtext,
  `rules` longtext,
  `prizes` longtext,
  `streams` longtext,
  `servers` longtext,
  `scheduledevents` longtext,
  PRIMARY KEY (`events_id`),
  CONSTRAINT `events_details` FOREIGN KEY (`events_id`) REFERENCES `events` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `eventpayments` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `events_id` int(11) NOT NULL,
  `paytype` varchar(45) NOT NULL,
  `secret` varchar(45) DEFAULT NULL,
  `public` varchar(45) DEFAULT NULL,
  `address` varchar(45) DEFAULT NULL,
  `amount` double NOT NULL,
  `isenabled` smallint(6) NOT NULL DEFAULT '1',
  PRIMARY KEY (`id`),
  KEY `event_payment_id` (`events_id`),
  CONSTRAINT `event_payment_id` FOREIGN KEY (`events_id`) REFERENCES `events` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `games` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(45) NOT NULL,
  `publisher` varchar(45) DEFAULT NULL,
  `website` varchar(100) DEFAULT NULL,
  `gameType` varchar(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `tournament` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `bracketType` varchar(15) NOT NULL,
  `registrationType` varchar(15) NOT NULL,
  `tournamentType` varchar(15) NOT NULL,
  `games_id` int(11) NOT NULL,
  `events_id` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`),
  KEY `tournament_event_id` (`games_id`),
  KEY `tournament_game_id` (`events_id`),
  CONSTRAINT `tournament_event_id` FOREIGN KEY (`games_id`) REFERENCES `games` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `tournament_game_id` FOREIGN KEY (`events_id`) REFERENCES `events` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `tournamentdetails` (
  `tournamentId` int(11) NOT NULL,
  `name` varchar(45) DEFAULT NULL,
  `gamePlayed` varchar(45) DEFAULT NULL,
  `description` longtext,
  `rules` longtext,
  `prizes` longtext,
  `streams` longtext,
  `timeStart` int(11) DEFAULT NULL,
  `timeEnd` int(11) DEFAULT NULL,
  `tournamentdetailscol` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`tournamentId`),
  KEY `tournament_details_id_idx` (`tournamentId`),
  CONSTRAINT `tournament_details_id` FOREIGN KEY (`tournamentId`) REFERENCES `tournament` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `teams` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(45) NOT NULL,
  `games_id` int(11) NOT NULL,
  `maxPlayers` int(11) DEFAULT NULL,
  `joinType` varchar(45) NOT NULL,
  `tournament_id` int(11) NOT NULL,
  `createdDate` int(11) DEFAULT NULL,
  `isPresent` smallint(2) NOT NULL DEFAULT '0',
  `guildOnly` smallint(2) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `team_tournament_id_idx` (`tournament_id`),
  KEY `team_game_id_idx` (`games_id`),
  CONSTRAINT `team_tournament_id` FOREIGN KEY (`tournament_id`) REFERENCES `tournament` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `team_game_id` FOREIGN KEY (`games_id`) REFERENCES `games` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `guilds` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(45) NOT NULL,
  `description` text,
  `maxPlayers` int(11) DEFAULT NULL,
  `joinType` varchar(45) NOT NULL,
  `createdDate` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `guilds_users` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `guilds_id` int(11) NOT NULL,
  `users_id` int(11) NOT NULL,
  `isCaptain` bit(1) NOT NULL DEFAULT b'0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`),
  KEY `guild_id_idx` (`guilds_id`),
  KEY `user_id_idx` (`users_id`),
  CONSTRAINT `guild_users_id` FOREIGN KEY (`guilds_id`) REFERENCES `guilds` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION,
  CONSTRAINT `user_guilds_id` FOREIGN KEY (`users_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8;

CREATE TABLE `guilds_games` (
  `id` int(11) NOT NULL,
  `guilds_id` int(11) NOT NULL,
  `games_id` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `guild_guild_id_idx` (`guilds_id`),
  KEY `guild_game_id_idx` (`games_id`),
  CONSTRAINT `guild_guild_id` FOREIGN KEY (`guilds_id`) REFERENCES `guilds` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `guild_game_id` FOREIGN KEY (`games_id`) REFERENCES `games` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `games_events` (
  `games_id` int(11) NOT NULL,
  `events_id` int(11) NOT NULL,
  KEY `game_event_id_idx` (`games_id`),
  KEY `event_game_id_idx` (`events_id`),
  CONSTRAINT `event_game_id` FOREIGN KEY (`events_id`) REFERENCES `events` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `game_event_id` FOREIGN KEY (`games_id`) REFERENCES `games` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `teams_users` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `teams_id` int(11) NOT NULL,
  `users_id` int(11) NOT NULL,
  `isCaptain` bit(1) NOT NULL DEFAULT b'0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`),
  KEY `team_id_idx` (`teams_id`),
  KEY `user_id_idx` (`users_id`),
  CONSTRAINT `team_id` FOREIGN KEY (`teams_id`) REFERENCES `teams` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION,
  CONSTRAINT `user_id` FOREIGN KEY (`users_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `user_events` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `users_id` int(11) NOT NULL,
  `events_id` int(11) NOT NULL,
  `isPresent` tinyint(1) NOT NULL DEFAULT '0',
  `isAdmin` tinyint(1) NOT NULL DEFAULT '0',
  `isModerator` tinyint(1) NOT NULL DEFAULT '0',
  `hasPaid` tinyint(1) NOT NULL DEFAULT '0',
  `receiptId` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`),
  KEY `user_event_id_idx` (`users_id`),
  KEY `event_user_id_idx` (`events_id`),
  CONSTRAINT `event_user_id` FOREIGN KEY (`events_id`) REFERENCES `events` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `user_event_id` FOREIGN KEY (`users_id`) REFERENCES `users` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `user_tournaments` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `users_id` int(11) NOT NULL,
  `tournament_id` int(11) NOT NULL,
  `isPresent` tinyint(1) NOT NULL DEFAULT '0',
  `isAdmin` tinyint(1) NOT NULL DEFAULT '0',
  `isModerator` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`),
  KEY `user_tournament_id_idx` (`users_id`),
  KEY `tournament_user_id_idx` (`tournament_id`),
  CONSTRAINT `tournament_user_id` FOREIGN KEY (`tournament_id`) REFERENCES `tournament` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `user_tournament_id` FOREIGN KEY (`users_id`) REFERENCES `users` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `tokens` (
  `id` int(11) NOT NULL,
  `token` varchar(100) NOT NULL,
  `issuedOn` int(11) NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `token_UNIQUE` (`token`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `apikeys` (
  `id` int(11) NOT NULL,
  `apiToken` varchar(100) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `apiToken_UNIQUE` (`apiToken`),
  UNIQUE KEY `id_UNIQUE` (`id`),
  CONSTRAINT `api_key_id` FOREIGN KEY (`id`) REFERENCES `users` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `nonactiveusers` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `email` varchar(50) NOT NULL,
  `createdDate` datetime NOT NULL,
  `lastLogin` datetime DEFAULT NULL,
  `firstName` varchar(45) DEFAULT NULL,
  `lastName` varchar(45) DEFAULT NULL,
  `globalHandle` varchar(45) DEFAULT NULL,
  `role` varchar(11) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `nonactiveuseridentity` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `userId` int(11) NOT NULL,
  `userIdentifier` varchar(50) NOT NULL,
  `providerId` varchar(45) NOT NULL,
  `email` varchar(45) DEFAULT NULL,
  `password` varchar(100) DEFAULT NULL,
  `firstName` varchar(45) DEFAULT NULL,
  `lastName` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`),
  KEY `user_identity_id_nonactive` (`userId`),
  CONSTRAINT `user_identity_id_nonactive` FOREIGN KEY (`userId`) REFERENCES `nonactiveusers` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `confirmationtokens` (
  `userIdentId` int(11) NOT NULL,
  `token` varchar(50) NOT NULL,
  `eventId` int(11) DEFAULT NULL,
  PRIMARY KEY (`userIdentId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `invites` (
  `id` int(11) NOT NULL,
  `author` int(11) NOT NULL,
  `receiver` int(11) DEFAULT NULL,
  `message` text,
  `createdOn` int(11) NOT NULL,
  `tournament_id` int(11) DEFAULT NULL,
  `events_id` int(11) DEFAULT NULL,
  `guilds_id` int(11) DEFAULT NULL,
  `teams_id` int(11) DEFAULT NULL,
  `users_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `tour_invite_id_idx` (`tournament_id`),
  KEY `event_invite_id_idx` (`events_id`),
  KEY `guild_invite_id_idx` (`guilds_id`),
  KEY `team_invite_id_idx` (`teams_id`),
  KEY `user_invite_id_idx` (`users_id`),
  CONSTRAINT `tour_invite_id` FOREIGN KEY (`tournament_id`) REFERENCES `tournament` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `guild_invite_id` FOREIGN KEY (`guilds_id`) REFERENCES `guilds` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `event_invite_id` FOREIGN KEY (`events_id`) REFERENCES `events` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `team_invite_id` FOREIGN KEY (`teams_id`) REFERENCES `teams` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `user_invite_id` FOREIGN KEY (`users_id`) REFERENCES `users` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;