-- Run after running the scraper program in order to populate the foreign key positions

USE premdata;

-- Update foreign keys in the season stats table
INSERT INTO seasonstats(seasonID) VALUES ((SELECT seasonID FROM seasons WHERE seasons.season = seasonstats.season));
INSERT INTO seasonstats(teamID) VALUES ((SELECT teamID FROM teams WHERE teams.teamName = seasonstats.teamName));
-- Update foreign keys in the matches table
INSERT INTO matches(seasonID) VALUES ((SELECT seasonID FROM seasons WHERE seasons.season = matches.season));
INSERT INTO matches(homeID) VALUES ((SELECT teamID FROM teams WHERE teams.teamName = matches.homeName));
INSERT INTO matches(awayID) VALUES ((SELECT teamID FROM teams WHERE teams.teamName = matches.awayName));