import Classes.Game;
import Classes.User;
import Database.GameImplementation;
import Database.UserImplementation;
import Database.databaseConnection;

import java.sql.Connection;
import java.util.ArrayList;

public class history {

    User player;
    GameImplementation gameDAO;

    public history(User player, GameImplementation gameDAO) {
        this.player = player;
        this.gameDAO = gameDAO;
    }

    void getHistory() {
        ArrayList<Game> list = gameDAO.showHistorty(player.getUser_id());

        for (Game g : list) {
            System.out.println(g);
        }
    }

    public static void main(String[] args) {

        Connection conn = databaseConnection.makeConnection();

        UserImplementation userDAO = new UserImplementation(conn);
        GameImplementation gameDAO = new GameImplementation(conn);

        User user = userDAO.getUser(1);

        history h1 = new history(user, gameDAO);
        h1.getHistory();
    }
}