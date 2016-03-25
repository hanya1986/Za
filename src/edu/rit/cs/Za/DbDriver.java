/**
 * DbDriver.java
 * Contributor(s):  Jordan Rosario (jar2119@rit.edu)
 */

package edu.rit.cs.Za;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.sql.Date;
import java.sql.SQLException;
import java.security.NoSuchAlgorithmException;

public class DbDriver
{
    private static long testCustomerProfileCreation(Map<String,Object> customer, String pw)
        throws SQLException, NoSuchAlgorithmException
    {
        System.out.println("TESTING CUSTOMER PROFILE CREATION");
        long custid;
        try
        {
            custid = ProfileManager.createCustomer(customer, pw);
            System.out.printf("CUSTOMER PROFILE CREATED: custid=%d%n", custid);
        }
        catch (SQLException ex)
        {
            custid = ProfileManager.getPersonID((String)customer.get("username"));
            System.out.println(ex);
            System.out.println("CUSTOMER PROFILE NOT CREATED");
        }
        System.out.println();
        
        return custid;
    }
    
    private static void testCustomerLoginValidation(long custid, Map<String,Object> customer, String pw)
        throws SQLException, NoSuchAlgorithmException
    {
        String fakeUsername = "abc1234";
        String fakePassword = "drowssap";
        
        System.out.println("ATTEMPTING LOGIN WITH NON-EXISTENT USERNAME");
        long result = ProfileManager.validateCredentials(fakeUsername, fakePassword);
        if (result == ProfileManager.USERNAME_NOT_IN_TABLE)
        {
            System.out.printf("Username %s not in database.%n", fakeUsername);
            System.out.println();
        }
        
        System.out.println("ATTEMPTING LOGIN WITH INCORRECT PASSWORD");
        result = ProfileManager.validateCredentials((String)customer.get("username"), fakePassword);
        if (result == ProfileManager.INCORRECT_PASSWORD)
        {
            System.out.println("Incorrect password.");
            System.out.println();
        }
        
        System.out.println("LOGGING IN WITH VALID CREDENTIALS");
        result = ProfileManager.validateCredentials((String)customer.get("username"), pw);
        if (result == custid)
        {
            System.out.printf("(\'%s\',\'%s\') login successful.%n", (String)customer.get("username"), pw);
            System.out.println();
        }
        System.out.println();
    }
    
    public static void main(String[] args)
        throws Exception // test-driver program; swallow exceptions
    {
        StringBuilder builder = new StringBuilder();
        builder.append(System.getProperty("user.home"));
        builder.append(File.separatorChar);
        builder.append("ZADB");
        builder.append(File.separatorChar);
        builder.append("za");
        
        String location = builder.toString();
        String username = "username";
        String password = "password";
        
        System.out.println("INITIALIZING DATABASE CONNECTION");
        System.out.printf("location: %s%n", location);
        System.out.printf("username: %s%n", username);
        System.out.printf("password: %s%n", password);
        ConnectionManager.initConnection(location, username, password);
        System.out.println("DATABASE CONNECTION INITIALIZED");
        System.out.println();
        
        System.out.println("CREATING DATABASE");
        ZaDatabase.createDatabase();
        System.out.println("DATABASE CREATED");
        System.out.println();
        
        Map<String,Object> customer = new HashMap<String,Object>();
        customer.put("first_name", "Jordan");
        customer.put("last_name", "Rosario");
        customer.put("date_of_birth", new Date(1993 - 1900, 3, 29));
        customer.put("username", "jar2119");
        customer.put("state", State.NEW_YORK);
        
        /* non-existent columns (should be) ignored */
        customer.put("hours_of_netflix_viewed", 9001);
        String pw = "you'll never guess";
        long custid = testCustomerProfileCreation(customer, pw);
        testCustomerLoginValidation(custid, customer, pw);
        
        Map<String,Object> employee = new HashMap<String,Object>();
    }
}
