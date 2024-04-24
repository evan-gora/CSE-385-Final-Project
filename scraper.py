# Program for a web scraper that scrapes data from fbref.com. Takes data from every premier
# league season starting in 2007/2008 and uploads it to a locally hosted mySQL database. 
#
# Scrapes for data such as season year, team name, home stadium, match data (W/L/D, GF, GA, yCards, rCards, etc.),
# and season data(total wins, losses, draws, GF, GA, etc.)
#
# Author: Evan Gora

from io import StringIO
from urllib.request import urlopen
from bs4 import BeautifulSoup
import time
import pandas as pd
from pip._vendor import requests
from IPython.display import display, HTML
import mysql.connector 

# A method that gets the second year of the season (ex. 2007-2008 will return 2008)
def getSeasonYear(link):
    year = link[17:21]
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

# A list of all current premier league teams.
# Temporary fix to problems with getting team links for current season.
currentTeams = ["Manchester City", "Arsenal", "Liverpool", "Aston Villa", "Tottenham Hotspur", 
                "Newcastle Untied", "Manchester United", "West Ham United", "Chelsea", "Brighton and Hove Albion", 
                "Wolverhampton Wanderers", "Fulham", "Bournemouth", "Crystal Palace", "Brentford", "Everton", 
                "Nottingham Forest", "Luton Town", "Burnley", "Sheffield United"]

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
            seasonYrs.append("2023/2024")
            print("Link for 2023/2024 Generated")
    elif (getSeasonYear(link) > 2007):
        if (link not in seasonURLS):
            seasonURLS.append(link)
            # Set the season years and store it
            secondYear = getSeasonYear(link)
            firstYear = secondYear - 1
            season = str(firstYear) + "/" + str(secondYear)
            seasonYrs.append(season)
            print("Link for " + season + " Generated")
    
# Set each link to have the fbref url in front
for i in range(len(seasonURLS)):
    seasonURLS[i] = "https://fbref.com" + seasonURLS[i]


# Stores unique team names
uniqueTeams = []
print("Generating URLs for Each Team in Each Season:")
for seasonURL in seasonURLS:
    # Open the link and parse the HTML
    seasonHTML = urlopen(seasonURL)
    soup = BeautifulSoup(seasonHTML, "html.parser")
    # Get each team link for the specific season
    teams = soup.findAll("a")
    teams = [link.get("href") for link in teams]
    # Make sure all links are of type string
    teams = [link for link in teams if type(link) == str]
    # Get the links for each team
    teams = [link for link in teams if '/squads/' in link]
    for teamLink in teams:
        teamLink = "https://fbref.com" + teamLink
        if (teamLink[37:39] == "20" or getCurrName(teamLink) in currentTeams):
            # Add the team name to the unique list if not already in it
            name = ""
            # Check if the team is a past or current team
            if (teamLink[37:39] == "20"):
                name = getTeamName(teamLink)
            else:
                name = getCurrName(teamLink)
            if (name not in uniqueTeams):
                uniqueTeams.append(name)
                print("Added " + name + " to list of unique teams")
    # Avoid 429 Error (Too Many Requests)
    time.sleep(5)
print("List of Unique Teams Generated")

# Retrive squad stats for each team from each season
# Also generate match data from each season
for season in seasonURLS:
    # Boolean used for merging passing data
    passing = False
    seasonHTML = requests.get(season).text
    soup = BeautifulSoup(seasonHTML, "html.parser")
    print(season)
    
    # Generate tables for the necessary data
    regSeason = pd.read_html(StringIO(seasonHTML), match = "Squad Standard Stats")
    squadShooting = pd.read_html(StringIO(seasonHTML), match = "Squad Shooting")
    miscStats = pd.read_html(StringIO(seasonHTML), match = "Squad Miscellaneous Stats")
    
    # Use try catch for passing tables because some seasons do not have passing data
    try:
        squadPassing = pd.read_html(StringIO(seasonHTML), match = "Squad Passing")
        passingAtt = squadPassing[['Cmp', 'Att', 'Cmp%']]
        passing = True
    except:
        print("No Squad Passing Data for this Season")
    try:
        passTypes = pd.read_html(StringIO(seasonHTML), match = "Squad Pass Types")
        passTypesFinal = passTypes[['CK']]
    except:
        print("No Squad Pass Types Data for this Season")
        
    print("Season Stats Completed")
    
    # TODO: MERGE TABLES AND ADD RELEVANT INFORMATION TO THE DATABASE
    # Check if the season has passing data before merging passing tables
    if (passing):
        passingFinal = passingAtt.merge(passTypesFinal)
        print(passingFinal)
    
    # Match Data for each season
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
        
    data = pd.read_html(StringIO(matchHTML), match = "Scores & Fixtures")
    print(data)
    print("Match Data Completed")
    
    # TODO - Filter data and add to database
    # Drop unnecessary columns from match data
    data = data.drop(columns = ['Wk', 'Day', 'Time', 'Referee', 'Match Report', 'Notes'])
    
    break
    # Avoid 429 Error (Too Many Requests)
    time.sleep(5)