package Database;

import java.sql.*;

public class UserImplementation implements UserDAO {
    Connection con;
    public UserImplementation() {
        System.out.println("Start...");
        this.con = databaseConnection.makeConnection();
        if (this.con == null) {
            throw new RuntimeException("Database connection failed");
        }
    }

    @Override
    public int deleteUser(String username) {
        String requete_delete = "DELETE FROM `user` WHERE username ='" + username+"'";
        Statement st = null;
        if (con != null) {
            try {
                st = con.createStatement();
                int a = st.executeUpdate(requete_delete);
                if (a > 0) {
                    System.out.println("done,"+username + " deleted!");
                    return a;
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return 0;
    }

    @Override
    public int AddUser(String username) {
        String requete_insertion = "INSERT INTO `user`(`username`) VALUES ('"+username+"')";
        Statement st = null;
        if (con != null) {
            try {
                st = con.createStatement();
                int a = st.executeUpdate(requete_insertion);
                if (a > 0) {
                    System.out.println("done,"+username+" inserted!");
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
    public ResultSet selectUser(String requete_selection) {
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
