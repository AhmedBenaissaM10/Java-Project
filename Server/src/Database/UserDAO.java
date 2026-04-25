package Database;

import ClassesRemote.User;

import java.util.ArrayList;

public interface UserDAO {
    ArrayList<User> getUsers();
    int deleteUser(int id);
    int AddUser(String username);
    User getUser(int id);
    int UpdateUser(int id, int game_id);

}
