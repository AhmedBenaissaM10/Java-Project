package ClassesRemote;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface GameRemote extends Remote {
    void notifyServer(String message) throws RemoteException;
    ArrayList<String> getConnectedUsers() throws RemoteException;
    // Game
    int AddGame(int User_id, int score, int timeSpent) throws RemoteException;

    int deleteGame(int id) throws RemoteException;

    ArrayList<Game> showHistorty(int User_id) throws RemoteException;

    Game getGame(int id) throws RemoteException;

    // Question

    int AddQuestion(String country, String opt1, String opt2, String opt3) throws RemoteException;

    int deleteQuestion(int id) throws RemoteException;

    int updateQuestion(int id,String country, String opt1, String opt2, String opt3) throws RemoteException;

    Question getQuestion(int id) throws RemoteException;

    ArrayList<Question> getQuestions() throws RemoteException;

    // User

    ArrayList<User> getAllUsers() throws RemoteException;

    User getUserByUsername(String username)  throws RemoteException;

    int deleteUser(int id) throws RemoteException;

    int AddUser(String username) throws RemoteException;

    User getUser(int id) throws RemoteException;

    int UpdateUser(int id, int game_id) throws RemoteException;

    Game getBestGame(int id) throws RemoteException;
}