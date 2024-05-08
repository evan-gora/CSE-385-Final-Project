-- Run after running the scraper program in order to populate the foreign key positions

USE premdata;

-- Update foreign keys in the season stats table
ALTER TABLE seasonstats
	ADD CONSTRAINT fk_seasonstat_id FOREIGN KEY (statseasonID) 
    REFERENCES seasons(seasonID),
    ADD CONSTRAINT fk_team_id FOREIGN KEY (teamID)
    REFERENCES teams(teamID);
-- Update foreign keys in the matches table
ALTER TABLE matches
	ADD CONSTRAINT fk_matchseason_id FOREIGN KEY (matchseasonID) 
    REFERENCES seasons(seasonID),
    ADD CONSTRAINT fk_home_id FOREIGN KEY (homeID) 
    REFERENCES teams(teamID),
    ADD CONSTRAINT fk_away_id FOREIGN KEY (awayID)
    REFERENCES teams(teamID);
-- Update foreign keys in the upcoming matches table
ALTER TABLE upcomingMatches
	ADD CONSTRAINT fk_upcomingseason_id FOREIGN KEY (upcomingseasonID)
    REFERENCES seasons(seasonID),
    ADD CONSTRAINT fk_upcominghome_id FOREIGN KEY (upcominghomeID)
    REFERENCES teams(teamID),
    ADD CONSTRAINT fk_upcomingaway_id FOREIGN KEY (upcomingawayID)
    REFERENCES teams(teamID);
-- Update foreign key for team in testStandings table
ALTER TABLE testStandings
	ADD CONSTRAINT fk_testteam_id FOREIGN KEY (testteamID)
    REFERENCES teams(teamID);
-- Clear all matches that have all null values and all upcoming matches
DELETE FROM matches
	WHERE homeID IS NULL
    AND homeGoals IS NULL;
-- Clear all non-upcoming matches from the upcoming table
DELETE FROM upcomingMatches
	WHERE homeGoals IS NOT NULL
    AND upcominghomeID IS NOT NULL;
-- Clear all matches that have all null values
DELETE FROM upcomingMatches
	WHERE upcominghomeID IS NULL;
