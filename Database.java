package mco364;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Raphael Abrahamson
 */
public class Database {

    private ArrayList<String> initials = new ArrayList<>();
    private ArrayList<Integer> scores = new ArrayList<>();
    private String connection;

    public Database() {
        establishConnection();
        populateDatabase();
    }

    public ArrayList<Integer> getScores() {
        return new ArrayList<>(scores);
    }

    public ArrayList<String> getInitials() {
        return new ArrayList<>(initials);
    }

    public void refreshScores() {
        populateDatabase();
    }

    public void updateScores(int score, String name) {
        Connection connect;
        String valuesStatemnt = "VALUES(" + score + ",'" + name + "')";
        try {
            connect = DriverManager.getConnection(connection);
            System.out.println("Connected to Database!");
            PreparedStatement state = connect.prepareStatement(
                    "USE DS2\n"
                    + "INSERT INTO PongHighScores "
                    + "(Score, Initials)" + valuesStatemnt);
            int rowsAffected = state.executeUpdate();
            System.out.println("Query Executed Successfully!\n "
                    + rowsAffected
                    + " rows affected");

        } catch (SQLException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void establishConnection() {
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            System.out.println("Driver Successfully Loaded!");
            String driver = "jdbc:sqlserver:";
            String url = "//lcmdb.cbjmpwcdjfmq.us-east-1.rds.amazonaws.com:";
            String port = "1433";
            String username = "DS2";
            String password = "Touro123";
            String database = "DS2";
            connection = driver + url + port
                    + ";databaseName=" + database + ";user=" + username + ";password=" + password + ";";
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void populateDatabase() {
        try (Connection connect = DriverManager.getConnection(connection)) {
            System.out.println("Connected to Database!");
            PreparedStatement state = connect.prepareStatement("USE DS2\n"
                    + "Select TOP 10 Score, Initials\n"
                    + "FROM PongHighScores\n"
                    + "ORDER BY Score Desc");
            System.out.println("Query Executed Successfully!");
            ResultSet rs = state.executeQuery();
            if (!initials.isEmpty()) {
                initials.clear();
                scores.clear();
            }
            while (rs.next()) {
                initials.add(rs.getString("Initials"));
                scores.add(rs.getInt("Score"));
            }
        } catch (SQLException sqlex) {
            sqlex.printStackTrace();
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, sqlex);
        }
    }

    public boolean checkForWinningScore(int score) {
        for (int i = 0; i < this.scores.size(); i++) {
            if (score > scores.get(i)) {
                return true;
            }
        }
        return false;
    }

}
