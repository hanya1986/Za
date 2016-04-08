/**
 * DbDriver.java
 * Contributor(s):  Jordan Rosario (jar2119@rit.edu)
 * 					Jeremy Friedman (jsf6410@g.rit.edu)
 */

package edu.rit.cs.Za;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.security.NoSuchAlgorithmException;

public class DbDriver
{
	
	private static FileReader dataFileReader;
	private static BufferedReader dataBufferedReader;
	private static Connection conn;

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
            System.err.println(ex);
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
            System.out.printf("Username %s not in database. (PASSED)%n", fakeUsername);
        else
            System.out.println("Call to method ProfileManager.validateCredentials returned incorrect result. (FAILED)");
        System.out.println();
        
        System.out.println("ATTEMPTING LOGIN WITH INCORRECT PASSWORD");
        result = ProfileManager.validateCredentials((String)customer.get("username"), fakePassword);
        if (result == ProfileManager.INCORRECT_PASSWORD)
            System.out.println("Incorrect password. (PASSED)");
        else
            System.out.println("Call to method ProfileManager.validateCredentials returned incorrect result. (FAILED)");
        System.out.println();
        
        System.out.println("LOGGING IN WITH VALID CREDENTIALS");
        result = ProfileManager.validateCredentials((String)customer.get("username"), pw);
        if (result == custid)
            System.out.printf("(\'%s\',\'%s\') login successful. (PASSED)%n", (String)customer.get("username"), pw);
        else
            System.out.println("Call to method ProfileManager.validateCredentials returned incorrect result. (FAILED)");
        System.out.println();
    }

	public static void main(String[] args)
        throws Exception // test-driver program; swallow exceptions
    {

        String db_location = "./ZADB/za";
        String db_path = db_location + ".mv.db";
        
        // removes old database file to run clean test
        File f = new File(db_path);
        if (f.exists()) {
            System.out.println("REMOVING OLD DATABASE\n");
            f.delete();
        }

        String username = "username";
        String password = "password";
        
        System.out.println("INITIALIZING DATABASE CONNECTION");
        System.out.printf("location: %s%n", db_location);
        System.out.printf("username: %s%n", username);
        System.out.printf("password: %s%n", password);
        ConnectionManager.initConnection(db_location, username, password);
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
        
        TablePopulator tp = new TablePopulator();
    }
}
