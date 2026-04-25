package Database;

import ClassesRemote.Question;
import ClassesRemote.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class UserImplementation implements UserDAO {
    Connection con;
    public UserImplementation(Connection con) {
        this.con = con;
    }


    @Override
    public int AddUser(String username) {
        String query = "INSERT INTO users (username) VALUES (?)";
        try (PreparedStatement ps = con.prepareStatement(query, java.sql.Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, username);
            int affected = ps.executeUpdate();
            if (affected == 0) return -1;
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public ArrayList<User> getUsers() {
        String query = "SELECT * FROM `users`";
        ArrayList<User> users = new ArrayList<>();
        try (PreparedStatement ps = con.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                User user = new User();
                user.setUser_id(rs.getInt("user_id"));
                user.setUsername(rs.getString("username"));
                user.setBest_game_id(rs.getInt("best_game_id"));
                users.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    @Override
    public int deleteUser(int id) {
        String query = "DELETE FROM `users` WHERE id = ?";
        try {
            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1, id);

            return ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    @Override
    public User getUser(int id) {
        String query = "SELECT * FROM `users` WHERE user_id = ?";
        try {
            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1, id);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                User user = new User();
                user.setUser_id(rs.getInt("user_id"));
                user.setUsername(rs.getString("username"));
                user.setBest_game_id(rs.getInt("best_game_id"));
                return user;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    @Override
    public int UpdateUser(int id, int game_id) {
        String query = "UPDATE `users` SET best_game_id = ? WHERE user_id = ?";
        try {
            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1, game_id);
            ps.setInt(2, id);

            return ps.executeUpdate(); // returns number of affected rows

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }



}
