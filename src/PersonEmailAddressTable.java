import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import java.sql.PreparedStatement;

public class PersonEmailAddressTable
{
    public static void createPersonEmailAddressTable(Connection conn)
    {
        try
        {
            String query =  "CREATE TABLE IF NOT EXISTS PersonEmailAddress (" +
                            "  personid INT, " +
                            "  email_addr VARCHAR(255), " +
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
    
    public static void insertPersonEmailAddress(Connection conn, int personid, ArrayList<String> email)
    {
        try
        {
        	StringBuilder sb = new StringBuilder();
        	sb.append("INSERT INTO PersonEmailAddress (personid, email_addr)VALUES");
            for(int i = 0; i < email.size(); i++){
            	String pn = email.get(i);
    			sb.append(String.format("(%d,\'%s\')", 
    					personid, pn));
    			if( i != email.size()-1){
    				sb.append(",");
    			}
    			else{
    				sb.append(";");
    			}
            }
            PreparedStatement ps = conn.prepareStatement(sb.toString());
            ps.executeUpdate();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }   
    }
    
    public static ResultSet queryPersonEmailAddressTable(Connection conn, ArrayList<String> personId)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT * ");
        sb.append("FROM PersonEmailAddress ");
        
        if(!personId.isEmpty())
        {
            sb.append("WHERE ");
            for(int i = 0; i < personId.size(); i++)
            {
            	sb.append("personid = ");
                if(i != personId.size() -1)
                {
                    sb.append(personId.get(i) + " AND ");
                }
                else
                {
                    sb.append(personId.get(i));
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
    
    public static void printPersonEmailAddressTable(Connection conn)
    {
        String query = "SELECT * FROM PersonEmailAddress;";
        try
        {
            Statement stmt = conn.createStatement();
            ResultSet result = stmt.executeQuery(query);
            
            while(result.next())
            {
            	PersonEmailAddress ppn = new PersonEmailAddress();
                ppn.id = result.getInt(1);
                ppn.emails.add(result.getString(2));
                
                
                System.out.println(ppn);
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }
}
