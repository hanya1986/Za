
/**
 * OrderManager.java
 * Contributor(s):  Jordan Rosario (jar2119@rit.edu)
 */

package edu.rit.cs.Za;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.Iterator;
import java.util.List;
import java.util.LinkedList;
import java.util.HashMap;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class OrderManager
{   
    public static BigDecimal TAX_RATE = new BigDecimal("0.08");
    
    private static void updateTotal(long orderid)
        throws SQLException
    {
        Connection conn = ConnectionManager.getConnection();
        BigDecimal subtotal = new BigDecimal("0.00");
        StringBuilder builder = new StringBuilder();
        
        builder.append("SELECT small_price,medium_price,large_price,size,quantity ");
        builder.append("FROM Menu_Item INNER JOIN ZaOrderItem ON ZaOrderItem.itemid=Menu_Item.name ");
        builder.append("WHERE orderid=?;");
        PreparedStatement ps = conn.prepareStatement(builder.toString());
        ps.setLong(1, orderid);
        ResultSet rs = ps.executeQuery();
        
        while (rs.next())
        {
            BigDecimal price = null;
            String size = rs.getString(4).toUpperCase();
            if (size.equals(ItemSize.SMALL.toString()))
                price = rs.getBigDecimal(1);
            else if (size.equals(ItemSize.MEDIUM.toString()))
                price = rs.getBigDecimal(2);
            else if (size.equals(ItemSize.LARGE.toString()))
                price = rs.getBigDecimal(3);
            
            BigDecimal quantity = new BigDecimal(rs.getInt(2));
            subtotal = subtotal.add(price.multiply(quantity));
        }
        
        BigDecimal tax = subtotal.multiply(TAX_RATE);
        BigDecimal total = subtotal.add(tax);
        subtotal.setScale(2, RoundingMode.HALF_UP);
        tax.setScale(2, RoundingMode.HALF_UP);
        total.setScale(2, RoundingMode.HALF_UP);
        
        builder = new StringBuilder();
        builder.append("UPDATE ZaOrder ");
        builder.append("SET subtotal=?,tax=?,total=? ");
        builder.append("WHERE orderid=?;");
        ps = conn.prepareStatement(builder.toString());
        ps.setBigDecimal(1, subtotal);
        ps.setBigDecimal(2, tax);
        ps.setBigDecimal(3, total);
        ps.setLong(4, orderid);
        ps.executeUpdate();
        return;
    }
    
    public static long createOrder(long custid, OrderType orderType, Map<String,Integer> items)
        throws SQLException
    {
        Connection conn = ConnectionManager.getConnection();
        StringBuilder builder = new StringBuilder();
        builder.append("INSERT INTO ZaOrder (custid,order_type) ");
        builder.append("VALUES (?,?);");
        PreparedStatement ps = conn.prepareStatement(builder.toString(), new String[]{ "orderid" });
        ps.setLong(1, custid);
        ps.setString(2, orderType.toString());
        ps.executeUpdate();
        ResultSet generatedKey = ps.getGeneratedKeys();
        generatedKey.next();
        long orderid = generatedKey.getLong(1);
        addItems(orderid, items);
        return orderid;
    }
    
    public static List<Long> getActiveOrders()
        throws SQLException
    {
        Connection conn = ConnectionManager.getConnection();
        StringBuilder builder = new StringBuilder();
        builder.append("SELECT orderid ");
        builder.append("FROM ZaOrder ");
        builder.append("WHERE active=TRUE;");
        PreparedStatement ps = conn.prepareStatement(builder.toString());
        ResultSet rs = ps.executeQuery();
        List<Long> activeIDs = new LinkedList<Long>();
        while (rs.next())
            activeIDs.add(rs.getLong(1));
        return activeIDs;
    }
    
    public static void addItems(long orderid, Map<String,Integer> items)
        throws SQLException
    {
        Connection conn = ConnectionManager.getConnection();
        StringBuilder builder = new StringBuilder();
        builder.append("INSERT INTO ZaOrderItem (orderid,itemid,quantity,size) ");
        builder.append("VALUES ");
        Iterator<String> itemIt = items.keySet().iterator();
        while (itemIt.hasNext())
        {
        	itemIt.next();
            builder.append("(?,?,?,?)");
            if (itemIt.hasNext()) builder.append(',');
        }
        builder.append(';');
        PreparedStatement ps = conn.prepareStatement(builder.toString());
        itemIt = items.keySet().iterator();
        int paramIdx = 1;
        while (itemIt.hasNext())
        {
            String item = itemIt.next();
            String size = "";
            for (ItemSize itemSize : ItemSize.values())
            {
                if (item.toUpperCase().startsWith(itemSize.toString()))
                    size = itemSize.toString();
            }
            
            String name = item.substring(size.length() + 1);
            
            ps.setLong(paramIdx++, orderid);
            ps.setString(paramIdx++, name);
            ps.setInt(paramIdx++, items.get(item));
            ps.setString(paramIdx++, size);
        }
        ps.executeUpdate();
        updateTotal(orderid);
    }
    
    public static void changeQuantities(long orderid, Map<String,Integer> items)
        throws SQLException
    {
        Connection conn = ConnectionManager.getConnection();
        StringBuilder builder = new StringBuilder();
        builder.append("UPDATE ZaOrderItem ");
        builder.append("SET quantity=? ");
        builder.append("WHERE orderid=? AND itemid=? AND size=?;");
        PreparedStatement ps = conn.prepareStatement(builder.toString());
        Iterator<String> itemIt = items.keySet().iterator();
        while (itemIt.hasNext())
        {
            String item = itemIt.next();
            String size = "";
            for (ItemSize itemSize : ItemSize.values())
            {
                if (item.toUpperCase().startsWith(itemSize.toString()))
                    size = itemSize.toString();
            }
            
            String name = item.substring(size.length() + 1);
            int quantity = items.get(item);
            
            ps.setInt(1, quantity);
            ps.setLong(2, orderid);
            ps.setString(3, name);
            ps.setString(4, size);
            ps.executeUpdate();
        }
        updateTotal(orderid);
        return;
    }
    
    public static void removeItems(long orderid, List<String> items)
        throws SQLException
    {
        Connection conn = ConnectionManager.getConnection();
        StringBuilder builder = new StringBuilder();
        builder.append("DELETE FROM ZaOrderItem ");
        builder.append("WHERE orderid=?");
        Iterator<String> itemIt = items.iterator();
        if (itemIt.hasNext())
        {
            builder.append(" AND (");
            while (itemIt.hasNext())
            {
                builder.append("(itemid=? AND size=?)");
                if (itemIt.hasNext()) builder.append(" OR ");
                else builder.append(')');
            }
        }
        builder.append(';');
        PreparedStatement ps = conn.prepareStatement(builder.toString());
        int paramIdx = 1;
        ps.setLong(paramIdx++, orderid);
        itemIt = items.iterator();
        while (itemIt.hasNext())
        {
            String item = itemIt.next();
            String size = "";
            for (ItemSize itemSize : ItemSize.values())
            {
                if (item.toUpperCase().startsWith(itemSize.toString()))
                    size = itemSize.toString();
            }
            
            String name = item.substring(size.length() + 1);
            
            ps.setString(paramIdx++, name);
            ps.setString(paramIdx++, size);
        }
        ps.executeUpdate();
        updateTotal(orderid);
        return;
    }
    
    public static void modifyOrder(long orderid, Map<String,Object> values)
        throws SQLException
    {
        Connection conn = ConnectionManager.getConnection();
        List<String> columns = new LinkedList<String>();
        Iterator<String> colIt = values.keySet().iterator();
        while (colIt.hasNext())
        {
            String col = colIt.next();
            switch (col)
            {
            case "active":
            case "empid_took_order":
            case "empid_prepared_order":
            case "empid_delivered_order":
            case "time_order_placed":
            case "time_order_out":
            case "time_order_delivered":
            case "tip":
            case "pay_method":
                columns.add(col);
                break;
            }
        }
        
        if (columns.size() == 0) return;
        
        StringBuilder builder = new StringBuilder();
        builder.append("UPDATE ZaOrder ");
        builder.append("SET ");
        colIt = columns.iterator();
        while (colIt.hasNext())
        {
            String col = colIt.next();
            builder.append(col);
            builder.append("=?");
            if (colIt.hasNext()) builder.append(',');
        }
        builder.append(" WHERE orderid= " + orderid + ";");
        
        PreparedStatement ps = conn.prepareStatement(builder.toString());
        colIt = columns.iterator();
        int paramIdx = 1;
    	Calendar cal = Calendar.getInstance();
        while (colIt.hasNext())
        {
            String col = colIt.next();
            switch (col)
            {
            case "active":
                ps.setBoolean(paramIdx++, (boolean)values.get(col));
                break;
            case "empid_took_order":
                ps.setLong(paramIdx++, (long)values.get(col));
                break;
            case "empid_prepared_order":
                ps.setLong(paramIdx++, (long)values.get(col));
                break;
            case "empid_delivered_order":
                ps.setLong(paramIdx++, (long)values.get(col));
                break;
            case "time_order_placed":
            	cal.setTime((Date) values.get(col));
            	cal.set(Calendar.MILLISECOND, 0);
                ps.setTimestamp(paramIdx++, new Timestamp (cal.getTimeInMillis()));
                break;
            case "time_order_out":
            	cal.setTime((Date) values.get(col));
            	cal.set(Calendar.MILLISECOND, 0);
                ps.setTimestamp(paramIdx++, new Timestamp (cal.getTimeInMillis()));
                break;
            case "time_order_delivered":
            	cal.setTime((Date) values.get(col));
            	cal.set(Calendar.MILLISECOND, 0);
                ps.setTimestamp(paramIdx++, new Timestamp (cal.getTimeInMillis()));
                break;
            case "tip":
                ps.setBigDecimal(paramIdx++, (BigDecimal)values.get(col));
                break;
            case "pay_method":
                ps.setString(paramIdx++, ((PaymentMethod)values.get(col)).toString());
                break;
            }
        }
        ps.executeUpdate();
        return;
    }
    
    public static Map<String,Integer> getOrderItems(long orderid)
        throws SQLException
    {
        Map<String,Integer> items = new HashMap<String,Integer>();
        Connection conn = ConnectionManager.getConnection();
        
        StringBuilder builder = new StringBuilder();
        builder.append("SELECT (itemid,size,quantity) ");
        builder.append("FROM ZaOrderItem ");
        builder.append("WHERE orderid=?;");
        
        PreparedStatement ps = conn.prepareStatement(builder.toString());
        ps.setLong(1, orderid);
        ResultSet rs = ps.executeQuery();
        while (rs.next())
            items.put(String.format("%s %s", rs.getString(2), rs.getString(1)), rs.getInt(3));
        return items;
    }
    
    public static Map<String,Object> getOrderInfo(long orderid, List<String> attributes)
        throws SQLException
    {
        Connection conn = ConnectionManager.getConnection();
        Map<String,Object> values = new HashMap<String,Object>();
        Iterator<String> colIt = attributes.iterator();
        List<String> columns = new LinkedList<String>();
        while (colIt.hasNext())
        {
            String col = colIt.next();
            switch (col)
            {
            case "custid":
            case "order_type":
            case "active":
            case "empid_took_order":
            case "empid_prepared_order":
            case "empid_delivered_order":
            case "time_order_placed":
            case "time_order_out":
            case "time_order_delivered":
            case "subtotal":
            case "tax":
            case "total":
            case "tip":
            case "pay_method":
                columns.add(col);
                break;
            }
        }
        
        if (columns.size() == 0) return values;
        
        StringBuilder builder = new StringBuilder();
        builder.append("SELECT ");
        colIt = columns.iterator();
        while (colIt.hasNext())
        {
            String col = colIt.next();
            builder.append(col);
            if (colIt.hasNext()) builder.append(',');
        }
        builder.append(" FROM Customer ");
        builder.append("WHERE orderid=?;");
        
        PreparedStatement ps = conn.prepareStatement(builder.toString());
        ps.setLong(1, orderid);
        
        ResultSet rs = ps.executeQuery();
        if (!rs.next()) return values;
        colIt = columns.iterator();
        while (colIt.hasNext())
        {
            String col = colIt.next();
            switch (col)
            {
            case "custid":
                values.put(col, rs.getLong(col));
                break;
            case "order_type":
                values.put(col, OrderType.parseOrderType(rs.getString(col)));
                break;
            case "active":
                values.put(col, rs.getBoolean(col));
                break;
            case "empid_took_order":
                values.put(col, rs.getLong(col));
                break;
            case "empid_prepared_order":
                values.put(col, rs.getLong(col));
                break;
            case "empid_delivered_order":
                values.put(col,  rs.getLong(col));
                break;
            case "time_order_placed":
                values.put(col, rs.getTimestamp(col));
                break;
            case "time_order_out":
                values.put(col, rs.getTimestamp(col));
                break;
            case "time_order_delivered":
                values.put(col, rs.getTimestamp(col));
                break;
            case "subtotal":
                values.put(col, rs.getBigDecimal(col));
                break;
            case "tax":
                values.put(col, rs.getBigDecimal(col));
                break;
            case "total":
                values.put(col, rs.getBigDecimal(col));
                break;
            case "tip":
                values.put(col, rs.getBigDecimal(col));
                break;
            case "pay_method":
                values.put(col, PaymentMethod.parsePaymentMethod(rs.getString(col)));
                break;
            }
        }
        return values;
    }
}