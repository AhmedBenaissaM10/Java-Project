package Database;

import java.sql.ResultSet;

public interface GameDAO {
    int AddGame(int User_id, int score, int timeSpent);
    int deleteGame(int id);
    int showHistorty(int User_id);
    ResultSet selectGame(String requete_selection);
    void afficherResultSet(ResultSet rs);

}
