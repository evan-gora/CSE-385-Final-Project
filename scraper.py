# Program for a web scraper that scrapes data from fbref.com. Takes data from every premier
# league season starting in 1992 and uploads it to a locally hosted mySQL database. 
#
# Scrapes for data such as season year, team name, home stadium, match data (W/L/D, GF, GA, yCards, rCards, etc.),
# and season data(total wins, losses, draws, GF, GA, etc.)
#
# Not optimized
#
# Author: Evan Gora

from io import StringIO
from urllib.request import urlopen
from bs4 import BeautifulSoup
import time
import pandas as pd
from pip._vendor import requests
from sqlalchemy import create_engine
import mysql.connector 

# A list of all current premier league teams.
# Temporary fix to problems with getting team links for current season.
CURRENT_TEAMS = ["Manchester City", "Arsenal", "Liverpool", "Aston Villa", "Tottenham Hotspur", 
                "Newcastle Untied", "Manchester United", "West Ham United", "Chelsea", "Brighton and Hove Albion", 
                "Wolverhampton Wanderers", "Fulham", "Bournemouth", "Crystal Palace", "Brentford", "Everton", 
                "Nottingham Forest", "Luton Town", "Burnley", "Sheffield United"]
# Current season
CURRENT_SEASON = "2023/2024"

# Define the MySQL connector
connection = mysql.connector.connect(user = "root", password = "m1923!Ac", host = "localhost", database = "premdata")
cursor = connection.cursor()
engine = create_engine("mysql+mysqlconnector://root:m1923!Ac@localhost/premdata")

# A method that gets the second year of the season (ex. 2007-2008 will return 2008)
def getSeasonYear(link, indx1, indx2):
    year = link[indx1:indx2]
    return int(year)

# A method to get the name of each team. Used for creating a list of unique teams that
# have played a premier league game.
def getTeamName(link):
    team = link[47:]
    # Remove "-Stats" from the end of the link and replace each - with a space
    team = team.replace("-Stats", "").replace("-", " ")
    return team

# Method to return the name of a team currently playing in the league
def getCurrName(link):
    team = link[37:]
    # Remove "-Stats" from the end of the link and replace each - with a space
    team = team.replace("-Stats", "").replace("-", " ")
    return team

# Open the url with all the premier league seasons
seasonsURL = "https://fbref.com/en/comps/9/history/Premier-League-Seasons"
seasonsHTML = urlopen(seasonsURL)
soup = BeautifulSoup(seasonsHTML, "html.parser")

# Get the links to each premier league season
seasons = soup.findAll("a")
seasons = [link.get("href") for link in seasons]
# Make sure all links are of type string
seasons = [link for link in seasons if type(link) == str]
# Filter the links so that the list contains only links with premier league seasons.
seasons = [link for link in seasons if '/en/comps/9/' in link and 'Premier-League-Stats' in link]

print("Generating Season Links:")
# Get only the links since 2007-2008 season (first season called premier league)
seasonURLS = []
# List to store every season's years
seasonYrs = []
for link in seasons:
    if (link == '/en/comps/9/Premier-League-Stats'):
        if (link not in seasonURLS):
            seasonURLS.append(link)
            seasonYrs.append(CURRENT_SEASON)
            print("Link for 2023/2024 Generated")
    elif (getSeasonYear(link, 17, 21) > 1992):
        if (link not in seasonURLS):
            seasonURLS.append(link)
            # Set the season years and store it
            secondYear = getSeasonYear(link, 17, 21)
            firstYear = secondYear - 1
            season = str(firstYear) + "/" + str(secondYear)
            seasonYrs.append(season)
            print("Link for " + season + " Generated")
print("Season links generated successfully")

# Add the season years to the database
print("Adding seasons into SQL database")
for year in seasonYrs:
    sql = "INSERT INTO seasons (season) VALUES (%s)"
    cursor.execute(sql, (year,))
    connection.commit()
print("Seasons added to database")

# Set each link to have the fbref url in front
for i in range(len(seasonURLS)):
    seasonURLS[i] = "https://fbref.com" + seasonURLS[i]

