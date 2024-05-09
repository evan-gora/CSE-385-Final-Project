import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JLabel;
import javax.swing.JComboBox;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;

/**
 * A class to create the window that allows a user to look at season stats for a
 * team and season of their choice. Will be displayed if the user presses the
 * "View Season Stats" button from the home page.
 * 
 * @author Evan Gora
 */

public class SeasonStats extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private String team = "All Teams";
    private String season = "All Seasons";

    /**
     * Create the frame.
     */
    public SeasonStats(JDBC connection) {
        // Create the frame and content panel
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 450, 300);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

        setContentPane(contentPane);
        contentPane.setLayout(new BorderLayout(0, 0));
        contentPane.setPreferredSize(new Dimension(2000, 900));

        // Create a panel to store a menu bar at the top
        JPanel panel = new JPanel();
        contentPane.add(panel, BorderLayout.NORTH);
        panel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        // Add a panel to show the results
        JPanel resultsPanel = new JPanel();
        contentPane.add(resultsPanel, BorderLayout.CENTER);
        resultsPanel.setLayout(new BorderLayout(0, 0));

        // Create components for the results
        JLabel resultstxt = new JLabel("Results", SwingConstants.CENTER);
        JScrollPane scrollPane = new JScrollPane();
        connection.loadSeasonTable(team, season, scrollPane, resultsPanel);
        // Add components to the panel
        resultsPanel.add(resultstxt, BorderLayout.NORTH);

        // Create the text components
        JLabel teamtxt = new JLabel("Team:");
        JComboBox<String> teamSelect;
        JLabel seasontxt = new JLabel("Season:");
        JComboBox<String> seasonSelect;
        JButton confirmBtn = new JButton("Confirm");
        JButton clearBtn = new JButton("Clear Filters");
        JButton homeBtn = new JButton("Home");
        // Create the team and season combo boxes, as well as define functionality for the buttons
        try {
            String[] teams = connection.getTeams();
            String[] seasons = connection.getSeasons();
            // Create the combo boxes
            teamSelect = new JComboBox<String>(teams);
            seasonSelect = new JComboBox<String>(seasons);
            // Add the combo boxes and text to the panel
            panel.add(teamtxt);
            panel.add(teamSelect);
            panel.add(seasontxt);
            panel.add(seasonSelect);

            // Functionality for the confirm, clear, and home buttons
            // Confirm Button
            confirmBtn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    resultsPanel.removeAll();
                    team = teamSelect.getSelectedItem().toString();
                    season = seasonSelect.getSelectedItem().toString();
                    connection.loadSeasonTable(team, season, scrollPane, resultsPanel);
                }
            });
            // Clear Button
            clearBtn.addActionListener(new ActionListener () {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // Set the selected value in the combo boxes to "All Teams" or "All Seasons"
                    teamSelect.setSelectedIndex(0);
                    seasonSelect.setSelectedIndex(0);
                    resultsPanel.removeAll();
                    team = teamSelect.getSelectedItem().toString();
                    season = seasonSelect.getSelectedItem().toString();
                    connection.loadSeasonTable(team, season, scrollPane, resultsPanel);
                }
                
            });
            // Home Button
            // Closes the current window and opens a new home page.
            homeBtn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    dispose();
                    HomePage home = new HomePage(connection);
                    home.setVisible(true);
                }
            });
            
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Add the buttons to the panel
        panel.add(confirmBtn);
        panel.add(clearBtn);
        panel.add(homeBtn);

        pack();
    }

}
