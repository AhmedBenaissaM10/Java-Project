package Database;

import ClassesRemote.Question;

import java.util.ArrayList;

public interface QuestionDAO {
    int AddQuestion(String country, String opt1, String opt2, String opt3);
    int deleteQuestion(int id);
    int updateQuestion(int id,String country, String opt1, String opt2, String opt3);
    ArrayList<Question> getQuestions();
}
