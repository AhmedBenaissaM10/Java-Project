package Database;

import ClassesRemote.User;

import java.util.ArrayList;

public interface UserDAO {
    int deleteUser(int id);
    int AddUser(String username);
    User getUser(int id);
    int UpdateUser(int id, int game_id);
    ArrayList<User> getAllUsers();
    User getUserByUsername(String username);

}
