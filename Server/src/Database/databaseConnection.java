package Database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class databaseConnection {

    private static Connection con;

    public static Connection makeConnection() {

        if (con != null) return con; // 🔥 reuse connection

        try {
            Class.forName(Config.NOM_DRIVER);
            System.out.println("Driver OK..");

            con = DriverManager.getConnection(
                    Config.URL_DB,
                    Config.USERNAME,
                    Config.PASSWORD
            );

            System.out.println("Connected...");

        } catch (ClassNotFoundException e) {
            System.err.println("❌ Driver Error: " + e.getMessage());
            throw new RuntimeException(e);
        } catch (SQLException e) {
            System.err.println("❌ Connection Error: " + e.getMessage());
            throw new RuntimeException(e);
        }

        return con;
    }
}