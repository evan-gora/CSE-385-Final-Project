USE premdata;

CREATE TABLE teams (
	teamID INT NOT NULL AUTO_INCREMENT,
    teamName VARCHAR(100),
    
    PRIMARY KEY (teamID)
);

CREATE TABLE seasons (
	seasonID INT NOT NULL AUTO_INCREMENT,
    season VARCHAR(10),
    leagueWinner VARCHAR(100),
    winnerID INT,
    
    PRIMARY KEY (seasonID),
    FOREIGN KEY(winnerID)
		REFERENCES teams(teamID)
);

CREATE TABLE matches (
	matchID INT NOT NULL AUTO_INCREMENT,
	homeName VARCHAR(100),
    homeID INT NOT NULL,
    awayName VARCHAR(100),
    awayID INT NOT NULL,
    location VARCHAR(200),
    homeGoals INT NOT NULL,
    awayGoals INT NOT NULL,
    homeShots INT,
    awayShots INT,
    homeTouches INT,
    awayTouches INT,
    homeCorners INT,
    awayCorners INT,
    homeYellows INT,
    awayYellows INT,
    homeReds INT,
    awayReds INT,
    winnerName VARCHAR(100),
    winnerID INT NOT NULL,
    matchDay DATE,
    season VARCHAR(10),
    seasonID INT NOT NULL,
    
    PRIMARY KEY(matchID),
    FOREIGN KEY(homeID)
		REFERENCES teams(teamID),
	FOREIGN KEY(awayID)
		REFERENCES teams(teamID),
	FOREIGN KEY(winnerID)
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
    wins INT,
    losses INT,
    goals INT,
    yellowCards INT,
    redCards INT,
    shots INT,
    shotsOT INT,
    penGoals INT,
    fkGoals INT,
    cleanSheets INT,
    goalsConceded INT,
    clearances INT,
    ownGoals INT,
    pensConceded INT,
    penGoalsConceded INT,
    fouls INT,
    passes INT,
    corners INT,
    
    PRIMARY KEY (statID),
    FOREIGN KEY (seasonID)
		REFERENCES seasons(seasonID),
	FOREIGN KEY (teamID)
		REFERENCES teams(teamID)
);