package Database;

import java.sql.ResultSet;

public interface UserDAO {
    int deleteUser(String id);
    int AddUser(String id);
    ResultSet selectUser(String requete_selection);
    void afficherResultSet(ResultSet rs);
}
