package Database;

import Classes.User;

import java.sql.*;
import java.util.ArrayList;

public class UserImplementation implements UserDAO {
    Connection con;
    public UserImplementation(Connection con) {
        this.con = con;
    }
    @Override
    public int AddUser(int id, String username) {
        String query = "INSERT INTO users (id, username) VALUES (?, ?)";
        try {
            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1, id);
            ps.setString(2, username);

            return ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public int deleteUser(int id) {
        String query = "DELETE FROM `user` WHERE id = ?";
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
        String query = "SELECT * FROM `user` WHERE user_id = ?";
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
        String query = "UPDATE `user` SET best_game_id = ? WHERE user_id = ?";
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
    @Override
    public ArrayList<User> getAllUsers() {

        ArrayList<User> users = new ArrayList<>();
        String query = "SELECT * FROM `user`";

        try {
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(query);

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



}
