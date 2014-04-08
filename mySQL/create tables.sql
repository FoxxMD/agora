CREATE TABLE `users` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `email` varchar(60) DEFAULT NULL,
  `password` varchar(300) DEFAULT NULL,
  `salt` varchar(300) DEFAULT NULL,
  `alias` varchar(300) DEFAULT NULL,
  `paid` varchar(5) DEFAULT NULL,
  `entered` varchar(5) DEFAULT NULL,
  `steam` varchar(60) DEFAULT NULL,
  `bn` varchar(60) DEFAULT NULL,
  `lol` varchar(60) DEFAULT NULL,
  `xbox` varchar(100) DEFAULT NULL,
  `ign` varchar(100) DEFAULT NULL,
  `role` varchar(10) DEFAULT '0',
  `locktime` int(30) DEFAULT NULL,
  `attempt` int(11) DEFAULT NULL,
  `authtoken` varchar(300) DEFAULT NULL,
  `authExpire` int(30) DEFAULT NULL,
  `resetToken` varchar(300) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8;

CREATE TABLE `teams` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(100) DEFAULT NULL,
  `captain` int(11) DEFAULT NULL,
  `password` varchar(5) DEFAULT NULL,
  `des` text,
  `logo` varchar(200) DEFAULT NULL,
  `game` varchar(50) DEFAULT NULL,
  `member1` int(11) DEFAULT 0,
  `member2` int(11) DEFAULT 0,
  `member3` int(11) DEFAULT 0,
  `member4` int(11) DEFAULT 0,
  PRIMARY KEY (`ID`),
  KEY `id_idx` (`captain`),
  KEY `id_idx1` (`member1`),
  KEY `id_idx2` (`member2`),
  KEY `id_idx3` (`member3`),
  KEY `id_idx4` (`member4`),
  CONSTRAINT `id` FOREIGN KEY (`captain`) REFERENCES `users` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;

CREATE TABLE `tournaments` (
  `Id` int(11) NOT NULL AUTO_INCREMENT,
  `Game` varchar(60) NOT NULL,
  `Name` varchar(60) NOT NULL,
  `isPlaying` int(3) DEFAULT '0',
  `jsonName` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`Id`),
  UNIQUE KEY `Id_UNIQUE` (`Id`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8;

CREATE TABLE `tournament_users` (
  `Id` int(11) NOT NULL AUTO_INCREMENT,
  `UserId` int(11) NOT NULL,
  `TournamentId` int(11) NOT NULL,
  `isAdmin` bit(1) NOT NULL DEFAULT b'0',
  `isPresent` bit(1) NOT NULL DEFAULT b'0',
  PRIMARY KEY (`Id`),
  UNIQUE KEY `Id_UNIQUE` (`Id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `tournament_teams` (
  `Id` int(11) NOT NULL AUTO_INCREMENT,
  `TeamId` int(11) NOT NULL,
  `TournamentId` int(11) NOT NULL,
  `isPresent` bit(1) NOT NULL DEFAULT b'0',
  PRIMARY KEY (`Id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
