package edu.rit.cs.Za;

/**
 * Queries.java
 * Contributor(s):  Jordan Rosario (jar2119@rit.edu)
 */

import java.sql.Date;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Queries
{
    public static long getQuantitySold(String itemName, Date start, Date end)
        throws SQLException
    {
        Connection conn = ConnectionManager.getConnection();
        StringBuilder builder = new StringBuilder();
        builder.append("SELECT SUM(quantity) ");
        builder.append("FROM ZaOrder INNER JOIN ZaOrderItem ON ZaOrder.orderid=ZaOrderItem.orderid ");
        builder.append("WHERE itemid=? AND time_order_placed BETWEEN ? AND ?;");
        PreparedStatement ps = conn.prepareStatement(builder.toString());
        ps.setString(1, itemName);
        ps.setDate(2, start);
        ps.setDate(3, end);
        ResultSet rs = ps.executeQuery();
        rs.next();
        
        /* SUM aggregate returns sum of INTs (int) and BIGINT (long) */
        return rs.getLong(1);
    }
}
