# Program for a web scraper that scrapes data from fbref.com. Takes data from every premier
# league season starting in 2007/2008 and uploads it to the database in mySQL. 
#
# Scrapes for data such as season year, team name, home stadium, match data (W/L/D, GF, GA, yCards, rCards, etc.),
# and season data(total wins, losses, draws, GF, GA, etc.)

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
currentTeams = ["Manchester City", "Arsenal", "Liverpool", "Aston Villa", "Tottenham Hotspur", "Newcastle Untied", "Manchester United", "West Ham United", "Chelsea", "Brighton and Hove Albion", "Wolverhampton Wanderers", "Fulham", "Bournemouth", "Crystal Palace", "Brentford", "Everton", "Nottingham Forest", "Luton Town", "Burnley", "Sheffield United"]

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

# Get only the links since 2007-2008 season (first season called premier league)
seasonURLS = []
# List to store every season's years
seasonYrs = []
for link in seasons:
    if (link == '/en/comps/9/Premier-League-Stats'):
        if (link not in seasonURLS):
            seasonURLS.append(link)
            seasonYrs.append("2023/2024")
    elif (getSeasonYear(link) > 2007):
        if (link not in seasonURLS):
            seasonURLS.append(link)
            # Set the season years and store it
            secondYear = getSeasonYear(link)
            firstYear = secondYear - 1
            season = str(firstYear) + "/" + str(secondYear)
            seasonYrs.append(season)
    
# Set each link to have the fbref url in front
for i in range(len(seasonURLS)):
    seasonURLS[i] = "https://fbref.com" + seasonURLS[i]

# Get links to every team that played each season.
teamURLS = []
# Stores unique team names
uniqueTeams = []
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
            # Prevent duplicate links
            if (teamLink not in teamURLS):
                teamURLS.append(teamLink)
            # Add the team name to the unique list if not already in it
            name = ""
            # Check if the team is a past or current team
            if (teamLink[37:39] == "20"):
                name = getTeamName(teamLink)
            else:
                name = getCurrName(teamLink)
            if (name not in uniqueTeams):
                uniqueTeams.append(name)
    # Avoid 429 Error
    time.sleep(5)

# Retrive squad stats for each team from each season
count = 0
for season in seasonURLS:
    print(season)
    seasonHTML = requests.get(season).text
    soup = BeautifulSoup(seasonHTML, "html.parser")
    # Make sure the table is in the HTML
    if (not soup.findAll("Regular Season").isEmpty()):
        regSeason = pd.read_html(StringIO(seasonHTML), match = "Regular season")
    if (not soup.findAll("Regular Season").isEmpty()):
        squadShooting = pd.read_html(StringIO(seasonHTML), match = "Squad Shooting")
    if (not soup.findAll("Regular Season").isEmpty()):
        squadPassing = pd.read_html(StringIO(seasonHTML), match = "Squad Passing")
    if (not soup.findAll("Regular Season").isEmpty()):
        passTypes = pd.read_html(StringIO(seasonHTML), match = "Squad Pass Types")
    if (not soup.findAll("Regular Season").isEmpty()):
        miscStats = pd.read_html(StringIO(seasonHTML), match = "Squad Miscellaneous Stats")
    count += 1
    print(count)
    # Avoid 429 Error
    time.sleep(5)
    
# Retrive match data for each team in each season
# for url in teamURLS:
    