package edu.rit.cs.Za;

/**
 * MenuMananger.java
 * Contributor(s):  Jordan Rosario (jar2119@rit.edu)
 */

import java.util.Map;
import java.util.Iterator;
import java.util.List;
import java.util.LinkedList;
import java.util.HashMap;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.math.BigDecimal;

public class MenuManager
{
    public static void addItem(Map<String,Object> menuItem)
        throws SQLException
    {
        Connection conn = ConnectionManager.getConnection();
        StringBuilder builder = new StringBuilder();
        builder.append("INSERT INTO Menu_Item (");
        Iterator<String> colIt = menuItem.keySet().iterator();
        List<String> columns = new LinkedList<String>();
        while (colIt.hasNext())
        {
            String col = colIt.next();
            switch (col)
            {
            case "name":
            case "type":
            case "price":
            case "est_prep_time":
            case "available":
                columns.add(col);
                break;
            }
        }
        
        colIt = columns.iterator();
        while (colIt.hasNext())
        {
            String col = colIt.next();
            builder.append(col);
            if (colIt.hasNext()) builder.append(',');
        }
        builder.append(") VALUES (");
        colIt = columns.iterator();
        while (colIt.hasNext())
        {
            colIt.next();
            builder.append('?');
            if (colIt.hasNext()) builder.append(',');
        }
        builder.append(");");
        
        PreparedStatement ps = conn.prepareStatement(builder.toString());
        
        colIt = columns.iterator();
        int paramIdx = 1;
        while (colIt.hasNext())
        {
            String col = colIt.next();
            switch (col)
            {
            case "name":
                ps.setString(paramIdx++, (String)menuItem.get(col));
                break;
            case "type":
                ps.setString(paramIdx++, ((ItemType)menuItem.get(col)).toString());
                break;
            case "price":
                ps.setBigDecimal(paramIdx++, (BigDecimal)menuItem.get(col));
                break;
            case "est_prep_time":
                ps.setInt(paramIdx++, (int)menuItem.get(col));
                break;
            case "available":
                ps.setBoolean(paramIdx++, (boolean)menuItem.get(col));
                break;
            }
        }
        ps.executeUpdate();
        return;
    }

    public static void setItemAvailability(String itemName, boolean available)
        throws SQLException
    {
        Connection conn = ConnectionManager.getConnection();
        StringBuilder builder = new StringBuilder();
        builder.append("UPDATE Menu_item ");
        builder.append("SET available=? ");
        builder.append("WHERE name=?;");
        PreparedStatement ps = conn.prepareStatement(builder.toString());
        ps.setBoolean(1, available);
        ps.setString(2, itemName);
        ps.executeUpdate();
        return;
    }
    
    public static List<String> getAvailableItems()
        throws SQLException
    {
        List<String> items = new LinkedList<String>();
        Connection conn = ConnectionManager.getConnection();
        StringBuilder builder = new StringBuilder();
        builder.append("SELECT name ");
        builder.append("FROM Menu_Item ");
        builder.append("WHERE available=TRUE;");
        PreparedStatement ps = conn.prepareStatement(builder.toString());
        ResultSet rs = ps.executeQuery();
        while (rs.next())
            items.add(rs.getString(1));
        return items;
    }
    
    public Map<String,Object> getItemInfo(String itemName, List<String> attributes)
        throws SQLException
    {
        Map<String,Object> menuItem = new HashMap<String,Object>();
        Connection conn = ConnectionManager.getConnection();
        StringBuilder builder = new StringBuilder();
        builder.append("SELECT ");
        Iterator<String> colIt = attributes.iterator();
        List<String> columns = new LinkedList<String>();
        while (colIt.hasNext())
        {
            String col = colIt.next();
            switch (col)
            {
            case "type":
            case "price":
            case "est_prep_time":
            case "available":
                columns.add(col);
                break;
            }
        }
        colIt = columns.iterator();
        while (colIt.hasNext())
        {
            String col = colIt.next();
            builder.append(col);
            if (colIt.hasNext()) builder.append(',');
        }
        builder.append(" FROM Menu_Item ");
        builder.append("WHERE name=?;");
        PreparedStatement ps = conn.prepareStatement(builder.toString());
        ResultSet rs = ps.executeQuery();
        if (!rs.next()) return menuItem;
        colIt = columns.iterator();
        while (colIt.hasNext())
        {
            String col = colIt.next();
            switch (col)
            {
            case "type":
                menuItem.put(col, ItemType.parseItemType(rs.getString(col)));
                break;
            case "price":
                menuItem.put(col, rs.getBigDecimal(col));
                break;
            case "est_prep_time":
                menuItem.put(col, rs.getInt(col));
                break;
            case "available":
                menuItem.put(col, rs.getBoolean(col));
                break;
            }
        }
        return menuItem;
    }

    public void modifyItem(String itemName, Map<String,Object> values)
        throws SQLException
    {
        Connection conn = ConnectionManager.getConnection();
        StringBuilder builder = new StringBuilder();
        builder.append("UPDATE Menu_Item ");
        builder.append("SET ");
        Iterator<String> colIt = values.keySet().iterator();
        List<String> columns = new LinkedList<String>();
        while (colIt.hasNext())
        {
            String col = colIt.next();
            switch (col)
            {
            case "type":
            case "price":
            case "est_prep_time":
            case "availability":
                columns.add(col);
                break;
            }
        }
        colIt = columns.iterator();
        while (colIt.hasNext())
        {
            String col = colIt.next();
            builder.append(col);
            builder.append("=?");
            if (colIt.hasNext()) builder.append(',');
        }
        builder.append(" WHERE name=?;");
        PreparedStatement ps = conn.prepareStatement(builder.toString());
        colIt = columns.iterator();
        int paramIdx = 1;
        while (colIt.hasNext())
        {
            String col = colIt.next();
            switch (col)
            {
            case "type":
                ps.setString(paramIdx++, ((ItemType)values.get("type")).toString());
                break;
            case "price":
                ps.setBigDecimal(paramIdx++, (BigDecimal)values.get("price"));
                break;
            case "est_prep_time":
                ps.setInt(paramIdx++, (int)values.get("est_prep_time"));
                break;
            case "available":
                ps.setBoolean(paramIdx++, (boolean)values.get("available"));
                break;
            }
        }
        ps.executeUpdate();
    }
}