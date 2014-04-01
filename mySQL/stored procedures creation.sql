DELIMITER $$
CREATE DEFINER=`root`@`localhost` PROCEDURE `getTeamByIds`(IN captainId int(11), IN member1Id int(11), IN member2Id int(11), IN member3Id int(11), IN member4Id int(11))
BEGIN

DECLARE captainName varchar(50);
DECLARE member1Name varchar(50);
DECLARE member2Name varchar(50);
DECLARE member3Name varchar(50);
DECLARE member4Name varchar(50);

if captainId != 0 then
Select alias INTO @captainName from users where id = captainId;
end if;
if member1Id != 0 then
Select alias INTO @member1Name from users where id = member1Id;
end if;
if member2id != 0 then
Select alias INTO @member2Name from users where id = member2Id;
end if;
if member3id != 0 then
Select alias INTO @member3Name from users where id = member3Id;
end if;
if member4id != 0 then
Select alias INTO @member4Name from users where id = member4Id;
end if;

SELECT @captainName,@member1Name,@member2Name,@member3Name,@member4Name;

END$$
DELIMITER ;

-- Get Teams by Tournament Id
DELIMITER $$
CREATE DEFINER=`root`@`localhost` PROCEDURE `getTeamByTournament`(IN inputTournamentId int(11))
BEGIN

SELECT te.ID, te.name, te.captain, t.isPresent FROM teams te INNER JOIN tournament_teams t ON t.TeamId = te.ID WHERE t.TournamentId=inputTournamentId;

END$$
DELIMITER ;

--Get Users by Tournament Id
DELIMITER $$
CREATE DEFINER=`root`@`localhost` PROCEDURE `getUsersByTournament`(IN inputTournamentId int(11))
BEGIN

SELECT u.id, u.alias , u.email, u.steam, u.bn, u.lol, u.xbox, u.ign, u.role, t.isAdmin, t.isPresent FROM users u INNER JOIN tournament_users t ON t.UserId = u.id WHERE t.TournamentId=inputTournamentId;

END$$
DELIMITER ;