import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.sql.PreparedStatement;
import java.util.Iterator;

public class CustomerTable
{
    public static void createCustomerTable(Connection conn)
    {
        try
        {
            String query =  "CREATE TABLE IF NOT EXISTS Customer (" +
                            "  cust_id    BIGINT," +
                            "  reward_pts INT DEFAULT 0," +
                            "  active     BOOLEAN DEFAULT TRUE," +
                            "  PRIMARY KEY (cust_id)," +
                            "  FOREIGN KEY (cust_id) REFERENCES Person(personid)" +
                            ");";
            
            Statement stmt = conn.createStatement();
            stmt.execute(query);
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }
        
    public static void insertCustomer(Connection conn, Customer customer)
    {
        try
        {
            PreparedStatement ps = conn.prepareStatement("INSERT INTO Customer (cust_id, reward_pts) VALUES (?,?);", Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, customer.cust_id);
            ps.setInt(2, customer.reward_pts);
            ps.executeUpdate();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }   
    }
        
    public static ResultSet queryCustomerTable(Connection conn, ArrayList<String> columns, ArrayList<String> whereClauses)
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
        
        sb.append("FROM Customer ");
        
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
        
    public static void printCustomerTable(Connection conn)
    {
        String query = "SELECT * FROM Customer;";
        try
        {
            Statement stmt = conn.createStatement();
            ResultSet result = stmt.executeQuery(query);
            
            while(result.next())
            {
                Customer customer = new Customer();
                customer.cust_id = result.getLong(1);
                customer.reward_pts = result.getInt(2);
                
                System.out.println(customer);
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }
}
