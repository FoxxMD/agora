CREATE TABLE `users` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `email` varchar(50) NOT NULL,
  `password` varchar(100) NOT NULL,
  `salt` varchar(100) NOT NULL,
  `createdDate` datetime NOT NULL,
  `lastLogin` datetime DEFAULT NULL,
  `role` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `id_UNIQUE` (`id` ASC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `userdetails` (
  `firstName` varchar(50) DEFAULT NULL,
  `lastName` varchar(50) DEFAULT NULL,
  `globalHandle` varchar(50) DEFAULT NULL,
  `avatarUrl` varchar(100) DEFAULT NULL,
  `userId` int(11) DEFAULT NULL,
  `steamId` varchar(45) DEFAULT NULL,
  KEY `user_details_idx` (`userId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `events` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `eventType` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `id_UNIQUE` (`id` ASC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `eventdetails` (
  `eventId` int(11) NOT NULL,
  `name` varchar(45) NOT NULL,
  `address` varchar(45) DEFAULT NULL,
  `city` varchar(45) DEFAULT NULL,
  `state` varchar(45) DEFAULT NULL,
  `eventType` int(11) NOT NULL,
  `timeStart` datetime DEFAULT NULL,
  `timeEnd` datetime DEFAULT NULL,
  `description` longtext,
  `rules` longtext,
  `prizes` longtext,
  `streams` longtext,
  `servers` longtext,
  `eventdetailscol` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`eventId`),
  CONSTRAINT `event_details_id` FOREIGN KEY (`eventId`) REFERENCES `events` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `games` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(45) NOT NULL,
  `publisher` varchar(45) DEFAULT NULL,
  `website` varchar(100) DEFAULT NULL,
  `gameType` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `id_UNIQUE` (`id` ASC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `teams` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(45) NOT NULL,
  `createdDate` datetime NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `id_UNIQUE` (`id` ASC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `tournament` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `bracketType` int(11) NOT NULL,
  `registrationType` int(11) NOT NULL,
  `tournamentType` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `id_UNIQUE` (`id` ASC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `tournamentdetails` (
  `tournamentId` int(11) NOT NULL,
  `name` varchar(45) DEFAULT NULL,
  `gamePlayed` varchar(45) DEFAULT NULL,
  `description` longtext,
  `rules` longtext,
  `prizes` longtext,
  `streams` longtext,
  `timeStart` datetime DEFAULT NULL,
  `timeEnd` datetime DEFAULT NULL,
  `tournamentdetailscol` varchar(45) DEFAULT NULL,
  KEY `tournament_details_id_idx` (`tournamentId`),
  CONSTRAINT `tournament_details_id` FOREIGN KEY (`tournamentId`) REFERENCES `tournament` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `game_tournaments` (
  `gameId` int(11) NOT NULL,
  `tournamentId` int(11) NOT NULL,
  KEY `game_id_idx` (`gameId`),
  KEY `tournament_id_idx` (`tournamentId`),
  CONSTRAINT `game_id` FOREIGN KEY (`gameId`) REFERENCES `games` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `tournament_id` FOREIGN KEY (`tournamentId`) REFERENCES `tournament` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



CREATE TABLE `games_events` (
  `gameId` int(11) NOT NULL,
  `eventId` int(11) NOT NULL,
  KEY `game_event_id_idx` (`gameId`),
  KEY `event_game_id_idx` (`eventId`),
  CONSTRAINT `game_event_id` FOREIGN KEY (`gameId`) REFERENCES `games` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `event_game_id` FOREIGN KEY (`eventId`) REFERENCES `events` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



CREATE TABLE `teams_tournaments` (
  `teamId` int(11) NOT NULL,
  `tournamentId` int(11) NOT NULL,
  `isPresent` binary(1) DEFAULT '0',
  KEY `team_tournament_id_idx` (`teamId`),
  KEY `tournament_team_id_idx` (`tournamentId`),
  CONSTRAINT `team_tournament_id` FOREIGN KEY (`teamId`) REFERENCES `teams` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `tournament_team_id` FOREIGN KEY (`tournamentId`) REFERENCES `tournament` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `teams_users` (
  `teamId` int(11) NOT NULL,
  `userId` int(11) NOT NULL,
  `isCaptain` binary(1) NOT NULL DEFAULT '0',
  KEY `team_id_idx` (`teamId`),
  KEY `user_id_idx` (`userId`),
  CONSTRAINT `team_id` FOREIGN KEY (`teamId`) REFERENCES `teams` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION,
  CONSTRAINT `user_id` FOREIGN KEY (`userId`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `tournaments_event` (
  `tournamentId` int(11) NOT NULL,
  `eventId` int(11) NOT NULL,
  KEY `tournament_event_id_idx` (`tournamentId`),
  KEY `event_tournament_id_idx` (`eventId`),
  CONSTRAINT `tournament_event_id` FOREIGN KEY (`tournamentId`) REFERENCES `tournament` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `event_tournament_id` FOREIGN KEY (`eventId`) REFERENCES `events` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `user_events` (
  `userId` int(11) NOT NULL,
  `eventId` int(11) NOT NULL,
  `isPresent` binary(1) NOT NULL DEFAULT '0',
  KEY `user_event_id_idx` (`userId`),
  KEY `event_user_id_idx` (`eventId`),
  CONSTRAINT `user_event_id` FOREIGN KEY (`userId`) REFERENCES `users` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `event_user_id` FOREIGN KEY (`eventId`) REFERENCES `events` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `user_tournaments` (
  `userId` int(11) NOT NULL,
  `tournamentId` int(11) NOT NULL,
  `isPresent` binary(1) NOT NULL DEFAULT '0',
  KEY `user_tournament_id_idx` (`userId`),
  KEY `tournament_user_id_idx` (`tournamentId`),
  CONSTRAINT `user_tournament_id` FOREIGN KEY (`userId`) REFERENCES `users` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `tournament_user_id` FOREIGN KEY (`tournamentId`) REFERENCES `tournament` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
