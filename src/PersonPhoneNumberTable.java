import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.sql.PreparedStatement;

public class PersonPhoneNumberTable
{
    public static void createPersonPhoneNumberTable(Connection conn)
    {
        try
        {
            String query =  "CREATE TABLE IF NOT EXISTS PersonPhoneNumber (" +
                            "  personid     BIGINT, " +
                            "  phone_number VARCHAR(17), " +
                            "  PRIMARY KEY (personid, phone_number)," +
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
    
    public static void insertPersonPhoneNumber(Connection conn, long personid, ArrayList<String> phoneNumbers)
    {
        try
        {
        	StringBuilder sb = new StringBuilder();
        	sb.append("INSERT INTO PersonPhoneNumber (personid, phone_number) VALUES ");
        	Iterator<String> phoneNumberIt = phoneNumbers.iterator();
            while (phoneNumberIt.hasNext())
            {
            	String phone_number = phoneNumberIt.next();
    			sb.append(String.format("(%d,\'%s\')", personid, phone_number));
    			if (phoneNumberIt.hasNext()) sb.append(", ");
    			else sb.append(';');
            }
            PreparedStatement ps = conn.prepareStatement(sb.toString());
            ps.executeUpdate();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }   
    }
    
    public static ResultSet queryPersonPhoneNumberTable(Connection conn, long personid)
    {
        try
        {
            Statement stmt = conn.createStatement();
            return stmt.executeQuery(String.format("SELECT phone_number FROM PersonPhoneNumber WHERE personid=%d", personid));
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        return null;
    }
    
    public static void printPersonPhoneNumberTable(Connection conn)
    {
        String query = "SELECT * FROM PersonPhoneNumber;";
        try
        {
            Statement stmt = conn.createStatement();
            ResultSet result = stmt.executeQuery(query);
            
            HashMap<Long,PersonPhoneNumber> personPhoneMap = new HashMap<Long,PersonPhoneNumber>();
            
            while(result.next())
            {
                long personid = result.getLong(1);
                if (!personPhoneMap.containsKey(personid))
                {
                    PersonPhoneNumber ppn = new PersonPhoneNumber();
                    ppn.personid = personid;
                    personPhoneMap.put(personid, ppn);
                }
                personPhoneMap.get(personid).phoneNumbers.add(result.getString(2));
            }
            
            Iterator<Long> personidIt = personPhoneMap.keySet().iterator();
            while (personidIt.hasNext())
                System.out.println(personPhoneMap.get(personidIt.next()));
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }
}
