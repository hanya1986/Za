package edu.rit.cs.Za.ui;

/**
 * EmployeeModel.java
 * Contributor(s):  Jordan Rosario (jar2119@rit.edu)
 */

import edu.rit.cs.Za.ConnectionManager;
import edu.rit.cs.Za.MenuManager;
import edu.rit.cs.Za.OrderManager;
import edu.rit.cs.Za.OrderType;
import edu.rit.cs.Za.ProfileManager;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.LinkedList;
import java.security.NoSuchAlgorithmException;

public class EmployeeModel
{
    private List<Map<String,String>> orders;
    private List<Map<String,String>> menu;
    private List<Map<String,String>> cart;
    private Map<String,Object> employee;
    
    private void loadActiveOrders()
        throws SQLException
    {
        List<Long> orderIDs = OrderManager.getActiveOrders();
        for (long orderid : orderIDs)
        {
            List<String> attributes = new LinkedList<String>();
            attributes.add("custid");
            attributes.add("time_order_placed");
            attributes.add("time_order_out");
            attributes.add("subtotal");
            attributes.add("tax");
            attributes.add("total");
            Map<String,Object> orderInfo = OrderManager.getOrderInfo(orderid, attributes);
            orderInfo.put("orderid", Long.toString(orderid));
            
            Map<String,String> orderProperties = new HashMap<String,String>();
            for (String key : orderInfo.keySet())
                orderProperties.put(key, orderInfo.get(key).toString());
            orders.add(orderProperties);
        }
    }
    
    private void loadMenu()
        throws SQLException
    {
        List<String> itemNames = MenuManager.getAvailableItems();
        for (String itemName : itemNames)
        {
            List<String> attributes = new LinkedList<String>();
            attributes.add("type");
            attributes.add("large_price");
            attributes.add("est_prep_time");
            Map<String,Object> itemInfo = MenuManager.getItemInfo(itemName, attributes);
            itemInfo.put("name", itemName);
            
            Map<String,String> itemProperties = new HashMap<String,String>();
            for (String key : itemInfo.keySet())
                itemProperties.put(key,  itemInfo.get(key).toString());
            menu.add(itemProperties);
        }
    }
    
    public EmployeeModel()
        throws SQLException
    {
        orders = new LinkedList<Map<String,String>>();
        menu = new LinkedList<Map<String,String>>();
        cart = new LinkedList<Map<String,String>>();
        employee = new HashMap<String,Object>();
        
        loadActiveOrders();
        loadMenu();
    }
    
    public void setOrderDelivered(long orderid, Timestamp deliveryTime)
        throws SQLException
    {
        Map<String,Object> values = new HashMap<String,Object>();
        values.put("time_order_delivered", deliveryTime);
        values.put("active", false);
        OrderManager.modifyOrder(orderid, values);
    }
    
    public void cancelOrder(long orderid)
        throws SQLException
    {
        Map<String,Object> value = new HashMap<String,Object>();
        value.put("active", false);
        OrderManager.modifyOrder(orderid, value);
    }
    
    public List<Long> getPastOrders(long custid, int nOrders)
        throws SQLException
    {
        List<Long> orderIDs = new LinkedList<Long>();
        Connection conn = ConnectionManager.getConnection();
        StringBuilder builder = new StringBuilder();
        builder.append("SELECT orderid ");
        builder.append("FROM ZaOrder ");
        builder.append("WHERE custid=? ");
        builder.append("LIMIT ?;");
        PreparedStatement ps = conn.prepareStatement(builder.toString());
        ps.setLong(1, custid);
        ps.setInt(2, nOrders);
        ResultSet rs = ps.executeQuery();
        while (rs.next())
            orderIDs.add(rs.getLong(1));
        return orderIDs;
    }
    
    public void addToCart(String itemName, int quantity)
        throws SQLException
    {
        List<String> attributes = new LinkedList<String>();
        attributes.add("type");
        attributes.add("large_price");
        attributes.add("est_prep_time");
        Map<String,Object> itemInfo = MenuManager.getItemInfo(itemName, attributes);
        itemInfo.put("name", itemName);
        itemInfo.put("quantity", Integer.toString(quantity));
        
        Map<String,String> itemProperties = new HashMap<String,String>();
        for (String key : itemInfo.keySet())
            itemProperties.put(key, itemProperties.get(key).toString());
        cart.add(itemProperties);
    }
    
    public void removeFromCart(String itemName)
    {
        Iterator<Map<String,String>> cartIt = cart.iterator();
        while (cartIt.hasNext())
        {
            Map<String,String> itemProperties = cartIt.next();
            if (itemProperties.get("name").equals(itemName))
            {
                cartIt.remove();
                break;
            }
        }
    }
    
    public void resetCart()
    {
        cart.clear();
    }
    
    public void placeOrder(long custid, OrderType orderType)
        throws SQLException
    {
        Map<String,Integer> items = new HashMap<String,Integer>();
        Iterator<Map<String,String>> cartIt = cart.iterator();
        
        while (cartIt.hasNext())
        {
            Map<String,String> itemProperties = cartIt.next();
            items.put("LARGE " + (String)itemProperties.get("name"), Integer.parseInt(itemProperties.get("quantity")));
        }
        
        OrderManager.createOrder(custid, orderType, items);
        cart.clear();
        orders.clear();
        loadActiveOrders();
    }
    
    public void createCustomerProfile(Map<String,Object> attributes, String password, List<String> phoneNumbers, List<String> emailAddresses)
        throws SQLException, NoSuchAlgorithmException
    {
        long personid = ProfileManager.createCustomer(attributes, password);
        for (String number : phoneNumbers)
            ProfileManager.addPhoneNumber(personid, number);
        for (String email : emailAddresses)
            ProfileManager.addEmailAddress(personid, email);
    }
    
    public Map<String,Object> loadEmployeeInfo(long empid)
        throws SQLException
    {
        List<String> attributes = new LinkedList<String>();
        attributes.add("first_name");
        attributes.add("middle_name");
        attributes.add("last_name");
        attributes.add("date_of_birth");
        attributes.add("street");
        attributes.add("city");
        attributes.add("state");
        attributes.add("zip");
        attributes.add("ssn");
        attributes.add("hourly_rate");
        attributes.add("hours_per_week");
        attributes.add("date_hired");
        attributes.add("job_title");
        Map<String,Object> employeeProperties = ProfileManager.getEmployeeInfo(empid, attributes);
        
        List<String> phoneNumbers = ProfileManager.getPhoneNumbers(empid);
        List<String> emailAddresses = ProfileManager.getEmailAddresses(empid);
        employeeProperties.put("phone_numbers", phoneNumbers);
        employeeProperties.put("emailAddresses", emailAddresses);
        
        return employeeProperties;
    }
    
    public void updateProfile(long empid, Map<String,Object> attributes, List<String> phoneNumbers, List<String> emailAddresses)
        throws SQLException
    {
        for (String number : phoneNumbers)
            ProfileManager.removePhoneNumber(empid, number);
        for (String email : emailAddresses)
            ProfileManager.removeEmailAddress(empid, email);
        
        ProfileManager.modifyEmployee(empid, attributes);
    }
}
