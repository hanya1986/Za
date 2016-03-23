import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionManager
{
    private static Connection connection = null;
    
    public static void initConnection(String location, String username, String password) throws SQLException, ClassNotFoundException
    {
        if (connection != null) connection.close();
        String url = "jdbc:h2:" + location;
        Class.forName("org.h2.Driver");
        connection = DriverManager.getConnection(url, username, password);
    }
    
    public static Connection getConnection() { return connection; }
}