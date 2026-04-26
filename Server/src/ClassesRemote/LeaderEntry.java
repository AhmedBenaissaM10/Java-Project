package ClassesRemote;

import java.io.Serializable;

public class LeaderEntry implements Serializable {
    public String username;
    public int score;
    public int time;
    public LeaderEntry(String username, int score, int time) {
        this.username = username;
        this.score = score;
        this.time = time;
    }
}
