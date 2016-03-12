import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.sql.PreparedStatement;

public class PersonTable
{
    public static void createPersonTable(Connection conn)
    {
        try
        {
            String query =  "CREATE TABLE IF NOT EXISTS Person (" +
                            "  personid IDENTITY PRIMARY KEY," +
                            "  first_name VARCHAR(255) NOT NULL," +
                            "  middle_name VARCHAR(255)," +
                            "  last_name VARCHAR(255) NOT NULL," +
                            "  date_of_birth DATE," +
                            "  username VARCHAR(255) NOT NULL," +
                            "  password_hash BINARY(64) NOT NULL," +
                            "  password_salt BINARY(64) NOT NULL," +
                            "  street VARCHAR(255)," +
                            "  city VARCHAR(255)," +
                            "  state CHAR(2)," +
                            "  zip VARCHAR(9)" +
                            ");";
            
            Statement stmt = conn.createStatement();
            stmt.execute(query);
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }
    
    public static void insertPerson(Connection conn, Person person)
    {
        try
        {
            PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO Person (personid, first_name, middle_name, last_name, date_of_birth, username, password_hash, " +
                "password_salt, street, city, state, zip) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)");
            ps.setInt(1, person.personid);
            ps.setString(2, person.first_name);
            ps.setString(3, person.middle_name);
            ps.setString(4, person.last_name);
            ps.setDate(5, person.date_of_birth);
            ps.setString(6, person.username);
            ps.setBytes(7, person.password_hash);
            ps.setBytes(8, person.password_salt);
            ps.setString(9, person.street);
            ps.setString(10, person.city);
            ps.setString(11, person.state);
            ps.setString(12, person.zip);
            ps.executeUpdate();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }   
    }
    
    public static ResultSet queryPersonTable(Connection conn, ArrayList<String> columns, ArrayList<String> whereClauses)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT ");
        
        if(columns.isEmpty())
        {
            sb.append("* ");
        }
        else
        {
            for(int i = 0; i < columns.size(); i++)
            {
                if(i != columns.size() - 1)
                {
                    sb.append(columns.get(i) + ", ");
                }
                else
                {
                    sb.append(columns.get(i) + " ");
                }
            }
        }
        
        sb.append("FROM person ");
        
        if(!whereClauses.isEmpty())
        {
            sb.append("WHERE ");
            for(int i = 0; i < whereClauses.size(); i++)
            {
                if(i != whereClauses.size() -1)
                {
                    sb.append(whereClauses.get(i) + " AND ");
                }
                else
                {
                    sb.append(whereClauses.get(i));
                }
            }
        }
        
        sb.append(";");
        
        //Print it out to verify it made it right
        System.out.println("Query: " + sb.toString());
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
    
    public static void printPersonTable(Connection conn)
    {
        String query = "SELECT * FROM Person;";
        try
        {
            Statement stmt = conn.createStatement();
            ResultSet result = stmt.executeQuery(query);
            
            while(result.next())
            {
                Person person = new Person();
                person.personid = result.getInt(1);
                person.first_name = result.getString(2);
                person.middle_name = result.getString(3);
                person.last_name = result.getString(4);
                person.date_of_birth = result.getDate(5);
                person.username = result.getString(6);
                person.password_hash = result.getBytes(7);
                person.password_salt = result.getBytes(8);
                person.street = result.getString(9);
                person.city = result.getString(10);
                person.state = result.getString(11);
                person.zip = result.getString(12);
                
                System.out.println(person);
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }
}
