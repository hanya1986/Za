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
     * Gets the quantity of an item sold in a given time period. Does not
     * distinguish between different sizes of the same item.
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
     * Gets order cost statistics for the given time period.
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
        
        stats.put("AVG_ORDER_COST", rs.getBigDecimal(1));
        stats.put("MIN_ORDER_COST", rs.getBigDecimal(2));
        stats.put("MAX_ORDER_COST", rs.getBigDecimal(3));
        stats.put("TOTAL_ORDER_COST", rs.getBigDecimal(4));
        
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
            median = a.add(b).divide(new BigDecimal(2));
        }
        else median = totals.get(totals.size() / 2);
        
        median.setScale(2, RoundingMode.HALF_UP);
        stats.put("MED_ORDER_COST", median);
        
        return stats;
    }

    /**
     * Gets statistics for revenue generated on a daily basis for the given time
     * period.
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
        builder.append("SELECT time_order_placed,subtotal ");
        builder.append("FROM ZaOrder ");
        builder.append("WHERE time_order_placed BETWEEN ? AND ? ");
        builder.append("AND active=FALSE ");
        builder.append("AND subtotal IS NOT NULL ");
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
        else medDailyRev = dailyRevs.get(dailyRevs.size() / 2);
        
        BigDecimal avgDailyRev = sumDailyRev.divide(new BigDecimal(nDays));
        
        medDailyRev.setScale(2, RoundingMode.HALF_UP);
        minDailyRev.setScale(2, RoundingMode.HALF_UP);
        maxDailyRev.setScale(2, RoundingMode.HALF_UP);
        avgDailyRev.setScale(2, RoundingMode.HALF_UP);
        sumDailyRev.setScale(2, RoundingMode.HALF_UP);
        
        stats.put("AVG_DAILY_REV", avgDailyRev);
        stats.put("MIN_DAILY_REV", minDailyRev);
        stats.put("MED_DAILY_REV", medDailyRev);
        stats.put("MAX_DAILY_REV", maxDailyRev);
        stats.put("TOTAL_DAILY_REV", sumDailyRev);
        
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
     * Gets statistics for revenue generated on a monthly basis for the given
     * time period.
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
        cal.set(Calendar.DAY_OF_MONTH, endDay);
        cal.add(Calendar.DAY_OF_MONTH, 1);
        Date end = new Date(cal.get(Calendar.YEAR) - 1900, cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
        
        Connection conn = ConnectionManager.getConnection();
        StringBuilder builder = new StringBuilder();
        builder.append("SELECT subtotal,YEAR(time_order_placed) AS order_year,MONTH(time_order_placed) AS order_month ");
        builder.append("FROM ZaOrder ");
        builder.append("WHERE time_order_placed BETWEEN ? AND ? ");
        builder.append("AND active=FALSE ");
        builder.append("AND subtotal IS NOT NULL ");
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
        sumMonthlyRevs.setScale(2, RoundingMode.HALF_UP);
        
        stats.put("AVG_MONTHLY_REV", avgMonthlyRev);
        stats.put("MIN_MONTHLY_REV", minMonthlyRev);
        stats.put("MED_MONTHLY_REV", medMonthlyRev);
        stats.put("MAX_MONTHLY_REV", maxMonthlyRev);
        stats.put("TOTAL_MONTHLY_REV", sumMonthlyRevs);
        
        return stats;
    }

    /**
     * Gets statistics for the number of orders placed on a daily basis for the
     * given time period.
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
            Date tmpDate = start;
            start = end;
            end = tmpDate;
        }
        
        Connection conn = ConnectionManager.getConnection();
        StringBuilder builder = new StringBuilder();
        builder.append("SELECT orderid,YEAR(time_order_placed) AS order_year,");
        builder.append("MONTH(time_order_placed) AS order_month,");
        builder.append("DAY_OF_MONTH(time_order_placed) AS order_day ");
        builder.append("FROM ZaOrder ");
        builder.append("WHERE time_order_placed BETWEEN ? AND ? ");
        builder.append("ORDER BY order_year,order_month,order_day;");
        
        PreparedStatement ps = conn.prepareStatement(builder.toString());
        ps.setDate(1, start);
        ps.setDate(2, end);
        ResultSet rs = ps.executeQuery();
        
        Map<String,Float> stats = new HashMap<String,Float>();
        if (!rs.next()) return stats;
        
        float avgDailyOrders = 0;
        int minDailyOrders = Integer.MAX_VALUE;
        int maxDailyOrders = Integer.MIN_VALUE;
        float medDailyOrders;
        int sumDailyOrders = 0;
        int orders = 0;
        int nDays = 1;
        List<Integer> dailyOrders = new ArrayList<Integer>();
        
        int currDay = rs.getInt(3);
        do
        {
            int day = rs.getInt(3);
            if (day != currDay)
            {
                sumDailyOrders += orders;
                ++nDays;
                if (orders < minDailyOrders) minDailyOrders = orders;
                if (orders > maxDailyOrders) maxDailyOrders = orders;
                dailyOrders.add(orders);
                orders = 0;
                currDay = day;
                continue;
            }
            ++orders;
        } while (rs.next());
        
        Collections.sort(dailyOrders);
        if (dailyOrders.size() % 2 == 0)
        {
            int a = dailyOrders.get(dailyOrders.size() / 2 - 1);
            int b = dailyOrders.get(dailyOrders.size() / 2);
            medDailyOrders = ((float)a + (float)b) / 2.0f;
        }
        else
            medDailyOrders = (float)dailyOrders.get(dailyOrders.size() / 2);
        
        avgDailyOrders = (float)sumDailyOrders / (float)nDays;
        
        stats.put("AVG_DAILY_ORDERS", avgDailyOrders);
        stats.put("MIN_DAILY_ORDERS", (float)minDailyOrders);
        stats.put("MED_DAILY_ORDERS", medDailyOrders);
        stats.put("MAX_DAILY_ORDERS", (float)maxDailyOrders);
        stats.put("TOTAL_DAILY_ORDERS", (float)sumDailyOrders);
        
        return stats;
    }

    /**
     * Gets statistics for the number of orders placed on a monthly basis for
     * the given time period.
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
        Map<String,Float> stats = new HashMap<String,Float>();
        
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
        builder.append("SELECT orderid,YEAR(time_order_placed) AS order_year,");
        builder.append("MONTH(time_order_placed) AS order_month ");
        builder.append("FROM ZaOrder ");
        builder.append("WHERE time_order_placed BETWEEN ? AND ? ");
        builder.append("ORDER BY order_year,order_month;");
        PreparedStatement ps = conn.prepareStatement(builder.toString());
        ps.setDate(1, start);
        ps.setDate(2, end);
        ResultSet rs = ps.executeQuery();
        if (!rs.next()) return stats;
        
        List<Integer> monthlyOrders = new ArrayList<Integer>();
        float avgMonthlyOrders;
        float medMonthlyOrders;
        int minMonthlyOrders = Integer.MAX_VALUE;
        int maxMonthlyOrders = Integer.MIN_VALUE;
        int sumMonthlyOrders = 0;
        int orders = 0;
        int nMonths = 1;
        
        int currMonth = rs.getInt(3);
        do
        {
            int month = rs.getInt(3);
            if (month != currMonth)
            {
                sumMonthlyOrders += orders;
                ++nMonths;
                
                if (orders < minMonthlyOrders) minMonthlyOrders = orders;
                if (orders > maxMonthlyOrders) maxMonthlyOrders = orders;
                monthlyOrders.add(orders);
                orders = 0;
                currMonth = month;
                continue;
            }
            ++orders;
        } while (rs.next());
        
        Collections.sort(monthlyOrders);
        if (monthlyOrders.size() % 2 == 0)
        {
            int a = monthlyOrders.get(monthlyOrders.size() / 2 - 1);
            int b = monthlyOrders.get(monthlyOrders.size() / 2);
            medMonthlyOrders = (float)(a + b) / 2.0f;
        }
        else
            medMonthlyOrders = (float)monthlyOrders.get(monthlyOrders.size() / 2);
        
        avgMonthlyOrders = (float)sumMonthlyOrders / (float)nMonths;
        
        stats.put("AVG_MONTHLY_ORDERS", avgMonthlyOrders);
        stats.put("MIN_MONTHLY_ORDERS", (float)minMonthlyOrders);
        stats.put("MED_MONTHLY_ORDERS", medMonthlyOrders);
        stats.put("MAX_MONTHLY_ORDERS", (float)maxMonthlyOrders);
        stats.put("TOTAL_MONTHLY_ORDERS", (float)sumMonthlyOrders);
        
        return stats;
    }

    public static void main(String[] args)
        throws SQLException
    {
        String db_location = "./ZADB/za";
        String db_path = db_location + ".h2.db";

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
        
        /* test getQuantitySold */
        System.out.println("TESTING Queries.getQuantitySold");
        Date start = new Date(1970 - 1900, Month.JANUARY.value(), 23);
        Date end = new Date(1970 - 1900, Month.AUGUST.value(), 18);
        String[] items = new String[]{
                "Salad", "Dr. Pepper", "New York-Style", "Fennel-Taleggio", "Ginger Ale",
                "Eggplant", "Zucchini", "Chips", "Margherita", "Fries", "Herb", "Puttanesca",
                "Mixed-Vegetable", "Pepsi", "Egg Roll"
        };
        long[] quantities = new long[]{
                3L, 11L, 2L, 4L, 4L, 11L, 7L, 5L, 8L, 8L, 4L, 9L, 2L, 9L, 0L
        };
        int i = 0;
        for (; i < items.length; ++i)
        {
            if (Queries.getQuantitySold(items[i], start, end) != quantities[i])
            {
                System.out.println("FAIL (" + i + ")");
                break;
            }
        }
        if (i == items.length) System.out.println("PASS");
        
        try
        {
            ConnectionManager.closeConnection();
        }
        catch (SQLException ex)
        {
            // it's probably fine
        }
    }
}