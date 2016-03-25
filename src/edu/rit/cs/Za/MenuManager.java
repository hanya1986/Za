package edu.rit.cs.Za;

/**
 * MenuMananger.java
 * Contributor(s):  Jordan Rosario (jar2119@rit.edu)
 */

import java.util.Map;
import java.util.Iterator;
import java.util.List;
import java.util.LinkedList;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

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
                
            case "price":
            case "est_prep_time":
            case "available":
            }
        }
    }
}