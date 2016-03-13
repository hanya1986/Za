import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import java.sql.PreparedStatement;

public class PersonPhoneNumberTable
{
    public static void createPersonPhoneNUmberTable(Connection conn)
    {
        try
        {
            String query =  "CREATE TABLE IF NOT EXISTS PersonPhoneNumber (" +
                            "  personid INT, " +
                            "  phone_number VARCHAR(20), " +
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
    
    public static void insertPersonPhoneNumber(Connection conn, int personid, ArrayList<String> phoneNumber)
    {
        try
        {
        	StringBuilder sb = new StringBuilder();
        	sb.append("INSERT INTO PersonPhoneNumber (personid, phone_number)VALUES");
            for(int i = 0; i < phoneNumber.size(); i++){
            	String pn = phoneNumber.get(i);
    			sb.append(String.format("(%d,\'%s\')", 
    					personid, pn));
    			if( i != phoneNumber.size()-1){
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
    
    public static ResultSet queryPersonPhoneNumberTable(Connection conn, ArrayList<String> personId)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT * ");
        sb.append("FROM PersonPhoneNumber ");
        
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
    
    public static void printPersonPhoneNumberTable(Connection conn)
    {
        String query = "SELECT * FROM PersonPhoneNumber;";
        try
        {
            Statement stmt = conn.createStatement();
            ResultSet result = stmt.executeQuery(query);
            
            while(result.next())
            {
            	PersonPhoneNumber ppn = new PersonPhoneNumber();
                ppn.id = result.getInt(1);
                ppn.phoneNumbers.add(result.getString(2));
                
                
                System.out.println(ppn);
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }
}
