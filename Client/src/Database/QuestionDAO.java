package Database;

import java.sql.ResultSet;

public interface QuestionDAO {
    int AddQuestion(String country, String opt1, String opt2, String opt3);
    int deleteQuestion(int id);
    int updateQuestion(String country, String opt1, String opt2, String opt3);
    ResultSet selectQuestion(String requete_selection);
    void afficherResultSet(ResultSet rs);
}
