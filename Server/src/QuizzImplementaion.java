import ClassesRemote.Game;
import ClassesRemote.Question;
import ClassesRemote.QuizzRemote;
import ClassesRemote.User;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.Connection;
import java.util.ArrayList;

import Database.GameImplementation;
import Database.QuestionImplementation;
import Database.UserImplementation;

public class QuizzImplementaion extends UnicastRemoteObject implements QuizzRemote {
    private final GameImplementation gameImp;
    private final UserImplementation userImp;
    private final QuestionImplementation questImp;
    private final Connection con;

    protected QuizzImplementaion(Connection con) throws RemoteException {
        this.con = con;
        gameImp  = new GameImplementation(con);
        userImp  = new UserImplementation(con);
        questImp = new QuestionImplementation(con);
    }

    // -------------------- Games --------------------

    @Override
    public int AddGame(int userId, int score, int timeSpent) throws RemoteException {
        int affected = gameImp.AddGame(userId, score, timeSpent);
        if (affected <= 0) return affected;

        Game best = gameImp.getBestGame(userId);
        if (best == null) return affected;

        User u = userImp.getUser(userId);
        if (u == null) {
            System.err.println("AddGame: no user with id " + userId + ", skipping best-game update");
            return affected;
        }

        if (u.getBest_game_id() != best.getId()) {
            userImp.UpdateUser(userId, best.getId());
        }
        return affected;
    }

    @Override
    public int deleteGame(int id) throws RemoteException {
        return gameImp.deleteGame(id);
    }

    @Override
    public ArrayList<Game> showHistorty(int userId) throws RemoteException {
        return gameImp.showHistorty(userId);
    }

    @Override
    public Game getGame(int id) throws RemoteException {
        return gameImp.getGame(id);
    }

    @Override
    public Game getBestGame(int userId) throws RemoteException {
        return gameImp.getBestGame(userId);
    }

    // -------------------- Questions --------------------

    @Override
    public int AddQuestion(String country, String opt1, String opt2, String opt3) throws RemoteException {
        return questImp.AddQuestion(country, opt1, opt2, opt3);
    }

    @Override
    public int deleteQuestion(int id) throws RemoteException {
        return questImp.deleteQuestion(id);
    }

    @Override
    public int updateQuestion(int id, String country, String opt1, String opt2, String opt3) throws RemoteException {
        return questImp.updateQuestion(id, country, opt1, opt2, opt3);
    }

    @Override
    public Question getQuestion(int id) throws RemoteException {
        return null;
    }

    @Override
    public ArrayList<Question> getQuestions() throws RemoteException {
        return questImp.getQuestions();
    }

    // -------------------- Users --------------------

    @Override
    public int deleteUser(int id) throws RemoteException {
        return userImp.deleteUser(id);
    }

    @Override
    public int AddUser(String username) throws RemoteException {
        return userImp.AddUser(username);
    }

    @Override
    public User getUser(int id) throws RemoteException {
        return userImp.getUser(id);
    }

    @Override
    public int UpdateUser(int id, int gameId) throws RemoteException {
        return userImp.UpdateUser(id, gameId);
    }
}