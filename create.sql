CREATE DATABASE `gtgamfest_scal` /*!40100 DEFAULT CHARACTER SET utf8 */;
USE gtgamfest_scal;
CREATE TABLE apikeys
(
    id INT PRIMARY KEY NOT NULL,
    apiToken VARCHAR(100) NOT NULL,
    FOREIGN KEY (id) REFERENCES users (id)
);
CREATE UNIQUE INDEX apiToken_UNIQUE ON apikeys (apiToken);
CREATE UNIQUE INDEX id_UNIQUE ON apikeys (id);
CREATE TABLE confirmationtokens
(
    userIdentId INT PRIMARY KEY NOT NULL,
    token VARCHAR(50) NOT NULL,
    eventId INT
);
CREATE TABLE eventdetails
(
    events_id INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
    location VARCHAR(45),
    address VARCHAR(45),
    city VARCHAR(45),
    state VARCHAR(45),
    timeStart INT,
    timeEnd INT,
    description LONGTEXT,
    rules LONGTEXT,
    prizes LONGTEXT,
    streams LONGTEXT,
    servers LONGTEXT,
    scheduledevents LONGTEXT,
    credits LONGTEXT,
    faq LONGTEXT,
    FOREIGN KEY (events_id) REFERENCES events (id) ON DELETE CASCADE
);
CREATE TABLE eventpayments
(
    id INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
    events_id INT NOT NULL,
    paytype VARCHAR(45) NOT NULL,
    secret VARCHAR(45),
    public VARCHAR(45),
    address VARCHAR(45),
    amount DOUBLE NOT NULL,
    isenabled SMALLINT DEFAULT 1 NOT NULL,
    FOREIGN KEY (events_id) REFERENCES events (id) ON DELETE CASCADE
);
CREATE INDEX event_payment_id ON eventpayments (events_id);
CREATE TABLE events
(
    id INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
    eventType VARCHAR(15) NOT NULL,
    name VARCHAR(45) NOT NULL
);
CREATE UNIQUE INDEX id_UNIQUE ON events (id);
CREATE TABLE games
(
    id INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
    name VARCHAR(45) NOT NULL,
    publisher VARCHAR(45),
    website VARCHAR(100),
    gameType VARCHAR(20) NOT NULL,
    userPlay TINYINT DEFAULT 1 NOT NULL,
    teamPlay TINYINT DEFAULT 1 NOT NULL,
    logoFilename VARCHAR(30)
);
CREATE UNIQUE INDEX id_UNIQUE ON games (id);
CREATE TABLE games_events
(
    games_id INT NOT NULL,
    events_id INT NOT NULL,
    FOREIGN KEY (events_id) REFERENCES events (id) ON DELETE CASCADE,
    FOREIGN KEY (games_id) REFERENCES games (id) ON DELETE CASCADE
);
CREATE INDEX event_game_id_idx ON games_events (events_id);
CREATE INDEX game_event_id_idx ON games_events (games_id);
CREATE TABLE games_tournamenttypes
(
    games_id INT NOT NULL,
    tournamenttypes_id INT NOT NULL,
    id INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
    FOREIGN KEY (games_id) REFERENCES games (id) ON DELETE CASCADE,
    FOREIGN KEY (tournamenttypes_id) REFERENCES tournamenttypes (Id) ON DELETE CASCADE
);
CREATE INDEX game_tt_id_idx ON games_tournamenttypes (games_id);
CREATE INDEX tt_id_idx ON games_tournamenttypes (tournamenttypes_id);
CREATE TABLE guilds
(
    id INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
    name VARCHAR(45) NOT NULL,
    description LONGTEXT,
    maxPlayers INT,
    joinType VARCHAR(45) NOT NULL,
    createdDate INT NOT NULL
);
CREATE TABLE guilds_games
(
    id INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
    guilds_id INT NOT NULL,
    games_id INT NOT NULL,
    FOREIGN KEY (games_id) REFERENCES games (id) ON DELETE CASCADE,
    FOREIGN KEY (guilds_id) REFERENCES guilds (id) ON DELETE CASCADE
);
CREATE INDEX guild_game_id_idx ON guilds_games (games_id);
CREATE INDEX guild_guild_id_idx ON guilds_games (guilds_id);
CREATE TABLE guilds_users
(
    id INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
    guilds_id INT NOT NULL,
    users_id INT NOT NULL,
    isCaptain BIT DEFAULT b'0' NOT NULL,
    FOREIGN KEY (guilds_id) REFERENCES guilds (id) ON DELETE CASCADE,
    FOREIGN KEY (users_id) REFERENCES users (id) ON DELETE CASCADE
);
CREATE UNIQUE INDEX id_UNIQUE ON guilds_users (id);
CREATE INDEX guild_id_idx ON guilds_users (guilds_id);
CREATE INDEX user_id_idx ON guilds_users (users_id);
CREATE TABLE invites
(
    id INT PRIMARY KEY NOT NULL,
    author INT NOT NULL,
    receiver INT,
    message LONGTEXT,
    createdOn INT NOT NULL,
    tournament_id INT,
    events_id INT,
    guilds_id INT,
    teams_id INT,
    users_id INT,
    FOREIGN KEY (events_id) REFERENCES events (id),
    FOREIGN KEY (guilds_id) REFERENCES guilds (id),
    FOREIGN KEY (teams_id) REFERENCES teams (id),
    FOREIGN KEY (tournament_id) REFERENCES tournament (id),
    FOREIGN KEY (users_id) REFERENCES users (id)
);
CREATE INDEX event_invite_id_idx ON invites (events_id);
CREATE INDEX guild_invite_id_idx ON invites (guilds_id);
CREATE INDEX team_invite_id_idx ON invites (teams_id);
CREATE INDEX tour_invite_id_idx ON invites (tournament_id);
CREATE INDEX user_invite_id_idx ON invites (users_id);
CREATE TABLE nonactiveuseridentity
(
    id INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
    userId INT NOT NULL,
    userIdentifier VARCHAR(50) NOT NULL,
    providerId VARCHAR(45) NOT NULL,
    email VARCHAR(45),
    password VARCHAR(100),
    firstName VARCHAR(45),
    lastName VARCHAR(45),
    FOREIGN KEY (userId) REFERENCES nonactiveusers (id) ON DELETE CASCADE
);
CREATE UNIQUE INDEX id_UNIQUE ON nonactiveuseridentity (id);
CREATE INDEX user_identity_id_nonactive ON nonactiveuseridentity (userId);
CREATE TABLE nonactiveusers
(
    id INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
    email VARCHAR(50) NOT NULL,
    createdDate DATETIME NOT NULL,
    lastLogin DATETIME,
    firstName VARCHAR(45),
    lastName VARCHAR(45),
    globalHandle VARCHAR(45),
    role VARCHAR(11) NOT NULL
);
CREATE UNIQUE INDEX id_UNIQUE ON nonactiveusers (id);
CREATE TABLE passwordtokens
(
    id INT PRIMARY KEY NOT NULL,
    token VARCHAR(70) NOT NULL,
    FOREIGN KEY (id) REFERENCES users (id) ON DELETE CASCADE
);
CREATE UNIQUE INDEX unique_Id ON passwordtokens (id);
CREATE UNIQUE INDEX unique_token ON passwordtokens (token);
CREATE TABLE teams
(
    id INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
    name VARCHAR(45) NOT NULL,
    joinType VARCHAR(45) NOT NULL,
    tournament_id INT NOT NULL,
    createdDate INT,
    isPresent SMALLINT DEFAULT 0 NOT NULL,
    guildOnly SMALLINT DEFAULT 0 NOT NULL,
    guildId INT,
    FOREIGN KEY (tournament_id) REFERENCES tournament (id) ON DELETE CASCADE
);
CREATE INDEX team_tournament_id_idx ON teams (tournament_id);
CREATE TABLE teams_users
(
    id INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
    teams_id INT NOT NULL,
    users_id INT NOT NULL,
    isCaptain BIT DEFAULT b'0' NOT NULL,
    FOREIGN KEY (teams_id) REFERENCES teams (id) ON DELETE CASCADE,
    FOREIGN KEY (users_id) REFERENCES users (id) ON DELETE CASCADE
);
CREATE UNIQUE INDEX id_UNIQUE ON teams_users (id);
CREATE INDEX team_id_idx ON teams_users (teams_id);
CREATE INDEX user_id_idx ON teams_users (users_id);
CREATE TABLE tokens
(
    id INT PRIMARY KEY NOT NULL,
    token VARCHAR(100) NOT NULL,
    issuedOn INT,
    FOREIGN KEY (id) REFERENCES users (id) ON DELETE CASCADE
);
CREATE UNIQUE INDEX token_UNIQUE ON tokens (token);
CREATE TABLE tournament
(
    id INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
    tournamenttypes_id INT NOT NULL,
    registrationType VARCHAR(15) NOT NULL,
    games_id INT NOT NULL,
    events_id INT NOT NULL,
    FOREIGN KEY (tournamenttypes_id) REFERENCES tournamenttypes (Id),
    FOREIGN KEY (games_id) REFERENCES games (id),
    FOREIGN KEY (events_id) REFERENCES events (id)
);
CREATE UNIQUE INDEX id_UNIQUE ON tournament (id);
CREATE INDEX tournament_event_id ON tournament (games_id);
CREATE INDEX tournament_game_id ON tournament (events_id);
CREATE INDEX tournament_type_id_idx1 ON tournament (tournamenttypes_id);
CREATE TABLE tournamentdetails
(
    tournament_id INT PRIMARY KEY NOT NULL,
    name VARCHAR(45),
    gamePlayed VARCHAR(45),
    description LONGTEXT,
    rules LONGTEXT,
    prizes LONGTEXT,
    streams LONGTEXT,
    servers LONGTEXT,
    timeStart INT,
    timeEnd INT,
    tournamentdetailscol VARCHAR(45),
    location LONGTEXT,
    teamMaxSize INT DEFAULT 0 NOT NULL,
    teamMinSize INT,
    playerMinSize INT DEFAULT 0 NOT NULL,
    playerMaxSize INT DEFAULT 0 NOT NULL,
    locationsub VARCHAR(255),
    FOREIGN KEY (tournament_id) REFERENCES tournament (id) ON DELETE CASCADE
);
CREATE INDEX tournament_details_id_idx ON tournamentdetails (tournament_id);
CREATE TABLE tournamenttypes
(
    Id INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
    name VARCHAR(45) NOT NULL,
    teamPlay TINYINT NOT NULL
);
CREATE TABLE user_events
(
    id INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
    users_id INT NOT NULL,
    events_id INT NOT NULL,
    isPresent TINYINT DEFAULT 0 NOT NULL,
    isAdmin TINYINT DEFAULT 0 NOT NULL,
    isModerator TINYINT DEFAULT 0 NOT NULL,
    hasPaid TINYINT DEFAULT 0 NOT NULL,
    receiptId VARCHAR(45),
    FOREIGN KEY (events_id) REFERENCES events (id) ON DELETE CASCADE,
    FOREIGN KEY (users_id) REFERENCES users (id) ON DELETE CASCADE
);
CREATE UNIQUE INDEX id_UNIQUE ON user_events (id);
CREATE INDEX event_user_id_idx ON user_events (events_id);
CREATE INDEX user_event_id_idx ON user_events (users_id);
CREATE TABLE user_tournaments
(
    id INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
    users_id INT NOT NULL,
    tournament_id INT NOT NULL,
    isPresent TINYINT DEFAULT 0 NOT NULL,
    isAdmin TINYINT DEFAULT 0 NOT NULL,
    isModerator TINYINT DEFAULT 0 NOT NULL,
    FOREIGN KEY (tournament_id) REFERENCES tournament (id) ON DELETE CASCADE,
    FOREIGN KEY (users_id) REFERENCES users (id) ON DELETE CASCADE
);
CREATE UNIQUE INDEX id_UNIQUE ON user_tournaments (id);
CREATE INDEX tournament_user_id_idx ON user_tournaments (tournament_id);
CREATE INDEX user_tournament_id_idx ON user_tournaments (users_id);
CREATE TABLE useridentity
(
    id INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
    users_id INT NOT NULL,
    userIdentifier VARCHAR(50) NOT NULL,
    providerId VARCHAR(45) NOT NULL,
    email VARCHAR(45),
    password VARCHAR(100),
    firstName VARCHAR(45),
    lastName VARCHAR(45),
    FOREIGN KEY (users_id) REFERENCES users (id) ON DELETE CASCADE
);
CREATE UNIQUE INDEX id_UNIQUE ON useridentity (id);
CREATE INDEX user_identity_id ON useridentity (users_id);
CREATE TABLE userplatformprofile
(
    users_id INT PRIMARY KEY NOT NULL,
    platform VARCHAR(45) NOT NULL,
    identifier VARCHAR(45) NOT NULL,
    FOREIGN KEY (users_id) REFERENCES users (id) ON DELETE CASCADE
);
CREATE INDEX user_platform_id_idx ON userplatformprofile (users_id);
CREATE TABLE users
(
    id INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
    email VARCHAR(50) NOT NULL,
    createdDate INT NOT NULL,
    lastLogin DATETIME,
    firstName VARCHAR(45),
    lastName VARCHAR(45),
    globalHandle VARCHAR(45),
    role VARCHAR(11) NOT NULL
);
CREATE UNIQUE INDEX id_UNIQUE ON users (id);
