package ClassesRemote;

import java.rmi.RemoteException;
import java.util.ArrayList;

public interface QuizzRemote {
    int AddGame(int User_id, int score, int timeSpent) throws RemoteException;

    int deleteGame(int id) throws RemoteException;

    ArrayList<Game> showHistorty(int User_id) throws RemoteException;

    Game getGame(int id) throws RemoteException;

    int AddQuestion(String country, String opt1, String opt2, String opt3) throws RemoteException;

    int deleteQuestion(int id) throws RemoteException;

    int updateQuestion(int id,String country, String opt1, String opt2, String opt3) throws RemoteException;

    Question getQuestion(int id) throws RemoteException;

    ArrayList<Question> getQuestions() throws RemoteException;

    int deleteUser(int id) throws RemoteException;

    int AddUser(String username) throws RemoteException;

    User getUser(int id) throws RemoteException;

    int UpdateUser(int id, int game_id) throws RemoteException;

    Game getBestGame(int id) throws RemoteException;
}