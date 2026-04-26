package Database;

import ClassesRemote.Question;

import java.sql.*;
import java.util.ArrayList;

public class QuestionImplementation implements QuestionDAO {
    Connection con;

    public QuestionImplementation(Connection con) {
        this.con = con;
    }

    @Override
    public int AddQuestion(String country, String opt1, String opt2, String opt3) {
        String requete_insertion = "INSERT INTO `questions`(`Country_name`, `option1`, `option2`, `option3`) VALUES ('" + country + "','" + opt1 + "','" + opt2 + "','" + opt3 + "')";
        Statement st = null;
        if (con != null) {
            try {
                st = con.createStatement();
                int a = st.executeUpdate(requete_insertion);
                if (a > 0) {
                    System.out.println("done, inserted!");
                    return a;
                }
            } catch (SQLException e) {
                if (e.getErrorCode() == 1062) {
                    return -1;
                }
                throw new RuntimeException(e);
            }
        }
        return 0;
    }

    @Override
    public int deleteQuestion(int id) {
        String sql = "DELETE FROM questions WHERE id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public int updateQuestion(int id, String country, String opt1, String opt2, String opt3) {
        String sql = "UPDATE questions SET Country_name = ?, option1 = ?, option2 = ?, option3 = ? WHERE id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, country);
            ps.setString(2, opt1);
            ps.setString(3, opt2);
            ps.setString(4, opt3);
            ps.setInt(5, id);
            return ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public Question getQuestion(int id) {
        return null;
    }

    // ... existing code ...
    @Override
    public ArrayList<Question> getQuestions() {
        String query = "SELECT * FROM questions";
        ArrayList<Question> Questions = new ArrayList<>();

        try {
            PreparedStatement ps = con.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Question q = new Question();
                q.setId(rs.getInt("id"));
                q.setAnswer(rs.getString("Country_name"));
                q.setOption1(rs.getString("option1"));
                q.setOption2(rs.getString("option2"));
                q.setOption3(rs.getString("option3"));
                Questions.add(q);
            }
            return Questions;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return new ArrayList<>();
    }


}
