package edu.rit.cs.Za;

/**
 * Queries.java
 * Contributor(s):  Jordan Rosario (jar2119@rit.edu), Nicholas Marchionda (njm3348@rit.edu)
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
import java.util.Calendar;
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
         * accordnig to H2 documentation, SUM aggregate returns sum of INTs (int)
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
        BigDecimal minDailyRev = new BigDecimal(Long.MAX_VALUE);
        BigDecimal maxDailyRev = new BigDecimal(Long.MIN_VALUE);
        BigDecimal medDailyRev;
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
                
                if (rev.compareTo(minDailyRev) < 0) minDailyRev = new BigDecimal(rev.toString());
                if (rev.compareTo(maxDailyRev) > 0) maxDailyRev = new BigDecimal(rev.toString());
                dailyRevs.add(rev);
                rev = new BigDecimal("0.00");
                currDate = dt;
                continue;
            }
            rev = rev.add(rs.getBigDecimal(2));
        } while (rs.next());
        
        Collections.sort(dailyRevs);
        if (dailyRevs.size() % 2 == 0)
        {
            BigDecimal a = dailyRevs.get(dailyRevs.size() / 2 - 1);
            BigDecimal b = dailyRevs.get(dailyRevs.size() / 2);
            medDailyRev = a.add(b).divide(new BigDecimal(2));
        }
        else
            medDailyRev = dailyRevs.get(dailyRevs.size() / 2);
        
        BigDecimal avgDailyRev = sumDailyRev.divide(new BigDecimal(nDays));
        
        medDailyRev.setScale(2, RoundingMode.HALF_UP);
        minDailyRev.setScale(2, RoundingMode.HALF_UP);
        maxDailyRev.setScale(2, RoundingMode.HALF_UP);
        avgDailyRev.setScale(2, RoundingMode.HALF_UP);
        
        stats.put("AVG_DAILY_REV", avgDailyRev);
        stats.put("MIN_DAILY_REV", minDailyRev);
        stats.put("MED_DAILY_REV", medDailyRev);
        stats.put("MAX_DAILY_REV", maxDailyRev);
        
        return stats;
    }

    //// TODO: 4/4/2016 Test the below queries, not sure if correct yet --Nick
    public static Map<String, Integer> getTopNItems(int N) throws SQLException {
        Connection conn = ConnectionManager.getConnection();
        Map<String, Integer> topNItems = new HashMap<String, Integer>();
        String build = "";
        build += "SELECT name, count(name) LIMIT ?";
        build += "FROM Menu_Item INNER JOIN ZaOrderItem ON Menu_Item.name = ZaOrderItem.name ";
        build += "ORDER BY count(name)";
        PreparedStatement ps = conn.prepareStatement(build);
        ps.setInt(1, N);
        ResultSet results = ps.executeQuery();
        while(results.next()){
            topNItems.put(results.getString(1),results.getInt(2));
        }
        return topNItems;

    }

    public static Map<Integer, Integer> getFrequentCustomers(int N) throws SQLException {
        Connection conn = ConnectionManager.getConnection();
        //Result map, custId key, total number of orders value
        Map<Integer, Integer> customers = new HashMap<Integer, Integer>();
        String build = "";
        build += "SELECT DISTINCT custid, count(custid) LIMIT ?";
        build += "FROM ZaOrder";
        build += "ORDER BY count(custid)";
        PreparedStatement ps = conn.prepareStatement(build);
        ps.setInt(1, N);
        ResultSet results = ps.executeQuery();
        while (results.next()){
            customers.put(results.getInt(1), results.getInt(2));
        }
        return customers;
    }

    public static Map<Integer, Timestamp> getLastNCust(int N) throws SQLException{
        Connection conn = ConnectionManager.getConnection();
        Map<Integer, Timestamp> customers = new HashMap<Integer, Timestamp>();
        String build = "";
        build += "SELECT DISTINCT custid, time_order_placed LIMIT ?";
        build += "FROM ZaOrder";
        build += "ORDER BY time_order_placed";
        PreparedStatement ps = conn.prepareStatement(build);
        ps.setInt(1, N);
        ResultSet results = ps.executeQuery();
        while (results.next()){
            customers.put(results.getInt(1), results.getTimestamp(2));
        }
        return customers;
    }

    public static Map<String,BigDecimal> getMonthlyRevenueStats(Month startMonth, int startYear, Month endMonth, int endYear)
        throws SQLException
    {
        Map<String,BigDecimal> stats = new HashMap<String,BigDecimal>();
        
        if (endYear < startYear)
        {
            int tmpYear = startYear;
            startYear = endYear;
            endYear = tmpYear;
        }
        else if (startYear == endYear)
        {
            if (endMonth.value() < startMonth.value())
            {
                Month tmpMonth = startMonth;
                startMonth = endMonth;
                endMonth = tmpMonth;
            }
        }
        
        Date start = new Date(startYear - 1900, startMonth.value(), 1);
        
        Calendar cal = Calendar.getInstance();
        cal.set(endYear, endMonth.value(), 1);
        int endDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        
        Date end = new Date(endYear - 1900, endMonth.value(), endDay);
        
        Connection conn = ConnectionManager.getConnection();
        StringBuilder builder = new StringBuilder();
        builder.append("SELECT subtotal,YEAR(time_order_placed) AS order_year,MONTH(time_order_placed) AS order_month ");
        builder.append("FROM ZaOrder ");
        builder.append("WHERE time_order_placed BETWEEN ? AND ? ");
        builder.append("ORDER BY order_year,order_month;");
        PreparedStatement ps = conn.prepareStatement(builder.toString());
        ps.setDate(1, start);
        ps.setDate(2, end);
        ResultSet rs = ps.executeQuery();
        if (!rs.next()) return stats;
        
        List<BigDecimal> monthlyRevs = new ArrayList<BigDecimal>();
        
        BigDecimal minMonthlyRev = new BigDecimal(Long.MAX_VALUE);
        BigDecimal maxMonthlyRev = new BigDecimal(Long.MIN_VALUE);
        BigDecimal sumMonthlyRevs = new BigDecimal("0.00");
        BigDecimal rev = new BigDecimal("0.00");
        int nMonths = 1;
        
        int currMonth = rs.getInt(3);
        do
        {
            int month = rs.getInt(3);
            if (month != currMonth)
            {
                sumMonthlyRevs = sumMonthlyRevs.add(rev);
                ++nMonths;
                
                if (rev.compareTo(minMonthlyRev) < 0) minMonthlyRev = new BigDecimal(rev.toString());
                if (rev.compareTo(maxMonthlyRev) > 0) maxMonthlyRev = new BigDecimal(rev.toString());
                monthlyRevs.add(rev);
                rev = new BigDecimal("0.00");
                currMonth = month;
                continue;
            }
            rev = rev.add(rs.getBigDecimal(1));
        } while (rs.next());
        
        BigDecimal medMonthlyRev;
        Collections.sort(monthlyRevs);
        if (monthlyRevs.size() % 2 == 0)
        {
            BigDecimal a = monthlyRevs.get(monthlyRevs.size() / 2 - 1);
            BigDecimal b = monthlyRevs.get(monthlyRevs.size() / 2);
            medMonthlyRev = a.add(b).divide(new BigDecimal(2));
        }
        else
            medMonthlyRev = monthlyRevs.get(monthlyRevs.size() / 2);
        
        BigDecimal avgMonthlyRev = sumMonthlyRevs.divide(new BigDecimal(nMonths));
        
        medMonthlyRev.setScale(2, RoundingMode.HALF_UP);
        minMonthlyRev.setScale(2, RoundingMode.HALF_UP);
        maxMonthlyRev.setScale(2, RoundingMode.HALF_UP);
        avgMonthlyRev.setScale(2, RoundingMode.HALF_UP);
        
        stats.put("AVG_MONTHLY_REV", avgMonthlyRev);
        stats.put("MIN_MONTHLY_REV", minMonthlyRev);
        stats.put("MED_MONTHLY_REV", medMonthlyRev);
        stats.put("MAX_MONTHLY_REV", maxMonthlyRev);
        
        return stats;
    }
}