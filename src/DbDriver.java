import java.sql.*;
import java.util.ArrayList;
import java.util.Random;

import org.h2.table.Table;

public class DbDriver
{
    /* SHA-512 produces 512-bit/64-byte digests */
    public static final int DIGEST_SIZE_IN_BYTES = 64;
    
    private Connection conn;
    
    public void createConnection(String location, String user, String password)
    {
        try
        {
            String url = "jdbc:h2:" + location;
            Class.forName("org.h2.Driver");
            conn = DriverManager.getConnection(url, user, password);
        }
        catch (SQLException | ClassNotFoundException e)
        {
            e.printStackTrace();
        }
    }
    
    public Connection getConnection()
    {
        return conn;
    }

    public void closeConnection()
    {
        try
        {
            conn.close();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args)
        throws Exception // test-driver program; swallow exceptions
    {    
        DbDriver dbDriver = new DbDriver();
        String location = "C:\\ZADB\\za";
        String user = "username";
        String password = "password";
        dbDriver.createConnection(location, user, password);
    }
}
