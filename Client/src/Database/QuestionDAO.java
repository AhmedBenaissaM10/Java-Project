package Database;

import Classes.Question;

import java.sql.ResultSet;

public interface QuestionDAO {
    int AddQuestion(String country, String opt1, String opt2, String opt3);
    int deleteQuestion(int id);
    int updateQuestion(String country, String opt1, String opt2, String opt3);
    Question getQuestion(int id);
}
