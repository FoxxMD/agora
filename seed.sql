USE gtgamfest_scal;

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Seed the user
--
LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES ('1', 'test@test.com', '1410971983', NULL, NULL, NULL, 'Test User', 'admin');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Seed the user's identity
--
LOCK TABLES `useridentity` WRITE;
/*!40000 ALTER TABLE `useridentity` DISABLE KEYS */;
INSERT INTO `useridentity` VALUES ('1', '1', 'userpass', 'test@test.com', 'test@test.com', '1000:c2435ff18bf9dc3c6f667aea9cbb:c59dfd3c27a86f6c3b56cc53367c', NULL, NULL);
/*!40000 ALTER TABLE `useridentity` ENABLE KEYS */;
UNLOCK TABLES;


/*Insert some games*/
LOCK TABLES `games` WRITE;
/*!40000 ALTER TABLE `games` DISABLE KEYS */;
INSERT INTO `games` VALUES (1,'Halo 3','Bungie','http://bungie.com','FPS',1,1,NULL),(2,'League of Legends','Riot Games','http://lol.com','RTS',1,1,NULL),(3,'CS:GO','Valve','http://steampowered.com','FPS',1,1,NULL);
/*!40000 ALTER TABLE `games` ENABLE KEYS */;
UNLOCK TABLES;

/*Seed Tournament Types*/
LOCK TABLES `tournamenttypes` WRITE;
/*!40000 ALTER TABLE `tournamenttypes` DISABLE KEYS */;
INSERT INTO `tournamenttypes` VALUES (1,'Deathmatch',0),(2,'Team Deathmatch',1),(3,'Capture The Flag',1),(4,'Elimination',1),(5,'Round-Robin',1);
/*!40000 ALTER TABLE `tournamenttypes` ENABLE KEYS */;
UNLOCK TABLES;

/*Insert tournament types for games*/
LOCK TABLES `games_tournamenttypes` WRITE;
/*!40000 ALTER TABLE `games_tournamenttypes` DISABLE KEYS */;
INSERT INTO `games_tournamenttypes` VALUES (1,1,1),(1,2,2),(1,3,3),(2,4,4),(2,5,5);
/*!40000 ALTER TABLE `games_tournamenttypes` ENABLE KEYS */;
UNLOCK TABLES;

/* Seed one event
   This will only be used for Alpha. Once Beta dev begins there will be more events
 */
LOCK TABLES `events` WRITE;
/*!40000 ALTER TABLE `events` DISABLE KEYS */;
INSERT INTO `events` VALUES (1,'Public','GameFest: The Event');
/*!40000 ALTER TABLE `events` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Set test user as admin for seeded event
--
LOCK TABLES `user_events` WRITE;
/*!40000 ALTER TABLE `user_events` DISABLE KEYS */;
INSERT INTO `user_events` VALUES (1,1,1,0,1,1,0,NULL);
/*!40000 ALTER TABLE `user_events` ENABLE KEYS */;
UNLOCK TABLES;


/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
