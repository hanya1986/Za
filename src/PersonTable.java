import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.sql.PreparedStatement;
/**
 * Class to make and manipulate the person table
 * @author scj
 *
 */
public class PersonTable
{
    /**
     * Create the person table with the given attributes
     * 
     * @param conn: the database connection to work with
     */
    public static void createPersonTable(Connection conn){
        try {
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
            
            /**
             * Create a query and execute
             */
            Statement stmt = conn.createStatement();
            stmt.execute(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Adds a single person to the database
     * 
     * @param conn
     * @param id
     * @param fName
     * @param lName
     * @param MI
     */
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
    
    /**
     * Makes a query to the person table 
     * with given columns and conditions
     * 
     * @param conn
     * @param columns: columns to return
     * @param whereClauses: conditions to limit query by
     * @return
     */
    public static ResultSet queryPersonTable(Connection conn,
                                             ArrayList<String> columns,
                                             ArrayList<String> whereClauses){
        StringBuilder sb = new StringBuilder();
        
        /**
         * Start the select query
         */
        sb.append("SELECT ");
        
        /**
         * If we gave no columns just give them all to us
         * 
         * other wise add the columns to the query
         * adding a comma top seperate
         */
        if(columns.isEmpty()){
            sb.append("* ");
        }
        else{
            for(int i = 0; i < columns.size(); i++){
                if(i != columns.size() - 1){
                    sb.append(columns.get(i) + ", ");
                }
                else{
                    sb.append(columns.get(i) + " ");
                }
            }
        }
        
        /**
         * Tells it which table to get the data from
         */
        sb.append("FROM person ");
        
        /**
         * If we gave it conditions append them
         * place an AND between them
         */
        if(!whereClauses.isEmpty()){
            sb.append("WHERE ");
            for(int i = 0; i < whereClauses.size(); i++){
                if(i != whereClauses.size() -1){
                    sb.append(whereClauses.get(i) + " AND ");
                }
                else{
                    sb.append(whereClauses.get(i));
                }
            }
        }
        
        /**
         * close with semi-colon
         */
        sb.append(";");
        
        //Print it out to verify it made it right
        System.out.println("Query: " + sb.toString());
        try {
            /**
             * Execute the query and return the result set
             */
            Statement stmt = conn.createStatement();
            return stmt.executeQuery(sb.toString());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * Queries and print the table
     * @param conn
     */
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
