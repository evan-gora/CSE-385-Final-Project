# Program that creates a simple GUI for a user to access data gathered by the scraper.
# Uses embedded SQL programming with a PyQt5 GUI to create the functionality.
#
# Author: Evan Gora

from PyQt5.QtWidgets import *
import mysql.connector

# Define functional methods.

# Create the application, define a window, and define the main layout.
app = QApplication([])
window = QWidget()
mainLayout = QGridLayout()

## DEFINE NESTED LAYOUTS ##

# Layout to allow user to select 2 teams and a season to see matches played between them in that season
matchSelectionLayout = QFormLayout()
# Layout to allow user to select a team and a season to see the teams stats in the selected season
teamSeasonLayout = QFormLayout()

# Layout to display results
resultsLayout = QVBoxLayout()

### DEFINE NECESSARY WIDGETS ###

# Label to let the user know what these do
matchSelectLabel = QLabel("View Matches by Teams and Seasons")

# Combo Boxes and Confirm Button for match selection
teamCombo1 = QComboBox()
teamCombo2 = QComboBox()
# TODO: Populate with teams
# Combo Box to select a season (or all seasons)
seasonCombo = QComboBox()
# TODO: populate with seasons
confirmBtn1 = QPushButton("Confirm")
# Label to differentiate the 2 filters
diffLabel = QLabel("OR")

# Label to tell the user this shows team stats
teamSeasonLabel = QLabel("Team Stats by Season")
# Combo Boxes for team and season selection
teamSelect = QComboBox()
seasonSelect = QComboBox()
confirmBtn2 = QPushButton("Confirm")

# Clear button to clear all filters
clearBtn = QPushButton("Clear")

# Label to test results layout
resultsLabel = QLabel("Results")

### WIDGET FUNCTIONALITY ###


### ADD WIDGETS TO LAYOUTS ###

# Add to the match selection layout
matchSelectionLayout.addRow("Team: ", teamCombo1)
matchSelectionLayout.addRow("Team: ", teamCombo2)
matchSelectionLayout.addRow("Season: ", seasonCombo)
matchSelectionLayout.addWidget(confirmBtn1)
matchSelectionLayout.addWidget(diffLabel)

# Add to the team/season selector
teamSeasonLayout.addRow("Team: ", teamSelect)
teamSeasonLayout.addRow("Season: ", seasonSelect)
teamSeasonLayout.addWidget(confirmBtn2)

# Add to results layout
resultsLayout.addWidget(resultsLabel)

# Add nested layouts
mainLayout.addWidget(matchSelectLabel, 0, 0)
mainLayout.addLayout(matchSelectionLayout, 1, 0)
mainLayout.addWidget(teamSeasonLabel, 2, 0)
mainLayout.addLayout(teamSeasonLayout, 3, 0)
mainLayout.addWidget(clearBtn, 4, 0)
# Add the results layout
mainLayout.addLayout(resultsLayout, 0, 1)

# Set the main layout and show the window
window.setLayout(mainLayout)
window.show()
app.exec()