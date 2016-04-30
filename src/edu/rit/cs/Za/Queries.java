package edu.rit.cs.Za;

/**
 * Queries.java
 * Contributor(s):  Jordan Rosario (jar2119@rit.edu)
 *                  Nicholas Marchionda (njm3348@rit.edu)
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
import java.util.Calendar;
import java.util.Collections;
import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Static class that provides methods to obtain information about 'Za operations
 * from the database. These can be used to present reports to the manager about
 * employee, product, and store performance.
 */
public class Queries
{
    /**
     * Gets the quantity of an item sold in a given time period (from midnight
     * on the start date up to and including midnight on the second date, but
     * not a moment later). Does not distinguish between different sizes of the
     * same item.
     * @param itemName  the name of the item (without the size)
     * @param start     the beginning of the time period (inclusive)
     * @param end       the end of the time period (exclusive)
     * @return the number of the item sold in the given time period
     */
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
        builder.append("WHERE itemid=? ");
        builder.append("AND time_order_placed BETWEEN ? AND ? ");
        builder.append("AND ZaOrder.subtotal IS NOT NULL ");
        builder.append("AND ZaOrder.active=FALSE;");
        PreparedStatement ps = conn.prepareStatement(builder.toString());
        ps.setString(1, itemName);
        ps.setDate(2, start);
        ps.setDate(3, end);
        ResultSet rs = ps.executeQuery();
        if (!rs.next()) return 0;
        
