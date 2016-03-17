import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.sql.PreparedStatement;
import java.util.Iterator;

public class Menu_ItemTable
{
    public static void createMenu_ItemTable(Connection conn)
    {
        try
        {
            String query =  "CREATE TABLE IF NOT EXISTS Menu_Item (" +
                    "  name   VARCHAR(200)," +
                    "  type BOOLEAN NOT NULL," +
                    "  price DECIMAL(2,2) NOT NULL," +
                    "  est_prep_time INT," +
                    "  PRIMARY KEY (name)" +
                    ");";

            Statement stmt = conn.createStatement();
            stmt.execute(query);
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    public static void insertMenu_Item(Connection conn, Menu_Item menu_item)
    {
        try
        {
            PreparedStatement ps = conn.prepareStatement("INSERT INTO Menu_Item (name, type, price, est_prep_time) VALUES (?,?,?,?);", Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, menu_item.name);
            ps.setBoolean(2, menu_item.type);
            ps.setBigDecimal(3,menu_item.price);
            ps.setInt(4,menu_item.estPrepTime);
            ps.executeUpdate();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    public static ResultSet queryMenu_ItemTable(Connection conn, ArrayList<String> columns, ArrayList<String> whereClauses)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT ");

        if(columns.isEmpty()) sb.append("* ");
        else
        {
            Iterator<String> columnIt = columns.iterator();
            while (columnIt.hasNext())
            {
                String column = columnIt.next();
                sb.append(column);
                if (columnIt.hasNext()) sb.append(", ");
                else sb.append(' ');
            }
        }

        sb.append("FROM Menu_Item ");

        if(!whereClauses.isEmpty())
        {
            sb.append("WHERE ");
            Iterator<String> clauseIt = whereClauses.iterator();
            while (clauseIt.hasNext())
            {
                String clause = clauseIt.next();
                sb.append(clause);
                if(clauseIt.hasNext()) sb.append(" AND ");
            }
        }

        sb.append(";");

        try
        {
            Statement stmt = conn.createStatement();
            return stmt.executeQuery(sb.toString());
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public static void printMenu_ItemTable(Connection conn)
    {
        String query = "SELECT * FROM Menu_Item;";
        try
        {
            Statement stmt = conn.createStatement();
            ResultSet result = stmt.executeQuery(query);

            while(result.next())
            {
                Menu_Item menu_item = new Menu_Item();
                menu_item.name = result.getString(1);
                menu_item.type = result.getBoolean(2);
                menu_item.price = result.getBigDecimal(3);
                menu_item.estPrepTime = result.getInt(4);

                System.out.println(menu_item);
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }
}
