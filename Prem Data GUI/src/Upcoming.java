import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.FlowLayout;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;

public class Upcoming extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JTable matchTable = new JTable();

    /**
     * Create the frame.
     */
    public Upcoming(JDBC connection) {
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
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

        setContentPane(contentPane);
        contentPane.setLayout(new BorderLayout(0, 0));
        contentPane.setPreferredSize(new Dimension(2000, 500));
        ;

        // Create a panel to store the buttons
        JPanel panel = new JPanel();
        contentPane.add(panel, BorderLayout.NORTH);
        panel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

        // Create a panel to store upcoming matches and the table
        JPanel resultsPanel = new JPanel();
        contentPane.add(resultsPanel, BorderLayout.CENTER);
        resultsPanel.setLayout(new GridLayout(1, 0, 0, 0));

        // Create the panes to store matches and the table
        JScrollPane matchPane = new JScrollPane();
        JScrollPane tablePane = new JScrollPane();

        // Load the upcoming matches and the testTable into the scroll panes
        matchTable = connection.loadUpcoming(matchPane, resultsPanel);
        connection.loadTestTable(tablePane, resultsPanel);

        // Create the buttons
        JButton saveBtn = new JButton("Save Changes");
        JButton homeBtn = new JButton("Home");
        JButton resetBtn = new JButton("Reset");

        // Functionality for the home and save buttons
        // Save Button
        // Saves the changes in the table to the database and updates the
        // testTable
        saveBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resultsPanel.removeAll();
                try {
                    connection.updateTestTable(matchTable);
                    connection.loadTestTable(tablePane, resultsPanel);
                    resultsPanel.add(matchPane);
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
                
            }
        });
        // Reset
        // Resets the table to the original table
        resetBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resultsPanel.removeAll();
                try {
                    connection.resetUpcoming();
                    connection.loadUpcoming(matchPane, resultsPanel);
                    connection.resetTestStandings();
                    connection.loadTestTable(tablePane, resultsPanel);
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
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

        // Add the buttons to the panel
        panel.add(saveBtn);
        panel.add(resetBtn);
        panel.add(homeBtn);

        pack();
    }

}
