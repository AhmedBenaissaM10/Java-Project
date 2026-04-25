import ClassesRemote.Game;
import Database.GameImplementation;
import Database.databaseConnection;

import javax.swing.*;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.sql.Connection;

public class Main {
    public static void main(String[] args) {
        System.out.println("Starting Server...");
        try {
            Connection con = databaseConnection.makeConnection();
            final QuizzImplementaion quizz = new QuizzImplementaion(con);

            LocateRegistry.createRegistry(9003);
            String url = "rmi://localhost:9003/quizz";
            Naming.rebind(url, quizz);
            System.out.println("Server bound at " + url);

            // Launch admin UI on the Swing thread.
            SwingUtilities.invokeLater(() -> {
                QuestionAdminUI ui = new QuestionAdminUI(quizz);
                ui.setVisible(true);
                ui.refresh();
            });

        } catch (RemoteException e) {
            System.out.println("Erreur Registery: " + e.getMessage());
        } catch (MalformedURLException e) {
            System.out.println("Erreur Dans l'URL: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Erreur: " + e.getMessage());
        }
    }
}