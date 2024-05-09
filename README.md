# About the Project

This repository contains all files for my CSE 385 - Database Systems - final project. The requirements for this project are as follows: Use a database, either downloaded or
self-generated, and create a GUI to allow a user to view, filter, and modify the data.

# The data

For the data itself, I was unable to find a good Premier League dataset, so I decided to create my own. I wrote SQL queries to create the schema, the tables, and update foreign keys
and then created a web scraper to gather data to fill the tables. In this case, I have tables for each match in each season, each team's stats in each season, all upcoming matches,
and a copy of the current team stats. I also created tables assigned each team an ID and each season an ID, which makes cross-referencing tables easier when updating.

# The GUI

For data viewing, I simply use the matches table and seasonstats table and allow the user to filter by different teams and seasons. For the data editing portion of the project, I use
the upcoming matches table and copy of the current season standings. There is a button that takes the user to upcoming matches and displayed both all upcoming matches as well as the
current season table. When the user tests different scores in the upcoming matches table and presses the "Save Changes" button, the current season table is updated to display how the
result of the game affects the table. The user also has the ability to reset the tables to set all scores back to 0-0 and set the table back to how it currently stands.

# Languages and Technologies

The scraper is written in python, with the GUI itself being written using Java Swing graphics. The database queries and database are stored locally in mySQL workbench.
