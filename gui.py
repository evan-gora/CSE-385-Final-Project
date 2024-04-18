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
mainLayout = QVBoxLayout()

### DEFINE NECESSARY WIDGETS ###

# Possibly form layout with filter options on the left and results on the right.
# Have filters for team name and season - Allow user to select multple teams or seasons?

# Could also do a VBox with text fields for a user to enter two team names and find games they have played against each other. 
# Then under the VBox, can add a form layout with filters on the left and results on the right
# ^ This would probably be more difficult.

# Could do dropdown boxes for the filters so that a user can only select one team and/or season at a time.
# Would be easier than having user be able to select multiple teams/seasons

# Should default to showing current season data?

# Could use a dropdown to select a team but allow the user to select multiple seasons.
# Also need a confirm button to confirm when filters are set.

### WIDGET FUNCTIONALITY ###

### ADD WIDGETS TO LAYOUTS ###

# Set the main layout and show the window
window.setLayout(mainLayout)
window.show()
app.exec()