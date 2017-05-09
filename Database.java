import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 * @author Raphael Abrahamson
 */

public class Database {
ArrayList <String>  initials = new ArrayList<>();
ArrayList <Integer> scores   = new ArrayList<>();
String connection = "";

public Database(){
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
public void getScores(){

            try (Connection connect = DriverManager.getConnection(connection)) {
                System.out.println("Connected to Database!");                
                PreparedStatement state = connect.prepareStatement("Select TOP 10 FROM PongHighScores ORDER BY Score Desc");                
                System.out.println("Query Executed Successfully!");
                ResultSet rs = state.executeQuery();
                System.out.println("getting initials");
                while (rs.next()) {
                String s = rs.getString("Initials");
                System.out.println(s);
                initials.add(s);                
                scores.add(rs.getInt("Score"));
                }
            } catch (SQLException sqlex) {
                     sqlex.printStackTrace();
        Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, sqlex);
    }        

}    

public void checkForWinningScore(int score){
    for (int i = 0; i < this.scores.size(); i++) {
    if(score>scores.get(i)){
    String winnerInitals=getWinnerInitials();
    updateDatabase(scores.get(i), score, winnerInitals);
    }
    
    }
}
        
public void updateDatabase(int oldScore, int newScore, String initials){
            try (Connection connect = DriverManager.getConnection(connection)) {
                System.out.println("Connected to Database!");                 
                String s ="UPDATE PongHighScores SET Initials = '" +initials.trim() + "', Scores = '" +newScore+"' WHERE Scores = "+oldScore+";";
            PreparedStatement state = connect.prepareStatement(s);                
            state.executeUpdate();     
            connect.commit();
            connect.close();
            
            } catch (SQLException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
            }
            System.out.println("Database Closed!");
                 
        
}
public String getWinnerInitials(){
return JOptionPane.showInputDialog("you are a winner! please enter your name!");
}
}
