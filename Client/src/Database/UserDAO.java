package Database;

import Classes.User;

import java.sql.ResultSet;
import java.util.ArrayList;

public interface UserDAO {
    int deleteUser(int id);
    int AddUser(int id, String username);
    User getUser(int id);
    int UpdateUser(int id, int game_id);
    ArrayList<User> getAllUsers();

}
