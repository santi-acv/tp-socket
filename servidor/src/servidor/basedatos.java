package servidor;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class basedatos {

    private static final String url = "jdbc:postgresql://localhost:5432/log";
    private static final String user = "postgres";
    private static final String password = "admin";
 
    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

}