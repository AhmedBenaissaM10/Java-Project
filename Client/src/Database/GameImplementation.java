package Database;

import Classes.Game;

import java.sql.*;
import java.util.ArrayList;

public class GameImplementation implements GameDAO {
    Connection con;
    public GameImplementation(Connection con) {
        this.con = con;
    }
    @Override
    public int AddGame(int user_id, int score, int timeSpent) {
        String query = "INSERT INTO games (user_id, score, time_spent) VALUES (?, ?, ?)";
        try {
            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1, user_id);
            ps.setInt(2, score);
            ps.setInt(3, timeSpent);
            return ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

    @Override
    public int deleteGame(int id) {
        String requete_delete = "DELETE FROM `games` WHERE id =" + id;
        Statement st = null;
        if (con != null) {
            try {
                st = con.createStatement();
                int a = st.executeUpdate(requete_delete);
                if (a > 0) {
                    System.out.println("done, deleted!");
                    return a;
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return 0;
    }

    @Override
    public Game getGame(int id) {
        String query = "SELECT * FROM games WHERE id = ?";

        try {
            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1, id);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Game game = new Game();
                game.setId(rs.getInt("id"));
                game.setUser_id(rs.getInt("user_id"));
                game.setScore(rs.getInt("score"));
                game.setTimeSpent(rs.getInt("time_spent"));
                game.setPlayedAt(rs.getTimestamp("played_at"));

                return game;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public ArrayList<Game> showHistorty(int user_id) {
        ArrayList<Game> games = new ArrayList<>();
        String query = "SELECT * FROM games WHERE user_id = ? ORDER BY played_at DESC";

        try {
            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1, user_id);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Game game = new Game();
                game.setId(rs.getInt("id"));
                game.setUser_id(rs.getInt("user_id"));
                game.setScore(rs.getInt("score"));
                game.setTimeSpent(rs.getInt("time_spent"));
                game.setPlayedAt(rs.getTimestamp("played_at"));

                games.add(game);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return games;
    }
}
