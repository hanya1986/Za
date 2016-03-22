import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.sql.PreparedStatement;
import java.util.HashMap;

public class CustomerCardTable
{
    public static void createCustomerCardTable(Connection conn)
    {
        try
        {
            String query =  "CREATE TABLE IF NOT EXISTS CustomerCard (" +
                            "  personid BIGINT, " +
                            "  card_number CHAR(16), " +
                            "  PRIMARY KEY (personid, card_number)," +
                            "  FOREIGN KEY (personid) REFERENCES Person(personid)," +
                            "  FOREIGN KEY (card_number) REFERENCES Credit_Card(number)" +
                            ");";
            
            Statement stmt = conn.createStatement();
            stmt.execute(query);
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }
    
    public static void insertCustomerCard(Connection conn, long personid, ArrayList<String> cards)
    {
        try
        {
        	StringBuilder sb = new StringBuilder();
        	sb.append("INSERT INTO CustomerCard (personid, card_number) VALUES ");
        	Iterator<String> cardsIt = cards.iterator();
        	while (cardsIt.hasNext())
        	{
        	    String card_number = cardsIt.next();
    			sb.append(String.format("(%d,\'%s\')", personid, card_number));
    			if(cardsIt.hasNext()) sb.append(", ");
    			else sb.append(";");
            }
            PreparedStatement ps = conn.prepareStatement(sb.toString());
            ps.executeUpdate();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }   
    }
    
    public static ResultSet queryCustomerCardTable(Connection conn, ArrayList<String> columns, ArrayList<String> whereClauses)
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

        sb.append("FROM CustomerCard ");

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
    
    public static void printCustomerCardTable(Connection conn)
    {
        String query = "SELECT * FROM CustomerCard;";
        try
        {
            Statement stmt = conn.createStatement();
            ResultSet result = stmt.executeQuery(query);
            
            HashMap<Long,CustomerCard> customerCardMap = new HashMap<Long,CustomerCard>();
            
            while(result.next())
            {
                long personid = result.getLong(1);
                if (!customerCardMap.containsKey(personid))
                {
                	CustomerCard pea = new CustomerCard();
                    pea.personId = personid;
                    pea.cardNumber.add(result.getString(2));
                    customerCardMap.put(personid, pea);
                }
            }
            
            Iterator<Long> personidIt = customerCardMap.keySet().iterator();
            while (personidIt.hasNext())
                System.out.println(customerCardMap.get(personidIt.next()));
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }
}
