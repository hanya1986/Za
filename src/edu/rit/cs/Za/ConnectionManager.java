/**
 * ConnectionManager.java
 * Contributor(s):  Jordan Rosario (jar2119@rit.edu)
 */

package edu.rit.cs.Za;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Static class used to open and maintain the connection to the 'Za database.
 * Any operations that require a Connection should get the connection from this
 * class.
 */
public class ConnectionManager
{
    /* the connection to the 'Za database */
    private static Connection connection = null;
    
    /**
     * Opens a new connection to the 'Za database, creating a new local copy if
     * necessary using the provided credentials.
     * 
     * @param location  location of the database
     * @param username  the username to use to open the connection
     * @param password  the password to use to open the connection
     */
    public static void initConnection(String location, String username, String password)
        throws SQLException
    {
        closeConnection();
        String url = "jdbc:h2:" + location;
        connection = DriverManager.getConnection(url, username, password);
    }
    
    /**
     * Method used to retrieve the connection to the 'Za database if it has been
     * initialized.
     * 
     * @return the connection to the 'Za database
     */
    public static Connection getConnection()
    {
        if (connection == null)
        {
            StringBuilder builder = new StringBuilder();
            builder.append("The connection must be opened by a call to ");
            builder.append("Connection.initConnection(String,String,String) before it can be used.");
            throw new IllegalStateException(builder.toString());
        }
        
        return connection;
    }
    
    /**
     * Closes the connection to the 'Za database if it has been initialized.
     */
    public static void closeConnection()
        throws SQLException
    {
        if (connection != null)
            connection.close();
    }
}