import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.sql.PreparedStatement;
import java.util.HashMap;

public class PersonEmailAddressTable
{
    public static void createPersonEmailAddressTable(Connection conn)
    {
        try
        {
            String query =  "CREATE TABLE IF NOT EXISTS PersonEmailAddress (" +
                            "  personid   BIGINT, " +
                            "  email_addr VARCHAR(256), " +
                            "  PRIMARY KEY (personid, email_addr)," +
                            "  FOREIGN KEY (personid) REFERENCES Person(personid)" +
                            ");";
            
            Statement stmt = conn.createStatement();
            stmt.execute(query);
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }
    
    public static void insertPersonEmailAddress(Connection conn, long personid, ArrayList<String> email)
    {
        try
        {
        	StringBuilder sb = new StringBuilder();
        	sb.append("INSERT INTO PersonEmailAddress (personid, email_addr) VALUES ");
        	Iterator<String> emailIt = email.iterator();
        	while (emailIt.hasNext())
        	{
        	    String email_addr = emailIt.next();
    			sb.append(String.format("(%d,\'%s\')", personid, email_addr));
    			if(emailIt.hasNext()) sb.append(", ");
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
    
    public static ResultSet queryPersonEmailAddressTable(Connection conn, long personid)
    {
        try
        {
            Statement stmt = conn.createStatement();
            return stmt.executeQuery(String.format("SELECT email_addr FROM PersonEmailAddress WHERE personid=%d", personid));
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        return null;
    }
    
    public static void printPersonEmailAddressTable(Connection conn)
    {
        String query = "SELECT * FROM PersonEmailAddress;";
        try
        {
            Statement stmt = conn.createStatement();
            ResultSet result = stmt.executeQuery(query);
            
            HashMap<Long,PersonEmailAddress> personEmailMap = new HashMap<Long,PersonEmailAddress>();
            
            while(result.next())
            {
                long personid = result.getLong(1);
                if (!personEmailMap.containsKey(personid)) personEmailMap.put(personid, new PersonEmailAddress());
                personEmailMap.get(personid).emails.add(result.getString(2));
            }
            
            Iterator<Long> personidIt = personEmailMap.keySet().iterator();
            while (personidIt.hasNext())
                System.out.println(personEmailMap.get(personidIt.next()));
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }
}
