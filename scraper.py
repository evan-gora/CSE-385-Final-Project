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

# Get links to every team that played each season.
teamURLSorig = []
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
            # Prevent duplicate links
            if (teamLink not in teamURLSorig):
                teamURLSorig.append(teamLink)
            # Add the team name to the unique list if not already in it
            name = ""
            # Check if the team is a past or current team
            if (teamLink[37:39] == "20"):
                name = getTeamName(teamLink)
            else:
                name = getCurrName(teamLink)
            if (name not in uniqueTeams):
                uniqueTeams.append(name)
            print("Added Link for " + name)
    # Avoid 429 Error (Too Many Requests)
    time.sleep(5)
print("Team URLS Generated")
# Clean the teamURLs to make sure there are no duplicates
teamURLS = [teamURLS.append(link) for link in teamURLSorig if link not in teamURLS]
print("Cleaned teamURLS")

# Retrive squad stats for each team from each season
for season in seasonURLS:
    seasonHTML = requests.get(season).text
    soup = BeautifulSoup(seasonHTML, "html.parser")
    # Generate tables for the necessary data
    regSeason = pd.read_html(StringIO(seasonHTML), match = "Regular season")
    squadShooting = pd.read_html(StringIO(seasonHTML), match = "Squad Shooting")
    miscStats = pd.read_html(StringIO(seasonHTML), match = "Squad Miscellaneous Stats")
    # Use try catch for passing tables because some seasons do not have passing data
    try:
        squadPassing = pd.read_html(StringIO(seasonHTML), match = "Squad Passing")
    except:
        continue
    try:
        passTypes = pd.read_html(StringIO(seasonHTML), match = "Squad Pass Types")
    except:
        continue
    
    # TODO: MERGE TABLES AND ADD RELEVANT INFORMATION TO THE DATABASE
    # Avoid 429 Error (Too Many Requests)
    time.sleep(5)
print("Season Stats Completed")

# Retrive match data for each team in each season
for url in teamURLS:
    # Get the season years and team name
    endYear = getSeasonYear(url)
    season = str(endYear - 1) + "-" + str(endYear)
    team = getTeamName(url)
    teamHTML = requests.get(url).text
    soup = BeautifulSoup(teamHTML, "html.parser")
    # Get the scores of each game played
    matches = pd.read_html(StringIO(teamHTML), match = "Scores & Fixtures")
    # Generate shooting, passing, possession, and misc HTML pages
    # Some pages do not have passing or possession data
    links = soup.findAll("a")
    links = [link.get("href") for link in links]
    shootingLinks = [link for link in links if link and "all_comps/shooting/" in link]
    passingLinks = [link for link in links if link and "all_comps/passing_types/" in link]
    possLinks = [link for link in links if link and "all_comps/possession/" in link]
    miscLinks = [link for link in links if link and "all_comps/misc/" in link]
    shootingURL = "https://fbref.com" + shootingLinks[0]
    shootingHTML = requests.get(shootingURL).text
    miscURL = "https://fbref.com" + miscLinks[0]
    print(miscURL)
    miscHTML = requests.get(shootingURL).text
    shooting = pd.read_html(StringIO(shootingHTML), match = "Shooting")
    print("Shooting table created")
    # String for the match section of the misc table
    check = season + " " + team + ": All Competitions"
    misc = pd.read_html(StringIO(miscHTML), match = check)
    print(misc)
    # Try to get passing and possession data
    try:
        passingURL = "https://fbref.com" + passingLinks[0]
        passingHTML = requests.get(shootingURL).text
        passing = pd.read_html(StringIO(passingHTML), match = "Pass Types")
    except:
        if(getSeasonYear(url) == 2024):
            print("Passing data for " + getCurrName(url) + " for " + str(getSeasonYear(url) - 1) + "/" + str(getSeasonYear(url)) + " not found.")
        else :
            print("Passing data for " + getTeamName(url) + " for " + str(getSeasonYear(url) - 1) + "/" + str(getSeasonYear(url)) + " not found.")
    try:
        possURL = "https://fbref.com" + possLinks[0]
        possHTML = requests.get(shootingURL).text
        possession = pd.read_html(StringIO(possHTML), match = "Possession")
    except:
        if(getSeasonYear(url) == 2024):
            print("Possession data for " + getCurrName(url) + " for " + str(getSeasonYear(url) - 1) + "/" + str(getSeasonYear(url)) + " not found.")
        else :
            print("Possession data for " + getTeamName(url) + " for " + str(getSeasonYear(url) - 1) + "/" + str(getSeasonYear(url)) + " not found.")
    break
    # Avoid 429 Error (Too Many Requests)
    time.sleep(1)
    