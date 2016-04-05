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
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class Queries
{
    public static long getQuantitySold(String itemName, Date start, Date end)
        throws SQLException
    {
        if (end.compareTo(start) < 0)
        {
            Date tmp = start;
            start = end;
            end = tmp;
        }
        
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
        
        /*
         * accordnig to H2 doumentation, SUM aggregate returns sum of INTs (int)
         * as BIGINT (long)
         */
        return rs.getLong(1);
    }
    
    public static Map<String,BigDecimal> getOrderCostStats(Date start, Date end)
        throws SQLException
    {
        if (end.compareTo(start) < 0)
        {
            Date tmp = start;
            start = end;
            end = tmp;
        }
        
        Connection conn = ConnectionManager.getConnection();
        StringBuilder builder = new StringBuilder();
        builder.append("SELECT AVG(subtotal),MIN(subtotal),MAX(subtotal) ");
        builder.append("FROM ZaOrder ");
        builder.append("WHERE time_order_placed BETWEEN ? AND ?;");
        PreparedStatement ps = conn.prepareStatement(builder.toString());
        ps.setDate(1, start);
        ps.setDate(2, end);
        ResultSet rs = ps.executeQuery();
        rs.next();
        
        Map<String,BigDecimal> stats = new HashMap<String,BigDecimal>();
        stats.put("AVG_TOTAL", rs.getBigDecimal(1));
        stats.put("MIN_TOTAL", rs.getBigDecimal(2));
        stats.put("MAX_TOTAL", rs.getBigDecimal(3));
        
        builder.setLength(0);
        builder.append("SELECT subtotal ");
        builder.append("FROM ZaOrder ");
        builder.append("WHERE time_order_placed BETWEEN ? AND ? ");
        builder.append("ORDER BY subtotal ASC;");
        ps = conn.prepareStatement(builder.toString());
        ps.setDate(1, start);
        ps.setDate(2, end);
        rs = ps.executeQuery();
        List<BigDecimal> totals = new ArrayList<BigDecimal>();
        
        while (rs.next())
            totals.add(rs.getBigDecimal(1));
        
        BigDecimal median;
        if (totals.size() % 2 == 0)
        {
            BigDecimal a = totals.get(totals.size() / 2 - 1);
            BigDecimal b = totals.get(totals.size() / 2);
            median = a.add(b).divide(new BigDecimal(2));
        }
        else
            median = totals.get(totals.size() / 2);
        
        median.setScale(2, RoundingMode.HALF_UP);
        
        stats.put("MED_TOTAL", median);
        return stats;
    }
  
}
