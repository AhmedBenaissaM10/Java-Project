package Database;

import Classes.Question;

import java.sql.*;

public class QuestionImplementation implements QuestionDAO{
    Connection con;
    public QuestionImplementation(Connection con) {
        this.con = con;
    }
    @Override
    public int AddQuestion(String country, String opt1, String opt2, String opt3) {
        String requete_insertion = "INSERT INTO `questions`(`Country_name`, `option1`, `option2`, `option3`) VALUES ('"+country+"','"+ opt1+"','"+ opt2+"','"+ opt3+"')";
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
        return 0;
    }

    @Override
    public int updateQuestion(String country, String opt1, String opt2, String opt3) {
        return 0;
    }
    @Override
    public Question getQuestion(int id) {
        String query = "SELECT * FROM questions WHERE id = ?";

        try {
            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1, id);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Question q = new Question();
                q.setId(rs.getInt("id"));
                q.setAnswer(rs.getString("country"));
                q.setOption1(rs.getString("option1"));
                q.setOption2(rs.getString("option2"));
                q.setOption3(rs.getString("option3"));

                return q;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }


}
