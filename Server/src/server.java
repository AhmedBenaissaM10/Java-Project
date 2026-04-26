import Database.databaseConnection;

import javax.swing.*;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.sql.Connection;

public class server {
    public static void main(String[] args) {
        System.out.println("Starting Server....");
        // allocation du port
        try {
            LocateRegistry.createRegistry(9005);
            String url = "rmi://localhost:9005/Game";
            Connection conn = databaseConnection.makeConnection();
            GameImplementaion GameImp = new GameImplementaion(conn);
            Naming.rebind(url, GameImp);
            SwingUtilities.invokeLater(() -> new ServerFrame(GameImp));
            ServerFrame.log("✅ RMI Registry bound at " + url);
        } catch (RemoteException e) {
            ServerFrame.log("❌ Registry error: " + e.getMessage());
        } catch (MalformedURLException e) {
            ServerFrame.log("❌ URL error: " + e.getMessage());
        }

    }
}
