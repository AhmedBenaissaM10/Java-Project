import ClassesRemote.Game;
import ClassesRemote.Question;
import ClassesRemote.GameRemote;
import ClassesRemote.User;
import Database.GameImplementation;
import Database.QuestionImplementation;
import Database.UserImplementation;

import javax.swing.*;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.Connection;
import java.util.ArrayList;

public class GameImplementaion extends UnicastRemoteObject implements GameRemote {
    private final GameImplementation gameImp;
    private final UserImplementation userImp;
    private final QuestionImplementation questImp;
    private final Connection con;
    private static final ArrayList<String> connectedUsers = new ArrayList<>();

    protected GameImplementaion(Connection con) throws RemoteException {
        this.con = con;
        gameImp  = new GameImplementation(con);
        userImp  = new UserImplementation(con);
        questImp = new QuestionImplementation(con);
    }

    // -------------------- Games --------------------



    @Override
    public void notifyServer(String message) throws RemoteException {
        SwingUtilities.invokeLater(() -> ServerFrame.log(message));
        // track connections
        if (message.contains("connected") && !message.contains("disconnected")) {
            String username = message.split(" ")[1]; // extract username
            if (!connectedUsers.contains(username)) connectedUsers.add(username);
            SwingUtilities.invokeLater(() -> AdminFrame.refreshConnected(connectedUsers));
        }
        if (message.contains("disconnected")) {
            String username = message.split(" ")[1];
            connectedUsers.remove(username);
            SwingUtilities.invokeLater(() -> AdminFrame.refreshConnected(connectedUsers));
        }
    }

    @Override
    public ArrayList<String> getConnectedUsers() throws RemoteException {
        return connectedUsers;
    }

    @Override
    public int AddGame(int userId, int score, int timeSpent) throws RemoteException {
        return gameImp.AddGame(userId, score, timeSpent);

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
    public ArrayList<User> getAllUsers() throws RemoteException {
        return userImp.getAllUsers();
    }

    @Override
    public User getUserByUsername(String username) throws RemoteException {
        return userImp.getUserByUsername(username);
    }

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