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
-- Clear all matches that have all null values
DELETE FROM matches
	WHERE homeID IS NULL;