        /*
         * according to H2 documentation, SUM aggregate returns the sum of an
         * INT column (int) as a BIGINT (long)
         */
        return rs.getLong(1);
    }
    
    /**
     * Gets order cost statistics for the given time period (from midnight on
     * the start date up to and including midnight on the second date, but not
     * a moment later).
     * @param start the beginning of the time period (inclusive)
     * @param end   the end of the time period (exclusive)
     * @return  map from statistic keys to their values or empty map if no data
     *          Key                 Value
     *          ----------------    --------------------------------
     *          AVG_ORDER_COST      Average cost of an order
     *          MIN_ORDER_COST      Least expensive cost of an order
     *          MAX_ORDER_COST      Most expensive cost of an order
     *          MED_ORDER_COST      Median cost of an order
     *          TOTAL_ORDER_COST    Total revenue from all orders
     */
    public static Map<String,BigDecimal> getOrderCostStats(Date start, Date end)
        throws SQLException
    {
        Map<String,BigDecimal> stats = new HashMap<String,BigDecimal>();
        
        if (end.compareTo(start) < 0)
        {
            Date tmp = start;
            start = end;
            end = tmp;
        }
        
        Connection conn = ConnectionManager.getConnection();
        StringBuilder builder = new StringBuilder();
        builder.append("SELECT AVG(subtotal),MIN(subtotal),MAX(subtotal),SUM(subtotal) ");
        builder.append("FROM ZaOrder ");
        builder.append("WHERE time_order_placed BETWEEN ? AND ? ");
        builder.append("AND active=FALSE ");
        builder.append("AND subtotal IS NOT NULL;");
        PreparedStatement ps = conn.prepareStatement(builder.toString());
        ps.setDate(1, start);
        ps.setDate(2, end);
        ResultSet rs = ps.executeQuery();
        
        if (!rs.next()) return stats;
        
        BigDecimal avgOrderCost = rs.getBigDecimal(1);
        if (!rs.wasNull()) avgOrderCost = avgOrderCost.setScale(2, RoundingMode.HALF_UP);
        BigDecimal minOrderCost = rs.getBigDecimal(2);
        if (!rs.wasNull()) minOrderCost = minOrderCost.setScale(2, RoundingMode.HALF_UP);
        BigDecimal maxOrderCost = rs.getBigDecimal(3);
        if (!rs.wasNull()) maxOrderCost = maxOrderCost.setScale(2, RoundingMode.HALF_UP);
        BigDecimal totalOrderCost = rs.getBigDecimal(4);
        if (!rs.wasNull()) totalOrderCost = totalOrderCost.setScale(2, RoundingMode.HALF_UP);
        
        if (avgOrderCost == null && minOrderCost == null && maxOrderCost == null && totalOrderCost == null)
            return stats;
        
        builder.setLength(0);
        builder.append("SELECT subtotal ");
        builder.append("FROM ZaOrder ");
        builder.append("WHERE time_order_placed BETWEEN ? AND ? ");
        builder.append("AND active=FALSE ");
        builder.append("AND subtotal IS NOT NULL ");
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
            median = a.add(b).divide(new BigDecimal(2), RoundingMode.HALF_UP);
        }
        else median = totals.get(totals.size() / 2);
        
        median = median.setScale(2, RoundingMode.HALF_UP);
        stats.put("MED_ORDER_COST", median);
        stats.put("AVG_ORDER_COST", avgOrderCost);
        stats.put("MIN_ORDER_COST", minOrderCost);
        stats.put("MAX_ORDER_COST", maxOrderCost);
        stats.put("TOTAL_ORDER_COST", totalOrderCost);
        
        return stats;
    }

    /**
     * Gets statistics for revenue on days that revenue was generated during the
     * given time period.
     * @param start the beginning of the time period (inclusive)
     * @param end   the end of the time period (exclusive)
     * @return  map from statistic keys to their values or empty map if no data
     *          Key                 Value
     *          ----------------    ------------------------------------
     *          AVG_DAILY_REV       Average revenue made in a day
     *          MIN_DAILY_REV       Least amount of revenue made in a day
     *          MED_DAILY_REV       Median amount of revenue made in a day
     *          MAX_DAILY_REV       Greatest amount of revenue made in a day
     *          TOTAL_DAILY_REV     Total revenue for entire duration
     * @throws SQLException
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
        builder.append("SELECT AVG(rev) AS avg_daily_rev, ");
        builder.append("MIN(rev) AS min_daily_rev, ");
        builder.append("MAX(rev) AS max_daily_rev, ");
        builder.append("SUM(rev) AS total_daily_rev ");
        builder.append("FROM ");
        builder.append("(SELECT SUM(subtotal) AS rev, ");
        builder.append(" YEAR(time_order_placed) AS order_year, ");
        builder.append( "MONTH(time_order_placed) AS order_month, ");
        builder.append(" DAY(time_order_placed) AS order_day ");
        builder.append(" FROM ZaOrder ");
        builder.append(" WHERE time_order_placed BETWEEN ? AND ? ");
        builder.append(" AND active=FALSE ");
        builder.append(" AND subtotal IS NOT NULL ");
        builder.append(" GROUP BY order_year, order_month, order_day);");
        
        PreparedStatement ps = conn.prepareStatement(builder.toString());
        ps.setDate(1, start);
        ps.setDate(2, end);
        
        ResultSet rs = ps.executeQuery();
        rs.next();
        
        BigDecimal avgDailyOrders = rs.getBigDecimal(1);
        if (!rs.wasNull()) avgDailyOrders = avgDailyOrders.setScale(2, RoundingMode.HALF_UP);
        BigDecimal minDailyOrders = rs.getBigDecimal(2);
        if (!rs.wasNull()) minDailyOrders = minDailyOrders.setScale(2, RoundingMode.HALF_UP);
        BigDecimal maxDailyOrders = rs.getBigDecimal(3);
        if (!rs.wasNull()) maxDailyOrders = maxDailyOrders.setScale(2, RoundingMode.HALF_UP);
        BigDecimal totalDailyOrders = rs.getBigDecimal(4);
        if (!rs.wasNull()) totalDailyOrders = totalDailyOrders.setScale(2, RoundingMode.HALF_UP);
        
        if (avgDailyOrders == null &&
            minDailyOrders == null &&
            maxDailyOrders == null &&
            totalDailyOrders == null)
            return new HashMap<String,BigDecimal>();
        
        builder.setLength(0);
        builder.append("SELECT SUM(subtotal) AS rev, ");
        builder.append("YEAR(time_order_placed) AS order_year, ");
        builder.append("MONTH(time_order_placed) AS order_month, ");
        builder.append("DAY(time_order_placed) AS order_day ");
        builder.append("FROM ZaOrder ");
        builder.append("WHERE time_order_placed BETWEEN ? AND ? ");
        builder.append("AND active=FALSE ");
        builder.append("AND subtotal IS NOT NULL ");
        builder.append("GROUP BY order_year, order_month, order_day ");
        builder.append("ORDER BY rev ASC;");
        
        ps = conn.prepareStatement(builder.toString());
        ps.setDate(1, start);
        ps.setDate(2, end);
        rs = ps.executeQuery();
        
        List<BigDecimal> revenues = new ArrayList<BigDecimal>();
        while (rs.next())
            revenues.add(rs.getBigDecimal(1));
        
        BigDecimal medDailyOrders;
        if (revenues.size() % 2 == 0)
        {
            BigDecimal a = revenues.get(revenues.size() / 2 - 1);
            BigDecimal b = revenues.get(revenues.size() / 2);
            medDailyOrders = a.add(b).divide(new BigDecimal(2), RoundingMode.HALF_UP);
        }
        else medDailyOrders = revenues.get(revenues.size() / 2);
        
        medDailyOrders = medDailyOrders.setScale(2, RoundingMode.HALF_UP);
        
        Map<String,BigDecimal> stats = new HashMap<String,BigDecimal>();
        stats.put("AVG_DAILY_REV", avgDailyOrders);
        stats.put("MIN_DAILY_REV", minDailyOrders);
        stats.put("MED_DAILY_REV", medDailyOrders);
        stats.put("MAX_DAILY_REV", maxDailyOrders);
        stats.put("TOTAL_DAILY_REV", totalDailyOrders);
        
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
    
    public static long getAverageDeliveryTime(long empid) throws SQLException {
        Connection conn = ConnectionManager.getConnection();
        String query = "SELECT avg(DATEDIFF('MS', time_order_out, time_order_delivered)) FROM ZaOrder WHERE empid_delivered_order = ?";
        PreparedStatement ps = conn.prepareStatement(query);
        ps.setLong(1, empid);
        ResultSet results = ps.executeQuery();
        long avgDeliveryTimeMillis = 0;
        if (results.next()) {
        	avgDeliveryTimeMillis = results.getLong(1);
        }
        return avgDeliveryTimeMillis;
    }
    
    public static class DelivererTime {
    	public Long empid;
    	public Long avgDeliveryTimeMillis;
    }
    
    public static List<DelivererTime> getFastestDeliverers() throws SQLException {
        Connection conn = ConnectionManager.getConnection();
        String query = "SELECT empid_delivered_order, avg_delivery_time "
        		+ "FROM (SELECT empid_delivered_order, AVG(DATEDIFF('MS', time_order_out, time_order_delivered)) AS avg_delivery_time "
        		+ "FROM ZaOrder GROUP BY empid_delivered_order) ORDER BY avg_delivery_time";
        PreparedStatement ps = conn.prepareStatement(query);
        ResultSet results = ps.executeQuery();
        List<DelivererTime> fastestDeliverers = new ArrayList<>();
        while (results.next()) {
        	DelivererTime dt = new DelivererTime();
            dt.empid = results.getLong(1);
        	dt.avgDeliveryTimeMillis = results.getLong(2);
        	fastestDeliverers.add(dt);
        }
        return fastestDeliverers;
    }

    
    /**
     * Gets statistics for revenue during months that revenue was generated
     * during the given time period.
     * @param startMonth    month of beginning of time period (inclusive)
     * @param startYear     year of beginning of time period (inclusive)
     * @param endMonth      month of end of time period (inclusive)
     * @param endYear       year of end of time period (inclusive)
     * @return  map from statistic keys to their values or empty map if no data
     *          Key                 Value
     *          ----------------    --------------------------------------------
     *          AVG_MONTHLY_REV     Average revenue made during a month
     *          MIN_MONTHLY_REV     Least amount of revenue made during a month
     *          MED_MONTHLY_REV     Median amount of revenue made during a month
     *          MAX_MONTHLY_REV     Greatest amount of revenue made during a month
     *          TOTAL_MONTHLY_REV   Total revenue for entire duration
     */
    public static Map<String,BigDecimal> getMonthlyRevenueStats(Month startMonth, int startYear, Month endMonth, int endYear)
        throws SQLException
    {
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
        cal.set(Calendar.DAY_OF_MONTH, endDay);
        cal.add(Calendar.DAY_OF_MONTH, 1);
        Date end = new Date(cal.get(Calendar.YEAR) - 1900, cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
        
        if (end.compareTo(start) < 0)
        {
            Date tmp = start;
            start = end;
            end = tmp;
        }
        
        Connection conn = ConnectionManager.getConnection();
        StringBuilder builder = new StringBuilder();
        builder.append("SELECT AVG(rev) AS avg_monthly_rev, ");
        builder.append("MIN(rev) AS min_monthy_rev, ");
        builder.append("MAX(rev) AS max_monthly_rev, ");
        builder.append("SUM(rev) AS total_monthly_rev ");
        builder.append("FROM ");
        builder.append("(SELECT SUM(subtotal) AS rev, ");
        builder.append(" YEAR(time_order_placed) AS order_year, ");
        builder.append( "MONTH(time_order_placed) AS order_month ");
        builder.append(" FROM ZaOrder ");
        builder.append(" WHERE time_order_placed BETWEEN ? AND ? ");
        builder.append(" AND active=FALSE ");
        builder.append(" AND subtotal IS NOT NULL ");
        builder.append(" GROUP BY order_year, order_month);");
        
        PreparedStatement ps = conn.prepareStatement(builder.toString());
        ps.setDate(1, start);
        ps.setDate(2, end);
        
        ResultSet rs = ps.executeQuery();
        rs.next();
        
        BigDecimal avgMonthlyOrders = rs.getBigDecimal(1);
        if (!rs.wasNull()) avgMonthlyOrders = avgMonthlyOrders.setScale(2, RoundingMode.HALF_UP);
        BigDecimal minMonthlyOrders = rs.getBigDecimal(2);
        if (!rs.wasNull()) minMonthlyOrders = minMonthlyOrders.setScale(2, RoundingMode.HALF_UP);
        BigDecimal maxMonthlyOrders = rs.getBigDecimal(3);
        if (!rs.wasNull()) maxMonthlyOrders = maxMonthlyOrders.setScale(2, RoundingMode.HALF_UP);
        BigDecimal totalMonthlyOrders = rs.getBigDecimal(4);
        if (!rs.wasNull()) totalMonthlyOrders = totalMonthlyOrders.setScale(2, RoundingMode.HALF_UP);
        
        if (avgMonthlyOrders == null &&
            minMonthlyOrders == null &&
            maxMonthlyOrders == null &&
            totalMonthlyOrders == null)
            return new HashMap<String,BigDecimal>();
        
        builder.setLength(0);
        builder.append("SELECT SUM(subtotal) AS rev, ");
        builder.append("YEAR(time_order_placed) AS order_year, ");
        builder.append("MONTH(time_order_placed) AS order_month ");
        builder.append("FROM ZaOrder ");
        builder.append("WHERE time_order_placed BETWEEN ? AND ? ");
        builder.append("AND active=FALSE ");
        builder.append("AND subtotal IS NOT NULL ");
        builder.append("GROUP BY order_year, order_month ");
        builder.append("ORDER BY rev ASC;");
        
        ps = conn.prepareStatement(builder.toString());
        ps.setDate(1, start);
        ps.setDate(2, end);
        rs = ps.executeQuery();
        
        List<BigDecimal> revenues = new ArrayList<BigDecimal>();
        while (rs.next())
            revenues.add(rs.getBigDecimal(1));
        
        BigDecimal medMonthlyOrders;
        if (revenues.size() % 2 == 0)
        {
            BigDecimal a = revenues.get(revenues.size() / 2 - 1);
            BigDecimal b = revenues.get(revenues.size() / 2);
            medMonthlyOrders = a.add(b).divide(new BigDecimal(2), RoundingMode.HALF_UP);
        }
        else medMonthlyOrders = revenues.get(revenues.size() / 2);
        
        medMonthlyOrders = medMonthlyOrders.setScale(2, RoundingMode.HALF_UP);
        
        Map<String,BigDecimal> stats = new HashMap<String,BigDecimal>();
        stats.put("AVG_DAILY_REV", avgMonthlyOrders);
        stats.put("MIN_DAILY_REV", minMonthlyOrders);
        stats.put("MED_DAILY_REV", medMonthlyOrders);
        stats.put("MAX_DAILY_REV", maxMonthlyOrders);
        stats.put("TOTAL_DAILY_REV", totalMonthlyOrders);
        
        return stats;
    }
    

    /**
     * Gets statistics for the number of orders placed in a day on days that
     * orders were actually placed during the given time period (from midnight
     * on the start date up to and including midnight on the second date, but
     * not a moment later).
     * @param start the beginning of the time period (inclusive)
     * @param end   the end of the time period (exclusive)
     * @return  map from statistic keys to their values or empty map if no data
     *          Key                     Value
     *          --------------------    ----------------------------------------
     *          AVG_DAILY_ORDERS        Average number of orders placed in a day
     *          MIN_DAILY_ORDERS        Least number of orders placed in a day
     *          MED_DAILY_ORDERS        Median number of orders placed in a day
     *          MAX_DAILY_ORDERS        Greatest number of orders placed in a day
     *          TOTAL_DAILY_ORDERS      Total number of orders placed
     */
    public static Map<String,Float> getDailyOrderStats(Date start, Date end)
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
        builder.append("SELECT AVG(CAST(n_orders AS REAL)) AS avg_daily_orders, ");
        builder.append("MIN(n_orders) AS min_daily_orders, ");
        builder.append("MAX(n_orders) AS max_daily_orders, ");
        builder.append("SUM(n_orders) AS total_daily_orders ");
        builder.append("FROM ");
        builder.append("(SELECT COUNT(*) AS n_orders, ");
        builder.append(" YEAR(time_order_placed) AS order_year, ");
        builder.append( "MONTH(time_order_placed) AS order_month, ");
        builder.append(" DAY(time_order_placed) AS order_day ");
        builder.append(" FROM ZaOrder ");
        builder.append(" WHERE time_order_placed BETWEEN ? AND ? ");
        builder.append(" AND active=FALSE ");
        builder.append(" AND subtotal IS NOT NULL ");
        builder.append(" GROUP BY order_year, order_month, order_day);");
        
        PreparedStatement ps = conn.prepareStatement(builder.toString());
        ps.setDate(1, start);
        ps.setDate(2, end);
        
        ResultSet rs = ps.executeQuery();
        rs.next();
        
        Float avgDailyOrders = rs.getFloat(1);
        Long minDailyOrders = rs.getLong(2);
        Long maxDailyOrders = rs.getLong(3);
        Long totalDailyOrders = rs.getLong(4);
        
        if (avgDailyOrders == null &&
            minDailyOrders == null &&
            maxDailyOrders == null &&
            totalDailyOrders == null)
            return new HashMap<String,Float>();
        
        builder.setLength(0);
        builder.append("SELECT COUNT(*) AS n_orders, ");
        builder.append("YEAR(time_order_placed) AS order_year, ");
        builder.append("MONTH(time_order_placed) AS order_month, ");
        builder.append("DAY(time_order_placed) AS order_day ");
        builder.append("FROM ZaOrder ");
        builder.append("WHERE time_order_placed BETWEEN ? AND ? ");
        builder.append("AND active=FALSE ");
        builder.append("AND subtotal IS NOT NULL ");
        builder.append("GROUP BY order_year, order_month, order_day ");
        builder.append("ORDER BY n_orders ASC;");
        
        ps = conn.prepareStatement(builder.toString());
        ps.setDate(1, start);
        ps.setDate(2, end);
        rs = ps.executeQuery();
        
        List<Long> orders = new ArrayList<Long>();
        while (rs.next())
            orders.add(rs.getLong(1));
        
        float medDailyOrders;
        if (orders.size() % 2 == 0)
        {
            float a = orders.get(orders.size() / 2 - 1);
            float b = orders.get(orders.size() / 2);
            medDailyOrders = (a + b) / 2.0f;
        }
        else medDailyOrders = orders.get(orders.size() / 2);
        
        Map<String,Float> stats = new HashMap<String,Float>();
        stats.put("AVG_DAILY_REV", avgDailyOrders);
        stats.put("MIN_DAILY_REV", (float)(minDailyOrders.longValue()));
        stats.put("MED_DAILY_REV", medDailyOrders);
        stats.put("MAX_DAILY_REV", (float)(maxDailyOrders.longValue()));
        stats.put("TOTAL_DAILY_REV", (float)(totalDailyOrders.longValue()));
        
        return stats;
    }
    

    /**
     * Gets statistics for the number of orders placed in a month in months that
     * orders were actually placed during the given time period.
     * @param startMonth    month of beginning of time period (inclusive)
     * @param startYear     year of beginning of time period (inclusive)
     * @param endMonth      month of end of time period (inclusive)
     * @param endYear       year of end of time period (inclusive)
     * @return  map from statistics keys to their values or empty map if no data
     *          Key                     Value
     *          --------------------    ------------------------------------
     *          AVG_MONTHLY_ORDERS      Average number of orders in a month
     *          MIN_MONTHLY_ORDERS      Least number of orders in a month
     *          MED_MONTHLY_ORDERS      Median number of orders in a month
     *          MAX_MONTHLY_ORDERS      Greatest number of orders in a month
     *          TOTAL_MONTHLY_ORDERS    Total number of orders placed
     */
    public static Map<String,Float> getMonthlyOrderStats(Month startMonth, int startYear, Month endMonth, int endYear)
        throws SQLException
    {
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
        cal.set(Calendar.DAY_OF_MONTH, endDay);
        cal.add(Calendar.DAY_OF_MONTH, 1);
        Date end = new Date(cal.get(Calendar.YEAR) - 1900, cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
        
        Connection conn = ConnectionManager.getConnection();
        StringBuilder builder = new StringBuilder();
        builder.append("SELECT AVG(CAST(n_orders AS REAL)) AS avg_monthly_orders, ");
        builder.append("MIN(n_orders) AS min_monthly_orders, ");
        builder.append("MAX(n_orders) AS max_monthly_orders, ");
        builder.append("SUM(n_orders) AS total_monthly_orders ");
        builder.append("FROM ");
        builder.append("(SELECT COUNT(*) AS n_orders, ");
        builder.append(" YEAR(time_order_placed) AS order_year, ");
        builder.append(" MONTH(time_order_placed) AS order_month ");
        builder.append(" FROM ZaOrder ");
        builder.append(" WHERE time_order_placed BETWEEN ? AND ? ");
        builder.append(" AND active=FALSE ");
        builder.append(" AND subtotal IS NOT NULL ");
        builder.append(" GROUP BY order_year, order_month);");
        
        PreparedStatement ps = conn.prepareStatement(builder.toString());
        ps.setDate(1, start);
        ps.setDate(2, end);
        
        ResultSet rs = ps.executeQuery();
        rs.next();
        
        Float avgMonthlyOrders = rs.getFloat(1);
        Long minMonthlyOrders = rs.getLong(2);
        Long maxMonthlyOrders = rs.getLong(3);
        Long totalMonthlyOrders = rs.getLong(4);
        
        if (avgMonthlyOrders == null &&
            minMonthlyOrders == null &&
            maxMonthlyOrders == null &&
            totalMonthlyOrders == null)
            return new HashMap<String,Float>();
        
        builder.setLength(0);
        builder.append("SELECT COUNT(*) AS n_orders, ");
        builder.append("YEAR(time_order_placed) AS order_year, ");
        builder.append("MONTH(time_order_placed) AS order_month ");
        builder.append("FROM ZaOrder ");
        builder.append("WHERE time_order_placed BETWEEN ? AND ? ");
        builder.append("AND active=FALSE ");
        builder.append("AND subtotal IS NOT NULL ");
        builder.append("GROUP BY order_year, order_month ");
        builder.append("ORDER BY n_orders ASC;");
        
        ps = conn.prepareStatement(builder.toString());
        ps.setDate(1, start);
        ps.setDate(2, end);
        rs = ps.executeQuery();
        
        List<Long> orders = new ArrayList<Long>();
        while (rs.next())
            orders.add(rs.getLong(1));
        
        float medMonthlyOrders;
        if (orders.size() % 2 == 0)
        {
            float a = orders.get(orders.size() / 2 - 1);
            float b = orders.get(orders.size() / 2);
            medMonthlyOrders = (a + b) / 2.0f;
        }
        else medMonthlyOrders = orders.get(orders.size() / 2);
        
        Map<String,Float> stats = new HashMap<String,Float>();
        stats.put("AVG_DAILY_REV", avgMonthlyOrders);
        stats.put("MIN_DAILY_REV", (float)(minMonthlyOrders.longValue()));
        stats.put("MED_DAILY_REV", medMonthlyOrders);
        stats.put("MAX_DAILY_REV", (float)(maxMonthlyOrders.longValue()));
        stats.put("TOTAL_DAILY_REV", (float)(totalMonthlyOrders.longValue()));
        
        return stats;
    }
}