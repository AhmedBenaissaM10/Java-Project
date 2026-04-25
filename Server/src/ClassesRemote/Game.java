package ClassesRemote;

import java.sql.Timestamp;

public class Game {
    int id;
    int user_id;
    int score;
    int timeSpent;
    Timestamp playedAt;
    public Game(int id, int user_id, int score, int timeSpent) {
        this.id = id;
        this.user_id = user_id;
        this.score = score;
        this.timeSpent = timeSpent;
    }

    public Game() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getTimeSpent() {
        return timeSpent;
    }

    public void setTimeSpent(int timeSpent) {
        this.timeSpent = timeSpent;
    }

    public void setPlayedAt(Timestamp playedAt) {
        this.playedAt = playedAt;
    }
    public Timestamp getPlayedAt() {
        return playedAt;
    }
    @Override
    public String toString() {
        return "Game{" +
                "id=" + id +
                ", user_id=" + user_id +
                ", score=" + score +
                ", timeSpent=" + timeSpent +
                ", playedAt=" + playedAt +
                '}';
    }
}
