package Classes;

public class Question {
    int id;
    String Answer;
    String Option1;
    String Option2;
    String Option3;
    public Question(){}
    public Question(int id, String answer, String option1, String option2, String option3) {
        this.id = id;
        Answer = answer;
        Option1 = option1;
        Option2 = option2;
        Option3 = option3;
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAnswer() {
        return Answer;
    }

    public void setAnswer(String answer) {
        Answer = answer;
    }

    public String getOption1() {
        return Option1;
    }

    public void setOption1(String option1) {
        Option1 = option1;
    }

    public String getOption2() {
        return Option2;
    }

    public void setOption2(String option2) {
        Option2 = option2;
    }

    public String getOption3() {
        return Option3;
    }

    public void setOption3(String option3) {
        Option3 = option3;
    }
}
