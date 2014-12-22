CREATE TABLE apikeys
(
    id INT PRIMARY KEY NOT NULL,
    apiToken VARCHAR(100) NOT NULL,
    FOREIGN KEY (id) REFERENCES users (id)
);
CREATE UNIQUE INDEX apiToken_UNIQUE ON apikeys (apiToken);
CREATE UNIQUE INDEX id_UNIQUE ON apikeys (id);
CREATE TABLE bracket_types
(
    Id INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL
);
CREATE TABLE brackets
(
    bracketTypeId INT NOT NULL,
    `order` INT DEFAULT 1 NOT NULL,
    seedSize INT NOT NULL,
    teamPlay BIT DEFAULT b'0' NOT NULL,
    tournamentId INT,
    bracketId VARCHAR(100),
    ownerId INT,
    id INT UNSIGNED PRIMARY KEY NOT NULL AUTO_INCREMENT
);
CREATE TABLE confirmationtokens
(
    userIdentId INT NOT NULL,
    token VARCHAR(50) NOT NULL,
    eventId INT,
    id INT PRIMARY KEY NOT NULL AUTO_INCREMENT
);
CREATE TABLE event_payments
(
    id INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
    eventId INT NOT NULL,
    paytype VARCHAR(45) NOT NULL,
    secretKey VARCHAR(45),
    publicKey VARCHAR(45),
    address VARCHAR(45),
    amount DOUBLE NOT NULL,
    isenabled BIT DEFAULT b'1' NOT NULL,
    FOREIGN KEY (eventId) REFERENCES events (id) ON DELETE CASCADE
);
CREATE INDEX event_payment_id ON event_payments (eventId);
CREATE TABLE eventdetails
(
    eventId INT PRIMARY KEY NOT NULL,
    locationName VARCHAR(45),
    address VARCHAR(45),
    city VARCHAR(45),
    state VARCHAR(45),
    description LONGTEXT,
    rules LONGTEXT,
    prizes LONGTEXT,
    streams LONGTEXT,
    servers LONGTEXT,
    timeStart INT,
    timeEnd INT,
    scheduledevents LONGTEXT,
    credits LONGTEXT,
    faq LONGTEXT,
    FOREIGN KEY (eventId) REFERENCES events (id) ON DELETE CASCADE
);
CREATE TABLE events
(
    id INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
    name VARCHAR(45) NOT NULL,
    joinType VARCHAR(15) NOT NULL
);
CREATE UNIQUE INDEX id_UNIQUE ON events (id);
CREATE TABLE eventuser
(
    id INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
    userId INT NOT NULL,
    eventId INT NOT NULL,
    isPresent BIT DEFAULT b'0' NOT NULL,
    isAdmin BIT DEFAULT b'0' NOT NULL,
    isModerator BIT DEFAULT b'0' NOT NULL,
    hasPaid BIT DEFAULT b'0' NOT NULL,
    paymentType VARCHAR(45),
    receiptId VARCHAR(45),
    customerId VARCHAR(45),
    FOREIGN KEY (eventId) REFERENCES events (id) ON DELETE CASCADE,
    FOREIGN KEY (userId) REFERENCES users (id) ON DELETE CASCADE
);
CREATE UNIQUE INDEX id_UNIQUE ON eventuser (id);
CREATE INDEX event_user_id_idx ON eventuser (eventId);
CREATE INDEX user_event_id_idx ON eventuser (userId);
CREATE TABLE gamebrackettype
(
    gameId INT NOT NULL,
    bracketTypeId INT NOT NULL,
    id INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
    FOREIGN KEY (bracketTypeId) REFERENCES bracket_types (Id) ON DELETE CASCADE,
    FOREIGN KEY (gameId) REFERENCES games (id) ON DELETE CASCADE
);
CREATE INDEX game_tt_id_idx ON gamebrackettype (gameId);
CREATE INDEX tt_id_idx ON gamebrackettype (bracketTypeId);
CREATE TABLE games
(
    id INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
    name VARCHAR(45) NOT NULL,
    publisher VARCHAR(45),
    website VARCHAR(100),
    gameType VARCHAR(20) NOT NULL,
    userPlay BIT DEFAULT b'1' NOT NULL,
    teamPlay BIT DEFAULT b'1' NOT NULL,
    logoFilename VARCHAR(30)
);
CREATE UNIQUE INDEX id_UNIQUE ON games (id);
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
    guildId INT NOT NULL,
    gameId INT NOT NULL,
    FOREIGN KEY (gameId) REFERENCES games (id) ON DELETE CASCADE,
    FOREIGN KEY (guildId) REFERENCES guilds (id) ON DELETE CASCADE
);
CREATE INDEX guild_game_id_idx ON guilds_games (gameId);
CREATE INDEX guild_guild_id_idx ON guilds_games (guildId);
CREATE TABLE guilduser
(
    id INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
    guildId INT NOT NULL,
    userId INT NOT NULL,
    isCaptain BIT DEFAULT b'0' NOT NULL,
    FOREIGN KEY (guildId) REFERENCES guilds (id) ON DELETE CASCADE,
    FOREIGN KEY (userId) REFERENCES users (id) ON DELETE CASCADE
);
CREATE UNIQUE INDEX id_UNIQUE ON guilduser (id);
CREATE INDEX guild_id_idx ON guilduser (guildId);
CREATE INDEX user_id_idx ON guilduser (userId);
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
    FOREIGN KEY (tournament_id) REFERENCES tournaments (id),
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
    users_id INT NOT NULL,
    userIdentifier VARCHAR(50) NOT NULL,
    providerId VARCHAR(45) NOT NULL,
    email VARCHAR(45),
    password VARCHAR(100),
    firstName VARCHAR(45),
    lastName VARCHAR(45),
    FOREIGN KEY (users_id) REFERENCES nonactiveusers (id) ON DELETE CASCADE
);
CREATE UNIQUE INDEX id_UNIQUE ON nonactiveuseridentity (id);
CREATE INDEX user_identity_id_nonactive ON nonactiveuseridentity (users_id);
CREATE TABLE nonactiveusers
(
    id INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
    email VARCHAR(50) NOT NULL,
    createdDate INT NOT NULL,
    firstName VARCHAR(45),
    lastName VARCHAR(45),
    globalHandle VARCHAR(45) NOT NULL,
    role VARCHAR(11) NOT NULL
);
CREATE UNIQUE INDEX id_UNIQUE ON nonactiveusers (id);
CREATE TABLE passwordtokens
(
    userId INT NOT NULL,
    token VARCHAR(70) NOT NULL,
    id INT UNSIGNED PRIMARY KEY NOT NULL
);
CREATE UNIQUE INDEX id_UNIQUE ON passwordtokens (id);
CREATE UNIQUE INDEX unique_Id ON passwordtokens (userId);
CREATE UNIQUE INDEX unique_token ON passwordtokens (token);
CREATE TABLE teams
(
    id INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
    name VARCHAR(45) NOT NULL,
    joinType VARCHAR(45) NOT NULL,
    tournamentId INT NOT NULL,
    createdDate INT NOT NULL,
    isPresent BIT DEFAULT b'0' NOT NULL,
    guildOnly BIT DEFAULT b'0' NOT NULL,
    guildId INT,
    FOREIGN KEY (tournamentId) REFERENCES tournaments (id) ON DELETE CASCADE
);
CREATE INDEX team_tournament_id_idx ON teams (tournamentId);
CREATE TABLE teamuser
(
    id INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
    teamId INT NOT NULL,
    userId INT NOT NULL,
    isCaptain BIT DEFAULT b'0' NOT NULL,
    FOREIGN KEY (teamId) REFERENCES teams (id) ON DELETE CASCADE,
    FOREIGN KEY (userId) REFERENCES users (id) ON DELETE CASCADE
);
CREATE UNIQUE INDEX id_UNIQUE ON teamuser (id);
CREATE INDEX team_id_idx ON teamuser (teamId);
CREATE INDEX user_id_idx ON teamuser (userId);
CREATE TABLE tokens
(
    userId INT NOT NULL,
    token VARCHAR(100) NOT NULL,
    issuedOn INT,
    id INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
    FOREIGN KEY (userId) REFERENCES users (id) ON DELETE CASCADE
);
CREATE UNIQUE INDEX token_UNIQUE ON tokens (token);
CREATE UNIQUE INDEX userId_UNIQUE ON tokens (userId);
CREATE TABLE tournaments
(
    id INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
    registrationType VARCHAR(15) NOT NULL,
    gameId INT NOT NULL,
    eventId INT NOT NULL,
    bracketId VARCHAR(100),
    FOREIGN KEY (gameId) REFERENCES games (id),
    FOREIGN KEY (eventId) REFERENCES events (id)
);
CREATE UNIQUE INDEX id_UNIQUE ON tournaments (id);
CREATE INDEX tournament_event_id ON tournaments (gameId);
CREATE INDEX tournament_game_id ON tournaments (eventId);
CREATE TABLE tournaments_details
(
    tournamentId INT NOT NULL,
    name VARCHAR(45),
    gamePlayed VARCHAR(45),
    description LONGTEXT,
    location LONGTEXT,
    locationsub VARCHAR(255),
    rules LONGTEXT,
    prizes LONGTEXT,
    streams LONGTEXT,
    servers LONGTEXT,
    timeStart INT,
    timeEnd INT,
    teamMinSize INT,
    teamMaxSize INT,
    playerMinSize INT,
    playerMaxSize INT,
    id INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
    FOREIGN KEY (tournamentId) REFERENCES tournaments (id) ON DELETE CASCADE
);
CREATE UNIQUE INDEX id_UNIQUE ON tournaments_details (id);
CREATE UNIQUE INDEX tournamentId_UNIQUE ON tournaments_details (tournamentId);
CREATE INDEX tournament_details_id_idx ON tournaments_details (tournamentId);
CREATE TABLE tournamentuser
(
    id INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
    userId INT NOT NULL,
    tournamentId INT NOT NULL,
    isPresent BIT DEFAULT b'0' NOT NULL,
    isAdmin BIT DEFAULT b'0' NOT NULL,
    isModerator BIT DEFAULT b'0' NOT NULL,
    FOREIGN KEY (tournamentId) REFERENCES tournaments (id) ON DELETE CASCADE,
    FOREIGN KEY (userId) REFERENCES users (id) ON DELETE CASCADE
);
CREATE UNIQUE INDEX id_UNIQUE ON tournamentuser (id);
CREATE INDEX tournament_user_id_idx ON tournamentuser (tournamentId);
CREATE INDEX user_tournament_id_idx ON tournamentuser (userId);
CREATE TABLE users
(
    id INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
    email VARCHAR(50) NOT NULL,
    createdDate INT NOT NULL,
    firstName VARCHAR(45),
    lastName VARCHAR(45),
    globalHandle VARCHAR(45) NOT NULL,
    role VARCHAR(11) NOT NULL
);
CREATE UNIQUE INDEX id_UNIQUE ON users (id);
CREATE TABLE users_identity
(
    id INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
    userId INT NOT NULL,
    userIdentifier VARCHAR(50) NOT NULL,
    providerId VARCHAR(45) NOT NULL,
    email VARCHAR(45),
    password VARCHAR(100),
    firstName VARCHAR(45),
    lastName VARCHAR(45),
    FOREIGN KEY (userId) REFERENCES users (id) ON DELETE CASCADE
);
CREATE UNIQUE INDEX id_UNIQUE ON users_identity (id);
CREATE INDEX user_identity_id ON users_identity (userId);
CREATE TABLE users_platform_profile
(
    id INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
    userId INT NOT NULL,
    platform VARCHAR(45) NOT NULL,
    identifier VARCHAR(45) NOT NULL,
    FOREIGN KEY (userId) REFERENCES users (id) ON DELETE CASCADE
);
CREATE UNIQUE INDEX id_UNIQUE ON users_platform_profile (id);
CREATE INDEX user_platform_id_idx ON users_platform_profile (userId);
