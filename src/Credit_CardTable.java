import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.sql.PreparedStatement;
import java.util.Iterator;

public class Credit_CardTable
{
    public static void createCredit_CardTable(Connection conn)
    {
        try
        {
            String query =  "CREATE TABLE IF NOT EXISTS Credit_Card (" +
                    "  number   CHAR(16)," +
                    "  sec_code CHAR(3)," +
                    "  PRIMARY KEY (number)" +
                    ");";

            Statement stmt = conn.createStatement();
            stmt.execute(query);
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    public static void insertCredit_Card(Connection conn, Credit_Card credit_card)
    {
        try
        {
            PreparedStatement ps = conn.prepareStatement("INSERT INTO Credit_Card (number, sec_code) VALUES (?,?);", Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, credit_card.cardNo);
            ps.setString(2, credit_card.secNo);
            ps.executeUpdate();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    public static ResultSet queryCredit_CardTable(Connection conn, ArrayList<String> columns, ArrayList<String> whereClauses)
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

        sb.append("FROM Credit_Card ");

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

    public static void printCredit_CardTable(Connection conn)
    {
        String query = "SELECT * FROM Credit_Card;";
        try
        {
            Statement stmt = conn.createStatement();
            ResultSet result = stmt.executeQuery(query);

            while(result.next())
            {
                Credit_Card credit_card = new Credit_Card();
                credit_card.cardNo = result.getString(1);
                credit_card.secNo = result.getString(2);

                System.out.println(credit_card);
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }
}