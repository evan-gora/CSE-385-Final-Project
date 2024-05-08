-- Query to create the schema used for the project. Contains data scraped from the web and can be accessed by a user with a GUI.

DROP DATABASE premdata;
CREATE SCHEMA premdata;
USE premdata;

CREATE TABLE teams (
	teamID INT NOT NULL AUTO_INCREMENT,
    teamName VARCHAR(100),
    
    PRIMARY KEY (teamID)
);

CREATE TABLE seasons (
	seasonID INT NOT NULL AUTO_INCREMENT,
    season VARCHAR(10),
    
    PRIMARY KEY (seasonID)
);

CREATE TABLE matches (
	matchID INT NOT NULL AUTO_INCREMENT,
    season VARCHAR(10),
    matchseasonID INT,
    matchDay DATE,
	homeName VARCHAR(100),
    homeID INT,
    homeXG DECIMAL(2, 1),
    homeGoals INT,
    awayGoals INT,
    awayXG DECIMAL(2, 1),
    awayName VARCHAR(100),
    awayID INT,
    attendance DECIMAL(6, 1),
    location VARCHAR(50),
    
    PRIMARY KEY(matchID)
);

CREATE TABLE upcomingMatches (
	upcomingID INT NOT NULL AUTO_INCREMENT,
    season VARCHAR(10),
    upcomingseasonID INT,
    matchDay DATE,
    homeName VARCHAR(100),
    upcominghomeID INT,
    homeGoals INT,
    awayGoals INT,
    awayName VARCHAR(100),
    upcomingawayID INT,
    location VARCHAR(50),
    
    PRIMARY KEY(upcomingID)
);

CREATE TABLE seasonstats (
	statID INT NOT NULL AUTO_INCREMENT,
    season VARCHAR(10),
    statseasonID INT,
    teamName VARCHAR(100),
    teamID INT,
    points INT,
    wins INT,
    losses INT,
    draws INT,
    goals INT,
    shots INT,
    shotsOT INT,
    penGoals INT,
    fkShots INT,
    passesCompleted INT,
    passesAttempted INT,
    passCompPerc DECIMAL(3, 1),
    corners INT,
    goalsConceded INT,
    ownGoals INT,
    pensConceded INT,
    fouls INT,
    yellowCards INT,
    redCards INT,
    
    PRIMARY KEY (statID)
);

CREATE TABLE testStandings (
	standingID INT NOT NULL AUTO_INCREMENT,
    teamName VARCHAR(50),
    testteamID INT,
    points INT,
    wins INT,
    losses INT,
    draws INT,
    goals INT,
    goalsConceded INT,
    
    PRIMARY KEY (standingID)
);