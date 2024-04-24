-- Query to create the schema used for the project. Contains data scraped from the web and can be accessed by a user with a GUI.

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
    matchDay DATE,
    season VARCHAR(10),
    seasonID INT NOT NULL,
	homeName VARCHAR(100),
    homeID INT NOT NULL,
    awayName VARCHAR(100),
    awayID INT NOT NULL,
	homeGoals INT NOT NULL,
    homeXG DECIMAL(2, 1),
    awayGoals INT NOT NULL,
    awayXG DECIMAL(2, 1),
    attendance DECIMAL(6, 1),
    location VARCHAR(50),
    
    PRIMARY KEY(matchID),
    FOREIGN KEY(homeID)
		REFERENCES teams(teamID),
	FOREIGN KEY(awayID)
		REFERENCES teams(teamID),
	FOREIGN KEY(seasonID)
		REFERENCES seasons(seasonID)
);

CREATE TABLE seasonstats (
	statID INT NOT NULL AUTO_INCREMENT,
    seasonID INT NOT NULL,
    season VARCHAR(10),
    teamID INT NOT NULL,
    teamName VARCHAR(100),
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
    passCompPerc DECIMAL(2, 1),
    corners INT,
    goalsConceded INT,
    ownGoals INT,
    pensConceded INT,
    fouls INT,
    yellowCards INT,
    redCards INT,
    
    PRIMARY KEY (statID),
    FOREIGN KEY (seasonID)
		REFERENCES seasons(seasonID),
	FOREIGN KEY (teamID)
		REFERENCES teams(teamID)
);