# Array to store each unique team
uniqueTeams = []
#seasonYrs = []
# Retrive squad stats for each team from each season
# Also generate match data from each season
for season in seasonURLS:
    # Boolean used for checking if it is a season missing some data
    missing = False
    seasonHTML = requests.get(season).text
    soup = BeautifulSoup(seasonHTML, "html.parser")
    print(season)
    
    # Get the season year
    try:
        seasonStart = getSeasonYear(season, 29, 33)
        seasonEnd = seasonStart + 1
        seasonYr = str(seasonStart) + "/" + str(seasonEnd)
    except:
        seasonYr = CURRENT_SEASON
    #seasonYrs.append(seasonYr)
    
    # Generate tables for the necessary data
    print("Generating Season Stats")
    regSeason = pd.read_html(StringIO(seasonHTML), match = "Regular season Table")
    regSeason = regSeason[0]
    squadShooting = pd.read_html(StringIO(seasonHTML), match = "Squad Shooting")
    squadShooting = squadShooting[0]
    miscStats = pd.read_html(StringIO(seasonHTML), match = "Squad Miscellaneous Stats")
    miscStats = miscStats[0]
    
    # Use try catch for passing tables because some seasons do not have passing data
    try:
        squadPassing = pd.read_html(StringIO(seasonHTML), match = "Squad Passing")
        squadPassing = squadPassing[0]
        passingAtt = squadPassing[['Total']]
        passingAtt.columns = passingAtt.columns.droplevel(0)
        passingAtt = passingAtt[['Cmp', 'Att', 'Cmp%']]
    except:
        print("No Squad Passing Data for this Season")
        missing = True
    try:
        passTypes = pd.read_html(StringIO(seasonHTML), match = "Squad Pass Types")
        passTypes = passTypes[0]
        passTypesFinal = passTypes[['Pass Types']]
        passTypesFinal.columns = passTypesFinal.columns.droplevel(0)
        passTypesFinal = passTypesFinal[['CK']]
    except:
        print("No Squad Pass Types Data for this Season")
    
    # Filter the regSeason data
    regSeason = regSeason[['Squad', 'W', 'D', 'L', 'GF', 'GA', 'Pts']]
    # Drop the first level of shooting and misc data
    squadShooting = squadShooting[['Standard']]
    squadShooting.columns = squadShooting.columns.droplevel(0)
    miscStats = miscStats[['Performance']]
    miscStats.columns = miscStats.columns.droplevel(0)
    
    # Check if the season is missing data before filtering
    if (missing):
        squadShooting = squadShooting[['SoT', 'PK']]
        miscStats = miscStats[['CrdY', 'CrdR', 'Fls']]
    else:
        squadShooting = squadShooting[['Sh', 'SoT', 'FK', 'PK']]
        miscStats = miscStats[['CrdY', 'CrdR', 'Fls', 'PKcon', 'OG']]
    
    # Check if the season has passing data before joining passing tables
    if (not missing):
        passingFinal = passingAtt.join(passTypesFinal)
        
    # Join the rest of the data
    seasonStats = regSeason.join(squadShooting)
    if (not missing):
        seasonStats = seasonStats.join(passingFinal)
    seasonStats = seasonStats.join(miscStats)
    
    # Populate array of length len(seasonStats) with the current season so that the seasons column can be populated
    seasonStatsYrs = []
    seasonIDs = []
    for i in range(0, len(seasonStats)):
        seasonStatsYrs.append(seasonYr)
        seasonIDs.append(seasonYrs.index(seasonYr) + 1)
    # Add the column to the table
    seasonStats.insert(0, "season", seasonStatsYrs, True)
    seasonStats.insert(1, "statseasonID", seasonIDs, True)
    
    # Rename columns to match database
    if (missing):
        seasonStats = seasonStats.rename(columns = {'Squad': 'teamName', 'Pts': 'points', 'W': 'wins', 'L': 'losses', 
                                                    'D': 'draws', 'GF': 'goals', 'SoT': 'shotsOT', 'PK': 'penGoals',
                                                    'GA': 'goalsConceded', 'Fls': 'fouls', 'CrdY': 'yellowCards',
                                                    'CrdR': 'redCards'})
    else:
        seasonStats = seasonStats.rename(columns = {'Squad': 'teamName', 'Pts': 'points', 'W': 'wins', 'L': 'losses', 'D': 'draws', 
                                                    'GF': 'goals', 'Sh': 'shots', 'SoT': 'shotsOT', 'PK': 'penGoals', 'FK': 'fkShots',
                                                    'Cmp': 'passesCompleted', 'Att': 'passesAttempted', 'Cmp%': 'passCompPerc', 
                                                    'CK': 'corners', 'GA': 'goalsConceded', 'OG': 'ownGoals', 'PKcon': 'pensConceded',
                                                    'Fls': 'fouls', 'CrdY': 'yellowCards', 'CrdR': 'redCards'})
    print("Season Stats Completed")
    
    # Find the unique teams from each season
    # Also find the ID for the the team
    print("Finding unique teams for this season and assigning IDs")
    teamIDs = []
    teams = seasonStats['teamName'].values
    for team in teams:
        if (team not in uniqueTeams):
            uniqueTeams.append(team)
            print("Added " + team + " to list of unique teams")
            sql = "INSERT INTO teams(teamName) VALUES (%s)"
            cursor.execute(sql, (team,))
            connection.commit()
            print("Added " + team + " to mySQL database")
        indx = uniqueTeams.index(team) + 1
        for i in range(0, len(seasonStats)):
            if (teams[i] == uniqueTeams[indx - 1]):
                teamIDs.append(indx)
    # Add the teamIDs to the table
    seasonStats.insert(3, "teamID", teamIDs, False)
    
    # Add seasonStats table to database
    print("Adding season stats to database")
    seasonStats.to_sql("seasonstats", con = engine, if_exists = 'append', index = False)
    print("Season stats added to database")
    
    
    # Match Data for each season
    print("Generating match data")
    matches = soup.findAll("a")
    matches = [link.get("href") for link in matches]
    # Make sure all links are string
    matches = [link for link in matches if type(link) == str]
    # Filter the links so that the list contains only links with premier league seasons.
    matches = [link for link in matches if '/en/comps/9/' in link and '/schedule/' in link]
    
    # Get season match data
    matchURL = "https://fbref.com" + matches[0]
    matchHTML = requests.get(matchURL).text
    soup = BeautifulSoup(matchHTML, "html.parser")
    print(matchURL)
    # Read the match data
    data = pd.read_html(StringIO(matchHTML), match = "Scores & Fixtures")
    data = data[0]
    
    # Drop unnecessary columns from match data
    data = data.drop(columns = ['Wk', 'Day', 'Time', 'Referee', 'Match Report', 'Notes'], axis = 1)
    
    # Add season years and season IDs to the table
    matchSeason = []
    seasonIDs = []
    for i in range(0, len(data)):
        matchSeason.append(seasonYr)
        seasonIDs.append(seasonYrs.index(seasonYr) + 1)
    # Add the column to the table
    data.insert(0, "season", matchSeason, True)
    data.insert(1, "matchseasonID", seasonIDs, True)
    
    # Add home and away team IDs
    homeIDs = []
    awayIDs = []
    homeTeams = data["Home"].values
    awayTeams = data["Away"].values
    for i in range(0, len(data)):
        # Make sure the match has teams
        if (homeTeams[i] in uniqueTeams):
            homeID = uniqueTeams.index(homeTeams[i]) + 1
            homeIDs.append(homeID)
            awayID = uniqueTeams.index(awayTeams[i]) + 1
            awayIDs.append(awayID)
        else:
            homeIDs.append(None)
            awayIDs.append(None)
    # Add the columns to the table
    data.insert(4, "homeID", homeIDs, True)
    data.insert(9, "awayID", awayIDs, True)
    
    # Filter the score into home and away goals and add to their own columns. 
    homeGoals = []
    awayGoals = []
    scores = data["Score"].values
    for score in scores:
        # Make sure the score is not float (nan)
        if (not isinstance(score, float)):
            # Split the score by the '-' character. Gives an array containing home goals and away goals
            # Website uses a special character, not the common '-' character.
            matchGoals = score.split('–')
            # Append to the respective arrays
            homeGoals.append(int(matchGoals[0]))
            awayGoals.append(int(matchGoals[1]))
        else:
            homeGoals.append(None)
            awayGoals.append(None)
    # Add the goals columns
    data.insert(7, "homeGoals", homeGoals, True)
    data.insert(9, "awayGoals", awayGoals, True)
    # Drop the score column
    data = data.drop(columns=["Score"], axis = 1)
    
    # Rename columns to match the database
    # Check for missing data
    if (missing):
        data = data.rename(columns = {'Date': 'matchDay', 'Home': 'homeName', 'Away': 'awayName', 
                                      'Attendance': 'attendance', 'Venue': 'location'})
    else:
        data = data.rename(columns = {'Date': 'matchDay', 'Home': 'homeName', 'xG': 'homeXG', 'xG.1': 'awayXG', 
                                      'Away': 'awayName', 'Attendance': 'attendance', 'Venue': 'location'})
    print("Match Data Completed")
    
    # Add match data to the database
    print("Adding match data to the database")
    data.to_sql("matches", con = engine, if_exists = 'append', index = False)
    print("Match data added to databse")
    
    if (seasonYr == CURRENT_SEASON):
        # Add to the testStandings table
        testStandings = seasonStats[['teamName', 'teamID', 'points', 'wins', 'losses', 'draws', 'goals', 'goalsConceded']]
        testStandings = testStandings.rename(columns = {'teamID' : 'testteamID'})
        testStandings.to_sql("testStandings", con = engine, if_exists = 'append', index = False)
        # Add to the upcoming matches table
        data = data.rename(columns = {'matchseasonID' : 'upcomingseasonID', 'homeID' : 'upcominghomeID', 'awayID' : 'upcomingawayID'})
        data = data.drop(columns = ['homeXG', 'awayXG', 'attendance'])
        data.to_sql("upcomingMatches", con = engine, if_exists = 'append', index = False)
    
    # Avoid 429 Error (Too Many Requests)
    time.sleep(3) 
    

# Close the connection
print("Closing database connection")
connection.close()