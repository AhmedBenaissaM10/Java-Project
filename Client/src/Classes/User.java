package Classes;

public class User {
    int user_id;
    String username;
    int best_game_id;
    public User(){

    };
    public User(int user_id, String username, int best_game_id) {
        this.user_id = user_id;
        this.username = username;
        this.best_game_id = best_game_id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getBest_game_id() {
        return best_game_id;
    }

    public void setBest_game_id(int best_game_id) {
        this.best_game_id = best_game_id;
    }

    @Override
    public String toString() {
        return "User{" +
                "user_id=" + user_id +
                ", username='" + username + '\'' +
                ", best_game_id=" + best_game_id +
                '}';
    }
}
