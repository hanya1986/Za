/**
 * ConnectionManager.java
 * Contributor(s):  Jordan Rosario (jar2119@rit.edu)
 */

package edu.rit.cs.Za;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionManager
{
    private static Connection connection = null;
    
    public static void initConnection(String location, String username, String password)
        throws SQLException
    {
        if (connection != null) connection.close();
        String url = "jdbc:h2:" + location;
        connection = DriverManager.getConnection(url, username, password);
    }
    
    public static Connection getConnection()
    {
        return connection;
    }
    
    public static void closeConnection()
        throws SQLException
    {
        connection.close();
    }
}