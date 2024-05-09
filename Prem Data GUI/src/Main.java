import java.awt.EventQueue;
import java.sql.SQLException;

public class Main {

    public static void main(String[] args) {
        JDBC connection;
        try {
            connection = new JDBC();
            EventQueue.invokeLater(new Runnable() {
                public void run() {
                    try {
                        HomePage frame = new HomePage(connection);
                        frame.setVisible(true);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
