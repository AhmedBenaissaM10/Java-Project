package Database;

import java.sql.*;

public class GameImplementation implements GameDAO {
    Connection con;
    public GameImplementation() {
        System.out.println("Start...");
        this.con = databaseConnection.makeConnection();
    }


    @Override
    public int AddGame(int User_id, int score, int timeSpent) {
        String requete_insertion = "INSERT INTO `games`( `user_id`, `score`, `time_spent`) VALUES ("+ User_id+","+score+","+ timeSpent+")";
        Statement st = null;
        if (con != null) {
            try {
                st = con.createStatement();
                int a = st.executeUpdate(requete_insertion);
                if (a > 0) {
                    System.out.println("done, inserted!");
                    return a;
                }
            } catch (SQLException e) {
                if (e.getErrorCode() == 1062) {
                    return -1;
                }
                throw new RuntimeException(e);
            }
        }
        return 0;
    }

    @Override
    public int deleteGame(int id) {
        String requete_delete = "DELETE FROM `games` WHERE id =" + id;
        Statement st = null;
        if (con != null) {
            try {
                st = con.createStatement();
                int a = st.executeUpdate(requete_delete);
                if (a > 0) {
                    System.out.println("done, deleted!");
                    return a;
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return 0;
    }

    @Override
    public int showHistorty(int User_id) {
        return 0;
    }

    @Override
    public ResultSet selectGame(String requete_selection) {
        if (con != null) {
            Statement st = null;
            try {
                st = con.createStatement();
                ResultSet rs = st.executeQuery(requete_selection);
                return rs;
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    @Override
    public void afficherResultSet(ResultSet rs) {

    }
}
