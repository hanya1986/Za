/**
 * DbDriver.java
 * Contributor(s):  Jordan Rosario (jar2119@rit.edu)
 *                  Jeremy Friedman (jsf6410@g.rit.edu)
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
import java.util.Map;
import java.util.Random;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.security.NoSuchAlgorithmException;

public class TablePopulator
{
    private static FileReader dataFileReader;
    private static BufferedReader dataBufferedReader;
    private static Connection conn;    
    
    public TablePopulator() throws SQLException, IOException
    {
        wipeTables();
        populateTables();
        testTablesPopulated();
    }
    
    public static void wipeTableData(String tableName) throws SQLException 
    {
        conn = ConnectionManager.getConnection();
        String query = "DELETE FROM " + tableName + ";";
        PreparedStatement ps = conn.prepareStatement(query);
        ps.executeUpdate();
    }
    
    public static void wipeTables()
        throws SQLException
    {
        wipeTableData("ZaOrderItem");
        wipeTableData("ZaOrder");
        wipeTableData("Menu_Item");
        wipeTableData("CustomerCard");
        wipeTableData("Credit_Card");
        wipeTableData("PersonEmailAddress");
        wipeTableData("PersonPhoneNumber");
        wipeTableData("Employee");
        wipeTableData("Customer");
        wipeTableData("Person");
    }
    
    private static void populateTables() throws SQLException, IOException {
        //Other tables rely on personid, so populate persons first
        dataFileReader = new FileReader(new File("table_data/person_data.txt"));
        dataBufferedReader = new BufferedReader(dataFileReader);
        populatePersons(new File("table_data/person_data.txt")); 
        
        File[] dataFiles = new File("table_data/").listFiles();
        for (File dataFile : dataFiles) {
            try { dataFileReader = new FileReader(dataFile);}
            catch (FileNotFoundException e) {e.printStackTrace();}
            dataBufferedReader = new BufferedReader(dataFileReader);
            switch(dataFile.getName()) {
                case "person_data.txt":
                    break;
                case "item_data.txt":
                    populateItems(dataFile);
                    break;
                case "email_address_data.txt":
                    populateEmails(dataFile);
                    break;
                case "credit_card_data.txt":
                    populateCreditCards(dataFile);
                    break;
                case "phone_number_data.txt":
                    populatePhoneNumbers(dataFile);
                    break;
                case "order_data.txt":
                    populateOrders(dataFile);
                    break;
            }
        }
    }
    
    private static void populateOrders(File dataFile) throws SQLException {
        Map<String, ArrayList<Object>> orderData = new HashMap<String, ArrayList<Object>>();
        orderData.put("order_type", new ArrayList<Object>());
        orderData.put("time_order_placed", new ArrayList<Object>());
        orderData.put("time_order_out", new ArrayList<Object>());
        orderData.put("time_order_delivered", new ArrayList<Object>());
        orderData.put("empid_took_order", new ArrayList<Object>());
        orderData.put("empid_prepared_order", new ArrayList<Object>());
        orderData.put("empid_delivered_order", new ArrayList<Object>());
        orderData.put("subtotal", new ArrayList<Object>());
        orderData.put("tax", new ArrayList<Object>());
        orderData.put("total", new ArrayList<Object>());
        orderData.put("tip", new ArrayList<Object>());
        orderData.put("pay_method", new ArrayList<Object>()); //end order
        orderData.put("quantity", new ArrayList<Object>());
        orderData.put("size", new ArrayList<Object>());
        orderData.put("items", new ArrayList<Object>());
        
        conn = ConnectionManager.getConnection();
        StringBuilder builder = new StringBuilder();
        builder.append("SELECT * ");
        builder.append("FROM Person; ");
        PreparedStatement ps = conn.prepareStatement(builder.toString());
        ResultSet rs = ps.executeQuery();
        
        builder = new StringBuilder();
        builder.append("SELECT * ");
        builder.append("FROM Employee; ");
        PreparedStatement employeeID_PS = conn.prepareStatement(builder.toString());
        ResultSet employeeID_RS = employeeID_PS.executeQuery();
        
        ArrayList<Long> employeeIDs = new ArrayList<Long>();
        while (employeeID_RS.next()) 
        {
            employeeIDs.add(Long.parseLong(employeeID_RS.getString("empid")));
        }
         
        String currLine;
        ArrayList<Long> IDs = new ArrayList<Long>();
        try
        {
            String currKey = "";
            Random r = new Random();
            while ((currLine = dataBufferedReader.readLine()) != null)
            {
                if (currLine.startsWith("---"))
                {   //reading in new type of data
                    currKey = currLine.substring(3);    //...so update the key we're pairing vals to
                    continue;
                }
                else if (currKey.equals("order_type"))
                {
                    orderData.get(currKey).add(OrderType.parseOrderType(currLine));
                }
                else if (currKey.equals("time_order_placed"))
                {
                    orderData.get(currKey).add(new Date(Long.parseLong(currLine)));
                    orderData.get("time_order_out").add(new Date((long) (Long.parseLong(currLine) * .01)));
                    orderData.get("time_order_delivered").add(new Date((long) (Long.parseLong(currLine) * .02)));
                    orderData.get("empid_took_order").add(employeeIDs.get(r.nextInt(employeeIDs.size())));
                    orderData.get("empid_prepared_order").add(employeeIDs.get(r.nextInt(employeeIDs.size())));
                    orderData.get("empid_delivered_order").add(employeeIDs.get(r.nextInt(employeeIDs.size())));
                }
                else if (currKey.equals("subtotal"))
                {
                    orderData.get(currKey).add(new BigDecimal(currLine));
                    orderData.get("tax").add(new BigDecimal(Double.parseDouble(currLine) * .07));
                    orderData.get("tip").add(new BigDecimal(Double.parseDouble(currLine) * .05));
                    orderData.get("total").add(new BigDecimal(Double.parseDouble(currLine) + (Double.parseDouble(currLine) * .07)));
                }
                else if (currKey.equals("pay_method"))
                {
                    orderData.get(currKey).add(PaymentMethod.parsePaymentMethod(currLine));
                }
                else if (currKey.equals("quantity"))
                {
                    orderData.get(currKey).add(Integer.parseInt(currLine));
                }
                else
                    orderData.get(currKey).add(currLine);
                if (rs.next()) 
                {
                    IDs.add(Long.parseLong(rs.getString("personid")));
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        
        Map<String, Object> singleOrderData = new HashMap<String, Object>();;
        Map<String, Integer> items;
        for (int ordersCreated = 0; ordersCreated < 90; ordersCreated++) {
            singleOrderData = new HashMap<String, Object>();
            for (String orderKey : orderData.keySet())
            {
                try { singleOrderData.put(orderKey, orderData.get(orderKey).get(ordersCreated));    }
                catch(Exception e) { }
            }
            try
            {
                items = new HashMap<String, Integer>();
                items.put((String) singleOrderData.get("items"), (Integer) singleOrderData.get("quantity"));
                OrderManager.createOrder(IDs.get(ordersCreated), OrderType.parseOrderType(singleOrderData.get("order_type").toString()), items); 
                OrderManager.modifyOrder(IDs.get(ordersCreated), singleOrderData);
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }
        }
    }

    private static void populatePhoneNumbers(File dataFile) throws SQLException {
        Map<String, ArrayList<Object>> phoneData = new HashMap<String, ArrayList<Object>>();
        phoneData.put("phone_number", new ArrayList<Object>());

        conn = ConnectionManager.getConnection();
        StringBuilder builder = new StringBuilder();
        builder.append("SELECT * ");
        builder.append("FROM Person; ");
        PreparedStatement ps = conn.prepareStatement(builder.toString());
        ResultSet rs = ps.executeQuery();
        
        String currLine;
        ArrayList<Long> IDs = new ArrayList<Long>();;
        try
        {
            while ((currLine = dataBufferedReader.readLine()) != null)
            {
                if (rs.next()) 
                {
                    IDs.add(Long.parseLong(rs.getString("personid")));
                }
                phoneData.get("phone_number").add(currLine);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        HashMap<String, Object> singlePhoneData = new HashMap<String, Object>();
        for (int numbersCreated = 0; numbersCreated < 100; numbersCreated++) {
            singlePhoneData = new HashMap<String, Object>();
            for (String phoneKey : phoneData.keySet())
            {
                try { singlePhoneData.put(phoneKey, phoneData.get(phoneKey).get(numbersCreated));   }
                catch(Exception e) { }
            }
            try
            {
                ProfileManager.addPhoneNumber(IDs.get(numbersCreated), (String)singlePhoneData.get("phone_number"));
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }
        }
    }

    private static void populateCreditCards(File dataFile) throws SQLException, NumberFormatException, IOException {
        Map<String, ArrayList<Object>> ccData = new HashMap<String, ArrayList<Object>>();
        ccData.put("number", new ArrayList<Object>()); 
        ccData.put("sec_code", new ArrayList<Object>());
        ccData.put("exp_month", new ArrayList<Object>());
        ccData.put("exp_year", new ArrayList<Object>());
    
        conn = ConnectionManager.getConnection();
        StringBuilder builder = new StringBuilder();
        builder.append("SELECT * ");
        builder.append("FROM Person; ");
        PreparedStatement ps = conn.prepareStatement(builder.toString());
        ResultSet rs = ps.executeQuery();
        
        String currLine;
        ArrayList<Long> IDs = new ArrayList<Long>();;
        try
        {
            String currKey = "";
            while ((currLine = dataBufferedReader.readLine()) != null)
            {
                if (currLine.startsWith("---"))
                {   //reading in new type of data
                    currKey = currLine.substring(3);    //...so update the key we're pairing vals to
                    continue;
                }
                else
                    ccData.get(currKey).add(currLine);
                if (rs.next()) 
                {
                    IDs.add(Long.parseLong(rs.getString("personid")));
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        
        HashMap<String, Object> singleCCData = new HashMap<String, Object>();
        for (int ccsCreated = 0; ccsCreated < 90; ccsCreated++) {
            singleCCData = new HashMap<String, Object>();
            for (String ccKey : ccData.keySet())
            {
                try { singleCCData.put(ccKey, ccData.get(ccKey).get(ccsCreated));   }
                catch(Exception e) { }
            }
            try
            {
                ProfileManager.addCreditCard(IDs.get(ccsCreated), (String)singleCCData.get("number"),  (String)singleCCData.get("sec_code"), 
                        Month.parseMonth(Integer.valueOf((String)singleCCData.get("exp_month"))), Integer.valueOf((String)singleCCData.get("exp_year")));
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }
        }
    }

    private static void populateEmails(File emailsFile) throws SQLException, IOException {
        //Find a personID to attach the email to
        conn = ConnectionManager.getConnection();
        StringBuilder builder = new StringBuilder();
        builder.append("SELECT personid ");
        builder.append("FROM Person; ");
        PreparedStatement ps = conn.prepareStatement(builder.toString());
        ResultSet rs = ps.executeQuery();
        
        String currLine = "";
        while ((currLine = dataBufferedReader.readLine()) != null)
        {
            if (rs.next()) 
            {
                Long id = Long.parseLong(rs.getString("personid"));
                ProfileManager.addEmailAddress(id, currLine);   
            }
        } 
    }
    
    private static void populateItems(File itemsFile) throws SQLException {
        Map<String, ArrayList<Object>> itemData = new HashMap<String, ArrayList<Object>>();
        itemData.put("name", new ArrayList<Object>()); 
        itemData.put("type", new ArrayList<Object>());
        itemData.put("price", new ArrayList<Object>());
        itemData.put("small_price", new ArrayList<Object>());
        itemData.put("medium_price", new ArrayList<Object>());
        itemData.put("large_price", new ArrayList<Object>());
        itemData.put("est_prep_time", new ArrayList<Object>());
        itemData.put("available", new ArrayList<Object>());
    
        String currLine;
        try
        {
            String currKey = "";
            while ((currLine = dataBufferedReader.readLine()) != null)
            {
                if (currLine.startsWith("---"))
                {   //reading in new type of data
                    currKey = currLine.substring(3);    //...so update the key we're pairing vals to
                    continue;
                }
                else if (currKey.equals("price") || currKey.equals("small_price") || currKey.equals("medium_price") || 
                        currKey.equals("large_price"))
                {
                    itemData.get(currKey).add(new BigDecimal(currLine));
                }
                else if (currKey.equals("type"))
                {
                    itemData.get(currKey).add(ItemType.parseItemType(currLine));
                }
                else if (currKey.equals("est_prep_time"))
                {
                    itemData.get(currKey).add(Integer.parseInt(currLine));
                }
                else if (currKey.equals("available"))
                {
                    itemData.get(currKey).add(Boolean.parseBoolean(currLine));
                }
                else
                    itemData.get(currKey).add(currLine);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        
        HashMap<String, Object> singleItemData = new HashMap<String, Object>();
        for (int itemsCreated = 0; itemsCreated < 40; itemsCreated++) {
            singleItemData = new HashMap<String, Object>();
            for (String itemKey : itemData.keySet())
            {
                try { singleItemData.put(itemKey, itemData.get(itemKey).get(itemsCreated)); }
                catch(Exception e) { }
            }
            //System.out.println("Item Data: " + singleItemData);
            try
            {
                MenuManager.addItem(singleItemData);
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }
        }
    }

    private static void populatePersons(File personsFile) throws SQLException {
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
        personData.put("reward_pts", new ArrayList<Object>());
        personData.put("empid", new ArrayList<Object>());
        personData.put("hourly_rate", new ArrayList<Object>());
        personData.put("ssn", new ArrayList<Object>());
        personData.put("hours_per_week", new ArrayList<Object>());
        personData.put("date_hired", new ArrayList<Object>());
        personData.put("date_terminated", new ArrayList<Object>());
        personData.put("job_title", new ArrayList<Object>());
        String currLine;
        try
        {
            String currKey = "";
            while ((currLine = dataBufferedReader.readLine()) != null)
            {
                if (currLine.startsWith("---"))
                {   //reading in new type of data
                    currKey = currLine.substring(3);    //...so update the key we're pairing vals to
                    continue;
                }
                if (currKey.equals("street"))
                {
                    personData.get("street").add(currLine.split(",")[0].trim());
                    personData.get("city").add(currLine.split(",")[1].trim());
                    personData.get("state").add(State.parseState(currLine.split(",")[2].trim()));
                    personData.get("zip").add(currLine.split(",")[3].trim());
                }
                else if (currKey.equals("date_of_birth") || currKey.equals("date_hired") || currKey.equals("date_terminated"))
                    personData.get(currKey).add(new Date(Long.parseLong(currLine)));
                else if (currKey.equals("reward_pts") || currKey.equals("hours_per_week") || currKey.equals("ssn"))
                {
                    personData.get(currKey).add(Integer.parseInt(currLine));
                }
                else if (currKey.equals("empid")) 
                {
                    personData.get(currKey).add(Long.parseLong(currLine));
                }
                else if (currKey.equals("hourly_rate"))
                {
                    personData.get(currKey).add(new BigDecimal(currLine));
                }
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
                if (!((personKey.equals("hourly_rate")) || (personKey.equals("ssn") || personKey.equals("hours_per_week")
                || personKey.equals("date_hired") || personKey.equals("job_title") || personKey.equals("empid"))
                || personKey.equals("date_terminated")))
                {
                    try { customerData.put(personKey, personData.get(personKey).get(customersCreated)); }
                    catch(Exception e) { }
                }
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
        
        Map<String, Object> employeeData = new HashMap<String, Object>();;
        String[] employeeUniqueFields = {"empid", "hourly_rate", "ssn", "hours_per_week", "date_hired", "date_terminated", "job_title"};
        for (int employeesCreated = 90; employeesCreated < 100; employeesCreated++)
        {
            employeeData = new HashMap<String, Object>();
            for (String personKey : personData.keySet())
            {
                if (!(personKey.equals("reward_pts")))
                {
                    for (String s: employeeUniqueFields) 
                    {
                        if (personKey.equals(s))
                        {
                            try { employeeData.put(personKey, personData.get(personKey).get(employeesCreated - 90));    }
                            catch(Exception e) { }
                        }
                    }
                    try { employeeData.put(personKey, personData.get(personKey).get(employeesCreated)); }
                    catch(Exception e) { }
                }
            }
            //System.out.println("Employee Data: " + employeeData);
            try
            {
                ProfileManager.createEmployee(employeeData, (String) employeeData.get("password")); 
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
    }
    
    public static void testTablesPopulated() throws SQLException
    {
        testPersonPopulated();
        testCustomerPopulated();
        testEmployeePopulated();
        testMenu_ItemPopulated();
        testPersonEmailAddressPopulated();
        testCredit_CardPopulated();
        testCustomerCardPopulated();
        testPersonPhoneNumberPopulated();
        testOrderTablesPopulated();
    }
    
    private static void testOrderTablesPopulated() throws SQLException {
        StringBuilder builder = new StringBuilder();
        builder = new StringBuilder();
        builder.append("SELECT * ");
        builder.append("FROM ZaOrder; ");
        PreparedStatement ps = conn.prepareStatement(builder.toString());
        ResultSet rs = ps.executeQuery();
        while(rs.next()) 
        {
            System.out.println("Order ID: " + rs.getString("orderid"));
            System.out.println("\tCustomer's ID: " + rs.getString("custid")); 
            System.out.println("\tOrder Type: " + rs.getString("order_type"));
            System.out.println("\tActive: " + rs.getString("active"));
            System.out.println("\tID of Employee who Took Order: " + rs.getString("empid_took_order"));
            System.out.println("\tID of Employee who Prepared Order: " + rs.getString("empid_prepared_order"));
            System.out.println("\tID of Employee who Delivered Order: " + rs.getString("empid_delivered_order"));
            System.out.println("\tTime Order Placed: " + rs.getString("time_order_placed"));
            System.out.println("\tTime Order Sent: " + rs.getString("time_order_out"));
            System.out.println("\tTime Delivered: " + rs.getString("time_order_delivered"));
            System.out.println("\tSubtotal: " + rs.getString("subtotal"));
            System.out.println("\tTax: " + rs.getString("tax"));
            System.out.println("\tTotal: " + rs.getString("total"));
            System.out.println("\tTip: " + rs.getString("tip"));
            System.out.println("\tPayment Method: " + rs.getString("pay_method"));
        }
        
    }

    private static void testPersonPhoneNumberPopulated() throws SQLException {
        StringBuilder builder = new StringBuilder();
        builder = new StringBuilder();
        builder.append("SELECT * ");
        builder.append("FROM PersonPhoneNumber; ");
        PreparedStatement ps = conn.prepareStatement(builder.toString());
        ResultSet rs = ps.executeQuery();
        while(rs.next()) 
        {
            System.out.println("Person's ID: " + rs.getString("personid"));
            System.out.println("\tPerson's Phone #: " + rs.getString("phone_number")); 
        }
    }

    private static void testCustomerCardPopulated() throws SQLException {
        StringBuilder builder = new StringBuilder();
        builder = new StringBuilder();
        builder.append("SELECT * ");
        builder.append("FROM CustomerCard; ");
        PreparedStatement ps = conn.prepareStatement(builder.toString());
        ResultSet rs = ps.executeQuery();
        while(rs.next()) 
        {
            System.out.println("Customer's ID: " + rs.getString("personid"));
            System.out.println("\tCustomer's Credit Card #: " + rs.getString("card_number")); 
        }
    }

    private static void testCredit_CardPopulated() throws SQLException {
        StringBuilder builder = new StringBuilder();
        builder = new StringBuilder();
        builder.append("SELECT * ");
        builder.append("FROM Credit_Card; ");
        PreparedStatement ps = conn.prepareStatement(builder.toString());
        ResultSet rs = ps.executeQuery();
        while(rs.next()) 
        {
            System.out.println("Customer's Credit Card #: " + rs.getString("number"));
            System.out.println("\tSecurity Code: " + rs.getString("sec_code")); 
            System.out.println("\tExpiration Month: " + rs.getString("exp_month")); 
            System.out.println("\tExpiration Year: " + rs.getString("exp_year")); 
        }
    }

    private static void testEmployeePopulated() throws SQLException {
        StringBuilder builder = new StringBuilder();
        builder = new StringBuilder();
        builder.append("SELECT * ");
        builder.append("FROM Employee; ");
        PreparedStatement ps = conn.prepareStatement(builder.toString());
        ResultSet rs = ps.executeQuery();
        while(rs.next()) 
        {
            System.out.println("Person's Employee ID: " + rs.getString("empid"));
            System.out.println("\tPerson's Employee Hourly Rate: " + rs.getString("hourly_rate")); 
            System.out.println("\tPerson's Employee SSN: " + rs.getString("ssn")); 
            System.out.println("\tPerson's Employee Hours Per Week: " + rs.getString("hours_per_week")); 
            System.out.println("\tPerson's Employee Date Hired: " + rs.getString("date_hired")); 
            System.out.println("\tPerson's Employee Date Terminated: " + rs.getString("date_terminated")); 
            System.out.println("\tPerson's Employee Job Title: " + rs.getString("job_title")); 
        }
    }

    private static void testCustomerPopulated() throws SQLException {
        StringBuilder builder = new StringBuilder();
        builder = new StringBuilder();
        builder.append("SELECT * ");
        builder.append("FROM Customer; ");
        PreparedStatement ps = conn.prepareStatement(builder.toString());
        ResultSet rs = ps.executeQuery();
        while(rs.next()) 
        {
            System.out.println("Person's Customer ID: " + rs.getString("cust_id"));
            System.out.println("\tPerson's Customer Reward Pts: " + rs.getString("reward_pts")); 
            System.out.println("\tPerson's Active Status: " + rs.getString("active")); 
        }
    }

    private static void testPersonPopulated() throws SQLException {
        StringBuilder builder = new StringBuilder();
        builder = new StringBuilder();
        builder.append("SELECT * ");
        builder.append("FROM Person; ");
        PreparedStatement ps = conn.prepareStatement(builder.toString());
        ResultSet rs = ps.executeQuery();
        while(rs.next()) 
        {
            System.out.println("Person ID: " + rs.getString("personid")); 
            System.out.println("\tPerson First Name: " + rs.getString("first_name")); 
            System.out.println("\tPerson Middle Name: " + rs.getString("middle_name")); 
            System.out.println("\tPerson Last Name: " + rs.getString("last_name")); 
            System.out.println("\tPerson DoB: " + rs.getString("date_of_birth")); 
            System.out.println("\tPerson Username: " + rs.getString("username")); 
            System.out.println("\tPerson Password Hash: " + rs.getString("password_hash")); 
            System.out.println("\tPerson Password Salt: " + rs.getString("password_salt")); 
            System.out.println("\tPerson Street: " + rs.getString("street")); 
            System.out.println("\tPerson City: " + rs.getString("city")); 
            System.out.println("\tPerson State: " + rs.getString("state")); 
            System.out.println("\tPerson ZIP: " + rs.getString("zip")); 
        }
    }

    private static void testPersonEmailAddressPopulated() throws SQLException {
        StringBuilder builder = new StringBuilder();
        builder = new StringBuilder();
        builder.append("SELECT * ");
        builder.append("FROM PersonEmailAddress; ");
        PreparedStatement ps = conn.prepareStatement(builder.toString());
        ResultSet rs = ps.executeQuery();
        while(rs.next()) 
        {
            System.out.println("Person's ID: " + rs.getString("personid"));
            System.out.println("\tPerson's Email Address: " + rs.getString("email_addr")); 
        }
    }

    private static void testMenu_ItemPopulated() throws SQLException {
        StringBuilder builder = new StringBuilder();
        builder.append("SELECT * ");
        builder.append("FROM Menu_Item; ");
        PreparedStatement ps = conn.prepareStatement(builder.toString());
        ResultSet rs = ps.executeQuery();
        StringBuilder testBuilder = new StringBuilder();
        while(rs.next()) 
        {
            System.out.println("Item Name: " + rs.getString("name")); 
            System.out.println("\tItem Type: " + rs.getString("type")); 
            System.out.println("\tItem Price: " + rs.getString("price")); 
            System.out.println("\tItem Small Price: " + rs.getString("small_price")); 
            System.out.println("\tItem Medium Price: " + rs.getString("medium_price")); 
            System.out.println("\tItem Large Price: " + rs.getString("large_price")); 
            System.out.println("\tItem Est. Prep. Time: " + rs.getString("est_prep_time")); 
            System.out.println("\tItem Availability: " + rs.getString("available")); 
        }
    }

    public static void main(String[] args)
    {
        String db_location = "./ZADB/za";
        String db_path = db_location + ".mv.db";

        File f = new File(db_path);
        if (f.exists()) f.delete();

        String username = "username";
        String password = "password";
        
        try
        {
            ConnectionManager.initConnection(db_location, username, password);
        }
        catch (SQLException ex)
        {
            System.out.println("A SQL error occurred while attempting to initialize the database connection.");
            System.out.println(ex);
            return;
        }
        
        try
        {
            ZaDatabase.createDatabase();
        }
        catch (SQLException ex)
        {
            System.out.println("A SQL error occurred while attempting to create the database.");
            System.out.println(ex);
            return;
        }
        
        try
        {
            new TablePopulator();
        }
        catch (IOException ex)
        {
            System.out.println("An error occurred while attempting to read one of the data files.");
            System.out.println(ex);
        }
        catch (SQLException ex)
        {
            System.out.println("A SQL error occurred while attempting to recreate the test database.");
            System.out.println(ex);
        }
        
        try
        {
            ConnectionManager.closeConnection();
        }
        catch (SQLException ex)
        {
            // whatever
        }
    }
}