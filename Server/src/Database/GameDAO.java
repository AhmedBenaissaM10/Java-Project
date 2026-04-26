package Database;

import ClassesRemote.Game;

import java.util.ArrayList;

public interface GameDAO {
    int AddGame(int User_id, int score, int timeSpent);
    int deleteGame(int id);
    ArrayList<Game> showHistorty(int User_id);
    Game getGame (int id);
    Game getBestGame (int id);
}
