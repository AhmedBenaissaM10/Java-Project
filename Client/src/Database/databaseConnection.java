package Database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class databaseConnection {
    public static Connection makeConnection(){
        // Chargement diver
        try {
            Class.forName(Config.NOM_DRIVER);
            System.out.println("Driver OK..");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Erreur Driver : "+e.getMessage());
        }
        // Connection
        Connection con =null;
        try {
            con = DriverManager.getConnection(Config.URL_DB,Config.USERNAME,Config.PASSWORD);
            System.out.println("Connected...");
        } catch (SQLException e) {
            throw new RuntimeException("Erreur Connexion : "+e.getMessage());
        }
        return con;
    }
}
