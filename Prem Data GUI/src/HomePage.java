import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.JButton;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

/**
 * A home page for the GUI. Allows the user to choose between 2 different
 * functionalities for the program.
 * 
 * @author 
 */

public class HomePage extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;

    /**
     * Create the frame.
     */
    public HomePage(JDBC connection) {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // Save the store to a file upon closing the home page.
        WindowListener listener = new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent we) {
                connection.close();
            }
        };
        addWindowListener(listener);
        setBounds(100, 100, 450, 300);
        setTitle("Premier League Data");
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

        setContentPane(contentPane);
        contentPane.setLayout(new BorderLayout(0, 0));
        contentPane.setPreferredSize(new Dimension(2000, 400));

        // Panel to allow a user to select to either view match data or season
        // data
        JPanel selectionPanel = new JPanel();
        GridBagLayout gbl_panel = new GridBagLayout();
        gbl_panel.columnWidths = new int[] { 117, 0 };
        gbl_panel.rowHeights = new int[] { 123, 123, 0 };
        gbl_panel.columnWeights = new double[] { 0.0, Double.MIN_VALUE };
        gbl_panel.rowWeights = new double[] { 0.0, 0.0, Double.MIN_VALUE };
        selectionPanel.setLayout(gbl_panel);

        // Create the components
        JButton matchViewBtn = new JButton("View Matches");
        matchViewBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                MatchData matchWindow = new MatchData(connection);
                matchWindow.setVisible(true);
            }
        });

        JButton seasonDataBtn = new JButton("View Season Stats");
        seasonDataBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                SeasonStats seasonWindow = new SeasonStats(connection);
                seasonWindow.setVisible(true);
            }
        });
        
        JButton upcomingBtn = new JButton("View Upcoming Matches");
        upcomingBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                Upcoming upcomingMatches = new Upcoming(connection);
                upcomingMatches.setVisible(true);
            }
        });
        
        // Set grid bag constraints for the buttons
        GridBagConstraints gbc_matchViewBtn = new GridBagConstraints();
        gbc_matchViewBtn.fill = GridBagConstraints.BOTH;
        gbc_matchViewBtn.insets = new Insets(35, 15, 10, 15);
        gbc_matchViewBtn.gridx = 0;
        gbc_matchViewBtn.gridy = 0;
        GridBagConstraints gbc_seasonDataBtn = new GridBagConstraints();
        gbc_seasonDataBtn.fill = GridBagConstraints.BOTH;
        gbc_seasonDataBtn.insets = new Insets(20, 15, 20, 15);
        gbc_seasonDataBtn.gridx = 0;
        gbc_seasonDataBtn.gridy = 1;
        GridBagConstraints gbc_upcomingBtn = new GridBagConstraints();
        gbc_upcomingBtn.fill = GridBagConstraints.BOTH;
        gbc_upcomingBtn.insets = new Insets(10, 15, 50, 15);
        gbc_upcomingBtn.gridx = 0;
        gbc_upcomingBtn.gridy = 2;

        // Add the components to the panel
        selectionPanel.add(matchViewBtn, gbc_matchViewBtn);
        selectionPanel.add(seasonDataBtn, gbc_seasonDataBtn);
        selectionPanel.add(upcomingBtn, gbc_upcomingBtn);

        // Panel to show current season table
        JPanel viewPanel = new JPanel();
        viewPanel.setLayout(new BorderLayout(0, 0));

        JLabel currLbl = new JLabel("Current Standings", SwingConstants.CENTER);

        JScrollPane scrollPane = new JScrollPane();
        try {
            connection.loadSeasonTable("All Teams", "2023/2024", scrollPane, viewPanel);
        } catch (Exception e) {
            e.printStackTrace();
        }
            

        // Add the components to the panel
        viewPanel.add(currLbl, BorderLayout.NORTH);
        viewPanel.add(scrollPane, BorderLayout.CENTER);

        // Add the two panels to the main panel
        contentPane.add(selectionPanel, BorderLayout.WEST);
        contentPane.add(viewPanel, BorderLayout.CENTER);

        pack();
    }

}
