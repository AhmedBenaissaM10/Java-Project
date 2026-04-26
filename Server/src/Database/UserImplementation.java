package Database;

import ClassesRemote.User;

import java.sql.*;
import java.util.ArrayList;

public class UserImplementation implements UserDAO {
    Connection con;
    public UserImplementation(Connection con) {
        this.con = con;
    }


    @Override
    public int AddUser(String username) {
        String query = "INSERT INTO `user` (username) VALUES (?)";
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
    @Override
    public User getUserByUsername(String username){
        try {
            String query = "SELECT * FROM `user` WHERE username = ?";
            PreparedStatement stmt = con.prepareStatement(query);
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
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



}
