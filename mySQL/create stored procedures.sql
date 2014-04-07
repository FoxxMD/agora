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
CREATE DEFINER=`root`@`localhost` PROCEDURE `getTeamsByTournament`(IN inputTournamentId int(11))
BEGIN

SELECT te.ID, te.name, te.captain, t.isPresent FROM teams te INNER JOIN tournament_teams t ON t.TeamId = te.ID WHERE t.TournamentId=inputTournamentId;

END$$
DELIMITER ;

-- Get Users by Tournament Id
DELIMITER $$
CREATE DEFINER=`root`@`localhost` PROCEDURE `getUsersByTournament`(IN inputTournamentId int(11))
BEGIN

SELECT u.id, u.alias , u.email, u.steam, u.bn, u.lol, u.xbox, u.ign, u.role, t.isAdmin, t.isPresent FROM users u INNER JOIN tournament_users t ON t.UserId = u.id WHERE t.TournamentId=inputTournamentId;

END$$
DELIMITER ;

-- Get Tournament Info
DELIMITER $$
CREATE DEFINER=`root`@`localhost` PROCEDURE `getTournamentInfo`(IN inputTournamentId int(11))
BEGIN

DECLARE teamCount INT;
DECLARE playerCount INT;

SELECT COUNT(*) INTO @teamCount FROM tournament_teams t where t.TournamentId=inputTournamentId;
SELECT COUNT(*) INTO @playerCount FROM tournament_users t where t.TournamentId=inputTournamentId;

Select *,@teamCount,@playerCount FROM tournaments t where t.Id=inputTournamentId;

END$$
DELIMITER ;

-- Get Tournaments a user is participating in
DELIMITER $$
CREATE DEFINER=`root`@`localhost` PROCEDURE `getTournamentsByUserId`(IN userId int(11))
BEGIN

Select tour.* from tournaments tour INNER JOIN tournament_users tu on tu.TournamentId = tour.Id where tu.UserId = userId;

END$$
DELIMITER ;

-- Get Tournaments a team is participating in
DELIMITER $$
CREATE DEFINER=`root`@`localhost` PROCEDURE `getTournamentsByTeamId`(IN teamId int(11))
BEGIN

Select tour.* from tournaments tour INNER JOIN tournament_teams tt on tt.TournamentId = tour.Id where tt.TeamId = teamId;

END$$
DELIMITER ;

-- Get All teams a user is a captain for
DELIMITER $$
CREATE DEFINER=`root`@`localhost` PROCEDURE `getTeamsByCaptain`(IN captainId int(11))
BEGIN

SELECT t.ID,t.name,t.game FROM teams t where t.captain = captainId;

END$$
DELIMITER ;

-- Get all tournaments info
DELIMITER $$
CREATE DEFINER=`root`@`localhost` PROCEDURE `getAllTournamentInfo`()
BEGIN

drop temporary table if exists team_counts;
drop temporary table if exists player_counts;

create temporary table team_counts
(
Id int unsigned,
Game varchar(100),
Name varchar(100),
teamCount int unsigned
)engine=memory;

create temporary table player_counts
(
Id int unsigned,
Game varchar(100),
Name varchar(100),
playerCount int unsigned
)engine=memory;

insert into team_counts(Id, Game, Name, teamCount)
select t.Id,t.Game,t.Name, COUNT(tt.TeamId) as teamCount
from tournaments t
left join tournament_teams tt on tt.TournamentId = t.Id
group by t.Id,t.Game,t.Name;

insert into player_counts(Id, Game, Name, playerCount)
select t.Id,t.Game,t.Name, COUNT(tu.UserId) as playerCount
from tournaments t
left join tournament_users tu on tu.TournamentId =  t.Id
group by t.Id,t.Game,t.Name;


Select t.Id,T.Game,T.Name,T.teamCount,p.playerCount
from team_counts t
left join player_counts p on t.Id = p.Id;

END$$
DELIMITER ;

