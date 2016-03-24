/**
 * OrderManager.java
 * Contributor(s):  Jordan Rosario (jar2119@rit.edu)
 */

package edu.rit.cs.Za;

import java.util.Map;
import java.util.Iterator;
import java.util.List;
import java.util.LinkedList;
import java.util.Set;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class OrderManager
{   
    public static BigDecimal TAX_RATE = new BigDecimal("0.08");
    
    public static long createOrder(long custid, OrderType orderType, Map<String,Integer> items)
        throws SQLException
    {
        Connection conn = ConnectionManager.getConnection();
        String sql = "INSERT INTO ZaOrder (custid,order_type) VALUES (?,?);";
        PreparedStatement ps = conn.prepareStatement(sql, new String[]{ "orderid" });
        ps.setLong(1, custid);
        ps.setString(2, orderType.toString());
        ps.executeUpdate();
        ResultSet generatedKey = ps.getGeneratedKeys();
        generatedKey.next();
        long orderid = generatedKey.getLong(1);
        
        Iterator<String> itemIt = items.keySet().iterator();
        StringBuilder builder = new StringBuilder();
        builder.append("INSERT INTO ZaOrderItem (orderid,itemid,quantity) ");
        builder.append("VALUES(?,?,?);");
        ps = conn.prepareStatement(builder.toString());
        while (itemIt.hasNext())
        {
            String item = itemIt.next();
            ps.setLong(1, orderid);
            ps.setString(2, item);
            ps.setInt(3, items.get(item));
            ps.executeUpdate();
        }
        
        BigDecimal subtotal = new BigDecimal("0.00");
        builder.setLength(0);
        builder.append("SELECT price ");
        builder.append("FROM Menu_Item ");
        builder.append("WHERE name IN (");
        builder.append("    SELECT itemid ");
        builder.append("    FROM ZaOrderItem ");
        builder.append("    WHERE orderid=?);");
        ps = conn.prepareStatement(builder.toString());
        ps.setLong(1, orderid);
        ResultSet rs = ps.executeQuery();
        
        while (rs.next())
        {
            BigDecimal price = rs.getBigDecimal(1);
            subtotal = subtotal.add(price);
        }
        
        BigDecimal tax = subtotal.multiply(TAX_RATE);
        BigDecimal total = subtotal.add(tax);
        subtotal.setScale(2, RoundingMode.HALF_UP);
        tax.setScale(2, RoundingMode.HALF_UP);
        total.setScale(2, RoundingMode.HALF_UP);
        
        builder.append("UPDATE ZaOrder ");
        builder.append("SET subtotal=?,tax=?,total=? ");
        builder.append("WHERE orderid=?;");
        ps = conn.prepareStatement(builder.toString());
        ps.setBigDecimal(1, subtotal);
        ps.setBigDecimal(2, tax);
        ps.setBigDecimal(3, total);
        ps.setLong(4, orderid);
        ps.executeUpdate();
        
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
        /* get items already in order */
        Connection conn = ConnectionManager.getConnection();
        StringBuilder builder = new StringBuilder();
        builder.append("SELECT itemid,quantity ");
        builder.append("FROM ZaOrderItem ");
        builder.append("WHERE orderid=?;");
        PreparedStatement ps = conn.prepareStatement(builder.toString());
        ps.setLong(1, orderid);
        ResultSet rs = ps.executeQuery();
        
        builder.setLength(0);
        builder.append("UPDATE ZaOrderItem ");
        builder.append("SET quantity=? ");
        builder.append("WHERE orderid=?;");
        PreparedStatement update = conn.prepareStatement(builder.toString());
        
        builder.setLength(0);
        builder.append("INSERT INTO ZaOrderItem (orderid,itemid,quantity) ");
        builder.append("VALUES (?,?,?);");
        PreparedStatement insert = conn.prepareStatement(builder.toString());
        
        Set<String> itemSet = items.keySet();
        while (rs.next())
        {
            String itemName = rs.getString(1);
            int currentQty = rs.getInt(2);
            int additionalQty = items.get(itemName);
            if (itemSet.contains(itemName))
            {
                /* item already in order; increase quantity */
                update.setInt(1, currentQty + additionalQty);
                update.setLong(2, orderid);
                update.executeUpdate();
            }
            else
            {
                /* item not yet in order; add specified amount */
                insert.setLong(1, orderid);
                insert.setString(2, itemName);
                insert.setInt(3, additionalQty);
                insert.executeUpdate();
            }
        }
    }
}