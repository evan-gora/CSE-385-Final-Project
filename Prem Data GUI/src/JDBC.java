import java.awt.BorderLayout;
import java.sql.Connection;
import java.sql.Date;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/**
 * A program to contain the database connection and all necessary methods for
 * GUI functionality.
 * 
 * @author Evan Gora
 */

public class JDBC {

    // Define private attributes
    private Connection connect = null;
    private Statement statement = null;
    private ResultSet resultSet = null;
    private String[] colNames = { "Season", "Day", "Home Team", "Home Goals",
            "Away Goals", "Away Team", "Location" };
    private DefaultTableModel changedTableModel = new DefaultTableModel(
            colNames, 0);

    public JDBC() throws SQLException {
        connect = null;
        statement = null;
        resultSet = null;
    }

    /**
     * Method to connect to the locally hosted database.
     * 
     * @throws Exception
     */
    private void connect() throws Exception {
        // Define url, username and password
        String url = "jdbc:mysql://localhost/premdata";
        String username = "root";
        String password = "m1923!Ac";

        // Try to connect to the database
        try {
            connect = DriverManager.getConnection(url, username, password);
            statement = connect.createStatement();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Method to close the result set. Tries multiple times because it will not
     * always close.
     */
    public void close() {
        try {
            if (resultSet != null) {
                resultSet.close();
            }
            if (resultSet != null) {
                resultSet.close();
            }
            if (resultSet != null) {
                resultSet.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * A method to return all the teams in the database in a String array. Used
     * for displaying options in the combo boxes.
     * 
     * @return An array of all teams
     * @throws SQLException
     */
    public String[] getTeams() throws SQLException {
        // Connect to the database
        try {
            connect();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Get all team names in a result set
        resultSet = statement
                .executeQuery("SELECT * FROM premdata.teams ORDER BY teamName");

        // Convert the result set to an ArrayList
        ArrayList<String> teams = new ArrayList<String>();
        teams.add("All Teams");
        while (resultSet.next()) {
            teams.add(resultSet.getString(2));
        }

        // Convert to an array - needed to pass to combo boxes to show all teams
        String[] teamsArr = new String[teams.size()];
        teamsArr = teams.toArray(teamsArr);
        return teamsArr;
    }

    /**
     * A method to return an array containing all season years.
     * 
     * @return An array containing all seasons
     * @throws SQLException
     */
    public String[] getSeasons() throws SQLException {
        // Connect to the database
        try {
            connect();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Get all seasons in a result set
        resultSet = statement.executeQuery("SELECT * FROM premdata.seasons");

        // Convert result set to ArrayList
        ArrayList<String> seasons = new ArrayList<String>();
        seasons.add("All Seasons");
        while (resultSet.next()) {
            seasons.add(resultSet.getString(2));
        }

        // Convert to an array - needed to pass to combo boxes to show all
        // seasons.
        String[] seasonsArr = new String[seasons.size()];
        seasonsArr = seasons.toArray(seasonsArr);
        return seasonsArr;
    }

    /**
     * Helper method for the getSeasonStats method. Adds all rows from the
     * result set to the model.
     * 
     * @param model
     * @param resultSet
     * @return model
     */
    private DefaultTableModel createSeasonModel(DefaultTableModel model,
            ResultSet resultSet) throws SQLException {
        // Go through all entries in the result set
        while (resultSet.next()) {
            String season = resultSet.getString("season");
            String team = resultSet.getString("teamName");
            int points = resultSet.getInt("points");
            int wins = resultSet.getInt("wins");
            int losses = resultSet.getInt("losses");
            int draws = resultSet.getInt("draws");
            int goals = resultSet.getInt("goals");
            int shots = resultSet.getInt("shots");
            int shotsOT = resultSet.getInt("shotsOT");
            int penGoals = resultSet.getInt("penGoals");
            int fkShots = resultSet.getInt("fkShots");
            int passesComp = resultSet.getInt("passesCompleted");
            int passesAtt = resultSet.getInt("passesAttempted");
            double passPerc = resultSet.getDouble("passCompPerc");
            int corners = resultSet.getInt("corners");
            int goalsConceded = resultSet.getInt("goalsConceded");
            int ownGoals = resultSet.getInt("ownGoals");
            int pensConceded = resultSet.getInt("pensConceded");
            int fouls = resultSet.getInt("fouls");
            int yellowCards = resultSet.getInt("yellowCards");
            int redCards = resultSet.getInt("redCards");

            model.addRow(new Object[] { season, team, points, wins, losses,
                    draws, goals, shots, shotsOT, penGoals, fkShots, passesComp,
                    passesAtt, passPerc, corners, goalsConceded, ownGoals,
                    pensConceded, fouls,
                    yellowCards, redCards });
        }
        return model;
    }

    /**
     * Method to get all stats for a selected team and season (or all teams/all
     * seasons).
     * 
     * @param season
     * @param team
     * @return A table containg all stats for the selected team/season.
     * @throws SQLException
     */
    private JTable getSeasonStats(String team, String season)
            throws SQLException {
        // Connect to the database
        try {
            connect();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Create a table and table model
        JTable table = new JTable();
        String[] names = { "Season", "Team", "Points", "Wins", "Losses",
                "Draws", "Goals", "Shots", "Shots On Target", "Penalty Goals",
                "Free Kick Shots",
                "Passes Completed", "Passes Attempted", "Completion %",
                "Corners",
                "Goals Against", "Own Goals", "Penalties Coneded", "Fouls",
                "Yellow Cards", "Red Cards" };
        DefaultTableModel model = new DefaultTableModel(names, 0);

        // create the base query
        String query = "SELECT season, teamName, points, wins, losses, "
                + "draws, goals, shots, shotsOT, penGoals, fkShots, "
                + "passesCompleted, passesAttempted, passCompPerc, "
                + "corners, goalsConceded, ownGoals, pensConceded, "
                + "fouls, yellowCards, redCards FROM premdata.seasonstats";

        // Get the result set based on the filters set by the user.
        if (team.equals("All Teams") && season.equals("All Seasons")) {
            resultSet = statement.executeQuery(query);
        } else if (team.equals("All Teams")) {
            resultSet = statement.executeQuery(
                    query + " WHERE season LIKE " + "\"" + season + "\"");
        } else if (season.equals("All Seasons")) {
            resultSet = statement.executeQuery(
                    query + " WHERE teamName LIKE " + "\"" + team + "\"");
        } else {
            resultSet = statement.executeQuery(
                    query + " WHERE teamName LIKE " + "\"" + team + "\""
                            + " AND season LIKE " + "\"" + season + "\"");
        }

        // Create the model and the table
        model = createSeasonModel(model, resultSet);
        table.setModel(model);
        return table;
    }

    /**
     * A method to create a model which assists the loadMatchData method in
     * creating a table
     * 
     * @param model
     * @param resultSet
     * @return
     * @throws SQLException
     */
    private DefaultTableModel createMatchModel(DefaultTableModel model,
            ResultSet resultSet) throws SQLException {
        // Go through the whole result set
        while (resultSet.next()) {
            String season = resultSet.getString("season");
            Date day = resultSet.getDate("matchDay");
            String home = resultSet.getString("homeName");
            double homexG = resultSet.getDouble("homeXG");
            int homeGoals = resultSet.getInt("homeGoals");
            int awayGoals = resultSet.getInt("awayGoals");
            double awayxG = resultSet.getDouble("awayXG");
            String away = resultSet.getString("awayName");
            double attendance = resultSet.getDouble("attendance");
            String location = resultSet.getString("location");

            model.addRow(new Object[] { season, day, home, homexG, homeGoals,
                    awayGoals, awayxG, away, attendance, location });
        }
        return model;
    }

    /**
     * A method that gets all the match data and loads it into a table.
     * 
     * @param team1
     * @param team2
     * @param season
     * @return
     * @throws SQLException
     */
    private JTable getMatchData(String team1, String team2, String season)
            throws SQLException {
        // Connect to the database
        try {
            connect();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Create a table and a model for the table
        JTable table = new JTable();
        String[] names = { "Season", "Day", "Home Team", "Home xG",
                "Home Goals", "Away Goals", "Away xG", "Away Team",
                "Attendance", "Location" };
        DefaultTableModel model = new DefaultTableModel(names, 0);

        // create a base query
        String query = "SELECT season, matchDay, homeName, homeXG, homeGoals, "
                + "awayGoals, awayXG, awayName, attendance, location FROM "
                + "premdata.matches";

        // Check the selected filters
        if (team1.equals("All Teams") && team2.equals("All Teams")
                && season.equals("All Seasons")) {
            resultSet = statement
                    .executeQuery(query + " WHERE homeGoals IS NOT NULL");
        } else if (team1.equals("All Teams") && season.equals("All Seasons")) {
            resultSet = statement.executeQuery(
                    query + " WHERE homeName LIKE " + "\"" + team2 + "\""
                            + " OR awayName LIKE " + "\"" + team2 + "\""
                            + " AND homeGoals IS NOT NULL");
        } else if (team2.equals("All Teams") && season.equals("All Seasons")) {
            resultSet = statement.executeQuery(
                    query + " WHERE homeName LIKE " + "\"" + team1 + "\""
                            + " OR awayName LIKE " + "\"" + team1 + "\""
                            + " AND homeGoals IS NOT NULL");
        } else if (team1.equals("All Teams") && team2.equals("All Teams")) {
            resultSet = statement.executeQuery(
                    query + " WHERE season LIKE " + "\"" + season + "\""
                            + " AND homeGoals IS NOT NULL");
        } else if (season.equals("All Seasons")) {
            resultSet = statement.executeQuery(query + " WHERE (homeName LIKE "
                    + "\"" + team1 + "\"" + " OR homeName LIKE " + "\"" + team2
                    + "\"" + ") AND (awayName LIKE " + "\"" + team1 + "\""
                    + " OR awayName LIKE " + "\"" + team2 + "\"" + ")"
                    + " AND homeGoals IS NOT NULL");
        } else {
            resultSet = statement.executeQuery(query + " WHERE (homeName LIKE "
                    + "\"" + team1 + "\"" + " OR homeName LIKE " + "\"" + team2
                    + "\"" + ") AND (awayName LIKE " + "\"" + team1 + "\""
                    + " OR awayName LIKE " + "\"" + team2 + "\""
                    + ") AND season LIKE " + "\"" + season + "\""
                    + " AND homeGoals IS NOT NULL");
        }

        // Create the model and table
        model = createMatchModel(model, resultSet);
        table.setModel(model);
        return table;
    }

    /**
     * A method to create a model that uses data from the upcomingMatches table
     * in the database.
     * 
     * @param model
     * @param resultSet
     * @return
     * @throws SQLException
     */
    private DefaultTableModel createUpcomingModel(DefaultTableModel model,
            ResultSet resultSet) throws SQLException {
        // Go through the result set and create rows
        while (resultSet.next()) {
            String season = resultSet.getString("season");
            Date day = resultSet.getDate("matchDay");
            String home = resultSet.getString("homeName");
            int homeGoals = resultSet.getInt("homeGoals");
            int awayGoals = resultSet.getInt("awayGoals");
            String away = resultSet.getString("awayName");
            String location = resultSet.getString("location");

            model.addRow(new Object[] { season, day, home, homeGoals, awayGoals,
                    away, location });
        }
        return model;
    }

    /**
     * A method that creates a JTable containing all the match data which can be
     * loaded into a scroll pane.
     * 
     * @return
     * @throws SQLException
     */
    private JTable getUpcomingMatches() throws SQLException {
        // Connect to the database
        try {
            connect();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Create a table and a default model for the table
        JTable table = new JTable();
        String[] names = { "Season", "Day", "Home Team", "Home Goals",
                "Away Goals", "Away Team", "Location" };
        DefaultTableModel model = new DefaultTableModel(names, 0);

        // create a base query
        String query = "SELECT season, matchDay, homeName, homeGoals, "
                + "awayGoals, awayName,location FROM "
                + "premdata.upcomingMatches";

        // Get the result set
        resultSet = statement.executeQuery(query);
        // Create the model and the table
        model = createUpcomingModel(model, resultSet);
        table.setModel(model);
        resultSet.close();
        return table;
    }

    /**
     * A method to help create a model for the test table from the given result
     * set.
     * 
     * @param model
     * @param resultSet
     * @return
     * @throws SQLException
     */
    private DefaultTableModel createTestModel(DefaultTableModel model,
            ResultSet resultSet) throws SQLException {
        // Go through the entire result set
        while (resultSet.next()) {
            String team = resultSet.getString("teamName");
            int points = resultSet.getInt("points");
            int wins = resultSet.getInt("wins");
            int losses = resultSet.getInt("losses");
            int draws = resultSet.getInt("draws");
            int goals = resultSet.getInt("goals");
            int goalsCon = resultSet.getInt("goalsConceded");

            model.addRow(new Object[] { team, points, wins, losses, draws,
                    goals, goalsCon });
        }
        return model;
    }

    /**
     * A method to get the test data from the database and add it to the table.
     * 
     * @return
     * @throws SQLException
     */
    private JTable getTestTable() throws SQLException {
        // Connect to the database
        try {
            connect();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Create a table and a default model for the table
        JTable table = new JTable();
        String[] names = { "Team", "Points", "Wins", "Loses",
                "Draws", "Goals", "Goals Against" };
        DefaultTableModel model = new DefaultTableModel(names, 0);

        // create a base query
        String query = "SELECT teamName, points, wins, losses, "
                + "draws, goals, goalsConceded FROM "
                + "premdata.testStandings ORDER BY points DESC";

        // Get the result set
        resultSet = statement.executeQuery(query);
        // Create the model and the table
        model = createTestModel(model, resultSet);
        table.setModel(model);
        close();
        return table;
    }

    /**
     * Method to load in the table in a GUI window.
     * 
     * @param team
     * @param season
     * @param scroll
     * @param panel
     */
    public void loadSeasonTable(String team, String season,
            JScrollPane scroll,
            JPanel panel) {

        JTable statTable;
        try {
            statTable = getSeasonStats(team, season);
            scroll.setViewportView(statTable);
            scroll.setVisible(true);
            panel.add(scroll, BorderLayout.CENTER);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * A method for loading a table with the match data between 2 teams and
     * seasons.
     * 
     * @param team1
     * @param team2
     * @param season
     * @param scroll
     * @param panel
     */
    public void loadMatchTable(String team1, String team2, String season,
            JScrollPane scroll, JPanel panel) {
        JTable statTable;
        try {
            statTable = getMatchData(team1, team2, season);
            scroll.setViewportView(statTable);
            scroll.setVisible(true);
            panel.add(scroll, BorderLayout.CENTER);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * A method that loads the upcoming matches onto the scroll pane
     * 
     * @param scroll
     * @param panel
     */
    public JTable loadUpcoming(JScrollPane scroll, JPanel panel) {
        JTable matchTable;
        try {
            matchTable = getUpcomingMatches();
            scroll.setViewportView(matchTable);
            scroll.setVisible(true);
            panel.add(scroll);
            return matchTable;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * A method that loads the table for the current season. This table will be
     * updated when the user tests different match scores.
     * 
     * @param scroll
     * @param panel
     */
    public void loadTestTable(JScrollPane scroll, JPanel panel) {
        JTable testTable;
        try {
            testTable = getTestTable();
            scroll.setViewportView(testTable);
            scroll.setVisible(true);
            panel.add(scroll);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * A method that updates the testTable to reflect changes made by the user
     * in the upcoming matches.
     * 
     * @param matchTable
     * @throws SQLException
     */
    public void updateTestTable(JTable matchTable) throws SQLException {
        // Connect to the database
        try {
            connect();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Go through each row and column, adding the
        // elements to an array list
        for (int r = 0; r < matchTable.getModel()
                .getRowCount(); r++) {
            Vector<Object> row = new Vector<Object>();
            for (int c = 0; c < matchTable
                    .getColumnCount(); c++) {
                row.add(matchTable.getModel().getValueAt(r,
                        c));
            }
            changedTableModel.addRow(row);
        }

        // Variables to store necessary data
        String homeName = "";
        int homeID = 0;
        String awayName = "";
        int awayID = 0;
        int homeGoals = 0;
        int awayGoals = 0;
        // Go through each row and column of the model
        for (int r = 0; r < changedTableModel.getRowCount(); r++) {
            for (int c = 0; c < changedTableModel.getColumnCount(); c++) {
                // Set the values of the variables
                if (c == 2) {
                    homeName = (String) changedTableModel.getValueAt(r, c);
                } else if (c == 3) {
                    if (changedTableModel.getValueAt(r, c) instanceof String) {
                        homeGoals = Integer.valueOf(
                                (String) changedTableModel.getValueAt(r, c));
                    } else {
                        homeGoals = (int) changedTableModel.getValueAt(r, c);
                    }
                } else if (c == 4) {
                    if (changedTableModel.getValueAt(r, c) instanceof String) {
                        awayGoals = Integer.valueOf(
                                (String) changedTableModel.getValueAt(r, c));
                    } else {
                        awayGoals = (int) changedTableModel.getValueAt(r, c);
                    }
                } else if (c == 5) {
                    awayName = (String) changedTableModel.getValueAt(r, c);
                }
            }
            // Set the home and away ID variables
            ResultSet resultSet = statement.executeQuery(
                    "SELECT teamID FROM teams WHERE teamName = " + "\""
                            + homeName + "\"");
            if (resultSet.next()) {
                homeID = resultSet.getInt("teamID");
            }
            resultSet = statement.executeQuery(
                    "SELECT teamID FROM teams WHERE teamName = " + "\""
                            + awayName + "\"");
            if (resultSet.next()) {
                awayID = resultSet.getInt("teamID");
            }

            // Update the upcomingMatches table with the saved data
            String query = ("UPDATE upcomingMatches SET homeGoals = "
                    + Integer.toString(homeGoals) + ", awayGoals = "
                    + Integer.toString(awayGoals) + " WHERE upcominghomeID = "
                    + Integer.toString(homeID) + " AND upcomingawayID = "
                    + Integer.toString(awayID));
            statement.executeUpdate(query);

            // Update the testStandings table with the new data (3 pts for a
            // win, 1 for a draw, none for a loss
            if (homeGoals == awayGoals) {
                // Update home and away goals, as well as the teams draws and
                // points
                // Do not update if the score is 0-0
                if (homeGoals != 0 && awayGoals != 0) {
                    statement.executeUpdate(
                            "UPDATE testStandings SET goals = goals + "
                                    + Integer.toString(homeGoals)
                                    + ", draws = draws + 1, points = points + 1, "
                                    + "goalsConceded = goalsConceded + "
                                    + Integer.toString(awayGoals)
                                    + " WHERE testteamID = "
                                    + Integer.toString(homeID));
                    statement.executeUpdate(
                            "UPDATE testStandings SET goals = goals + "
                                    + Integer.toString(awayGoals)
                                    + ", draws = draws + 1, points = points + 1, "
                                    + "goalsConceded = goalsConceded + "
                                    + Integer.toString(homeGoals)
                                    + " WHERE testteamID = "
                                    + Integer.toString(awayID));
                }
            } else if (homeGoals > awayGoals) {
                // Update home and away goals, as well as wins, losses, and
                // points
                statement.executeUpdate(
                        "UPDATE testStandings SET goals = goals + "
                                + Integer.toString(homeGoals)
                                + ", wins = wins + 1, points = points + 3, "
                                + "goalsConceded = goalsConceded + "
                                + Integer.toString(awayGoals)
                                + " WHERE testteamID = "
                                + Integer.toString(homeID));
                statement.executeUpdate(
                        "UPDATE testStandings SET goals = goals + "
                                + Integer.toString(awayGoals)
                                + ", losses = losses + 1, "
                                + "goalsConceded = goalsConceded + "
                                + Integer.toString(homeGoals)
                                + " WHERE testteamID = "
                                + Integer.toString(awayID));
            } else {
                statement.executeUpdate(
                        "UPDATE testStandings SET goals = goals + "
                                + Integer.toString(homeGoals)
                                + ", losses = losses + 1, "
                                + "goalsConceded = goalsConceded + "
                                + Integer.toString(awayGoals)
                                + " WHERE testteamID = "
                                + Integer.toString(homeID));
                statement.executeUpdate(
                        "UPDATE testStandings SET goals = goals + "
                                + Integer.toString(awayGoals)
                                + ", wins = wins + 1, points = points + 3, "
                                + "goalsConceded = goalsConceded + "
                                + Integer.toString(homeGoals)
                                + " WHERE testteamID = "
                                + Integer.toString(awayID));
            }
        }
        // Reset the changed table model
        changedTableModel = new DefaultTableModel(colNames, 0);
    }

    /**
     * A method that resets the upcoming matches using data from the matches
     * table.
     * 
     * @param matchScroll
     * @param panel
     */
    public void resetUpcoming() throws SQLException {
        // Connect to the database
        try {
            connect();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // run a simple query to update homeGoals to 0
        // and awayGoals to 0

        statement.executeUpdate(
                "UPDATE upcomingMatches SET homeGoals = 0, awayGoals = 0");
        changedTableModel = new DefaultTableModel(colNames, 0);

    }

    /**
     * A method that resets the testStandings table using data from the
     * seasonStats table.
     * 
     * @param matchScroll
     * @param tableScroll
     * @param panel
     * @throws SQLException
     */
    public void resetTestStandings() throws SQLException {
        // Connect to the database
        try {
            connect();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Get the data from the standings database
        resultSet = statement
                .executeQuery("SELECT teamName, points, wins, losses, "
                        + "draws, goals, goalsConceded FROM "
                        + "premdata.seasonstats WHERE season = \"2023/2024\"");

        // Create an array list of object arrays to store each row
        ArrayList<Object[]> rows = new ArrayList<Object[]>();
        // Go through the resultSet to get the data from each row
        while (resultSet.next()) {
            String team = resultSet.getString("teamName");
            int points = resultSet.getInt("points");
            int wins = resultSet.getInt("wins");
            int losses = resultSet.getInt("losses");
            int draws = resultSet.getInt("draws");
            int goals = resultSet.getInt("goals");
            int goalsConceded = resultSet.getInt("goalsConceded");

            rows.add(new Object[] { team, points, wins, losses, draws, goals,
                    goalsConceded });
        }

        // Go through the rows, updating the database with each row
        for (Object[] row : rows) {
            String team = (String) row[0];
            int points = (int) row[1];
            int wins = (int) row[2];
            int losses = (int) row[3];
            int draws = (int) row[4];
            int goals = (int) row[5];
            int goalsConceded = (int) row[6];

            statement.executeUpdate("UPDATE testStandings SET points = "
                    + points
                    + ", wins = " + wins + ", losses = " + losses + ", draws = "
                    + draws + ", goals = " + goals + ", goalsConceded = "
                    + goalsConceded + " WHERE teamName = " + "\"" + team
                    + "\"");
        }
    }
}
