from urllib.request import urlopen
from bs4 import BeautifulSoup
import mysql.connector 

# Connect to the database
connection = mysql.connector.connect(
    user = 'root', password = 'm1923!Ac', host = 'localhost', database = 'premdata'
)
cursor = connection.cursor()

sql = "INESERT INTO `match` (`homeName`, `homeID`, `awayName`, `awayID`, `homeGoals`, `awayGoals`, `winnerName`, `winnerID`) VALUES (%s, %s, %s, %s, %s, %s, %s, %s)"
