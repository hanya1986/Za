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
import java.sql.Timestamp;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
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

    /*
     * SELECT SUM(subtotal)
     * FROM ZaOrder
     * WHERE time_order_placed BETWEEN ? AND ?
     * 
     */
    
    public static Map<String,BigDecimal> getDailyRevenueStats(Date start, Date end)
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
        builder.append("SELECT time_order_placed,subtotal ");
        builder.append("FROM ZaOrder ");
        builder.append("WHERE time_order_placed BETWEEN ? AND ? ");
        builder.append("ORDER BY time_order_placed;");
        PreparedStatement ps = conn.prepareStatement(builder.toString());
        ps.setDate(1, start);
        ps.setDate(2, end);
        ResultSet rs = ps.executeQuery();
        BigDecimal sumDailyRev = new BigDecimal("0.00");
        int nDays = 1;
        BigDecimal minDailyRev = new BigDecimal("0.00");
        BigDecimal maxDailyRev = new BigDecimal("0.00");
        BigDecimal medDailyRev = new BigDecimal("0.00");
        List<BigDecimal> dailyRevs = new ArrayList<BigDecimal>();
        BigDecimal rev = new BigDecimal("0.00");
        
        Map<String,BigDecimal> stats = new HashMap<String,BigDecimal>();
        
        if (!rs.next()) return stats;
        
        Timestamp ts = rs.getTimestamp(1);
        Date currDate = new Date(ts.getYear(), ts.getMonth(), ts.getDay());
        do
        {
            Timestamp tmOrderPlaced = rs.getTimestamp(1);
            Date dt = new Date(tmOrderPlaced.getYear(), tmOrderPlaced.getMonth(), tmOrderPlaced.getDay());
            if (dt.compareTo(currDate) > 0)
            {
                sumDailyRev = sumDailyRev.add(rev);
                ++nDays;
                
                if (rev.compareTo(minDailyRev) < 0) minDailyRev = rev;
                if (rev.compareTo(maxDailyRev) > 0) maxDailyRev = rev;
                dailyRevs.add(rev);
                rev = new BigDecimal("0.00");
                currDate = dt;
                continue;
            }
            rev = rev.add(rs.getBigDecimal(2));
        } while (rs.next());
        
        if (dailyRevs.size() % 2 == 0)
        {
            BigDecimal a = dailyRevs.get(dailyRevs.size() / 2 - 1);
            BigDecimal b = dailyRevs.get(dailyRevs.size() / 2);
            medDailyRev = a.add(b).divide(new BigDecimal(2));
        }
        else
            medDailyRev = dailyRevs.get(dailyRevs.size() / 2);
        
        BigDecimal avgDailyRev = new BigDecimal("0.00");
        Iterator<BigDecimal> dailyRevIt = dailyRevs.iterator();
        while (dailyRevIt.hasNext())
            avgDailyRev = avgDailyRev.add(dailyRevIt.next());
        avgDailyRev = avgDailyRev.divide(new BigDecimal(nDays));
        
        medDailyRev.setScale(2, RoundingMode.HALF_UP);
        minDailyRev.setScale(2,  RoundingMode.HALF_UP);
        maxDailyRev.setScale(2, RoundingMode.HALF_UP);
        avgDailyRev.setScale(2, RoundingMode.HALF_UP);
        
        stats.put("AVG_DAILY_REV", avgDailyRev);
        stats.put("MIN_DAILY_REV", minDailyRev);
        stats.put("MED_DAILY_REV", medDailyRev);
        stats.put("MAX_DAILY_REV", maxDailyRev);
        
        return stats;
    }
}
