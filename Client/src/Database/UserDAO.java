package Database;

import Classes.User;

import java.sql.ResultSet;

public interface UserDAO {
    int deleteUser(int id);
    int AddUser(int id, String username);
    User getUser(int id);
    int UpdateUser(int id, int game_id);

}
