/**
 * DbDriver.java
 * Contributor(s):  Jordan Rosario (jar2119@rit.edu)
 */

package edu.rit.cs.Za;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.sql.Date;
import java.sql.SQLException;
import java.security.NoSuchAlgorithmException;

public class DbDriver
{
	
	private static FileReader dataFileReader;
	private static BufferedReader dataBufferedReader;

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
    
    
    private static void populateTables() {
		File[] dataFiles = new File("table_data/").listFiles();
		for (File dataFile : dataFiles) {
			try { dataFileReader = new FileReader(dataFile);}
			catch (FileNotFoundException e) {e.printStackTrace();}
			dataBufferedReader = new BufferedReader(dataFileReader);
			switch(dataFile.getName()) {
				case "person_data.txt": //common employee, customer data
					populatePersons(dataFile);
			}
		}
    }
    
    private static void populatePersons(File personsFile) {
    	//Initialize personData.
		Map<String, ArrayList<Object>> personData = new HashMap<String, ArrayList<Object>>();
		personData.put("street", new ArrayList<Object>()); 
		personData.put("city", new ArrayList<Object>());
		personData.put("state", new ArrayList<Object>());
		personData.put("zip", new ArrayList<Object>());
		personData.put("first_name", new ArrayList<Object>());
		personData.put("middle_name", new ArrayList<Object>());
		personData.put("last_name", new ArrayList<Object>());
		personData.put("date_of_birth", new ArrayList<Object>());
		personData.put("username", new ArrayList<Object>());
		personData.put("password", new ArrayList<Object>());
		String currLine;
		try
		{
			String currKey = "";
			while ((currLine = dataBufferedReader.readLine()) != null)
			{
				if (currLine.startsWith("---"))
				{   //reading in new type of data
					currKey = currLine.substring(3);	//...so update the key we're pairing vals to
					continue;
				}
				if (currKey.equals("street"))
				{
					personData.get("street").add(currLine.split(",")[0].trim());
					personData.get("city").add(currLine.split(",")[1].trim());
					personData.get("state").add(State.parseState(currLine.split(",")[2].trim()));
					personData.get("zip").add(currLine.split(",")[3].trim());
				}
				else if (currKey.equals("date_of_birth"))
					personData.get(currKey).add(new Date(Long.parseLong(currLine)));
				else
				    personData.get(currKey).add(currLine);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	
		//Make personData ready for ProfileManager creation method.
		//I.e., Map<String, ArrayList<Object>> --> Map<String, Object>
		Map<String, Object> customerData = new HashMap<String, Object>();;
		for (int customersCreated = 0; customersCreated < 90; customersCreated++) {
			customerData = new HashMap<String, Object>();
			for (String personKey : personData.keySet())
			{
				try { customerData.put(personKey, personData.get(personKey).get(customersCreated));	}
				catch(Exception e) { }
			}
			//System.out.println("Customer Data: " + customerData);
			try
			{
				ProfileManager.createCustomer(customerData, (String) customerData.get("password")); //redundant, but I'm lazy
			}
			catch (NoSuchAlgorithmException e)
			{
				e.printStackTrace();
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
		}
		
		for (int employeesCreated = 0; employeesCreated < 10; employeesCreated++)
		{
			
		}
	}
    
    public static void main(String[] args)
        throws Exception // test-driver program; swallow exceptions
    {
        StringBuilder builder = new StringBuilder();
        builder.append("E:\\");
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
        
        //BEGIN JEREMY'S DATA GENERATION
        populateTables();
    }
}
