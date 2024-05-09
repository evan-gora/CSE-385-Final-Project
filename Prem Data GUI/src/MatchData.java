import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;

/**
 * A class to allow a user to display all matches for one team or between two
 * different teams for a season of their choice. Will be displayed if the user
 * presses the "View Matches" button on the home page.
 * 
 * @author Evan Gora
 */

public class MatchData extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private String team1 = "All Teams";
    private String team2 = "All Teams";
    private String season = "All Seasons";

    /**
     * Create the frame.
     */
    public MatchData(JDBC connection) {
        // Set the frame and content pane
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 450, 300);
        setTitle("Match Viewer");
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

        setContentPane(contentPane);
        contentPane.setLayout(new BorderLayout(0, 0));
        contentPane.setPreferredSize(new Dimension(1000, 450));

        // Create a menu bar at the top of the page
        JPanel menuPanel = new JPanel();
        contentPane.add(menuPanel, BorderLayout.NORTH);
        menuPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        
        // Create a panel to display results
        JPanel panel = new JPanel();
        contentPane.add(panel, BorderLayout.CENTER);
        panel.setLayout(new BorderLayout(0, 0));

        // Create components
        JLabel lblNewLabel = new JLabel("View Matches", SwingConstants.CENTER);
        JScrollPane scrollPane = new JScrollPane();
        
        // Default the scroll pane to show current season matches.
        connection.loadMatchTable(team1, team2, season, scrollPane, panel);
        
        // Add components to the panel
        panel.add(lblNewLabel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        
        // Create the labels for the combo boxes.
        JLabel team1txt = new JLabel("Team 1:");
        JLabel team2txt = new JLabel("Team 2:");
        JLabel seasontxt = new JLabel("Season:");
        JButton confirmBtn = new JButton("Confirm");
        JButton clearBtn = new JButton("Clear Filters");
        JButton homeBtn = new JButton("Home");

        // Get an array of all teams and add to the team combo boxes
        try {
            // Create the team selections and season selection 
            String[] teams = connection.getTeams();
            String[] seasons = connection.getSeasons();
            JComboBox<String> teamCombo1 = new JComboBox<String>(teams);
            JComboBox<String> teamCombo2 = new JComboBox<String>(teams);
            JComboBox<String> seasonCombo = new JComboBox<String>(seasons);
            // Add the labels and combo boxes to the panel
            menuPanel.add(team1txt);
            menuPanel.add(teamCombo1);
            menuPanel.add(team2txt);
            menuPanel.add(teamCombo2);
            menuPanel.add(seasontxt);
            menuPanel.add(seasonCombo);
            
            // Add functionality for the confirm, clear, and home buttons
            // Confirm
            confirmBtn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    panel.removeAll();
                    team1 = teamCombo1.getSelectedItem().toString();
                    team2 = teamCombo2.getSelectedItem().toString();
                    season = seasonCombo.getSelectedItem().toString();
                    connection.loadMatchTable(team1, team2, season, scrollPane, panel);
                }
            });
            // Clear Button
            clearBtn.addActionListener(new ActionListener () {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // Set the selected value in the combo boxes to "All Teams" or "All Seasons"
                    teamCombo1.setSelectedIndex(0);
                    teamCombo2.setSelectedIndex(0);
                    seasonCombo.setSelectedIndex(0);
                    panel.removeAll();
                    team1 = teamCombo1.getSelectedItem().toString();
                    team2 = teamCombo2.getSelectedItem().toString();
                    season = seasonCombo.getSelectedItem().toString();
                    connection.loadMatchTable(team1, team2, season, scrollPane, panel);
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
        
        
        // Add the components to the menu
        menuPanel.add(confirmBtn);
        menuPanel.add(clearBtn);
        menuPanel.add(homeBtn);

        pack();
    }

}
