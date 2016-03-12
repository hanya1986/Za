import java.sql.*;
import java.util.ArrayList;

public class DbDriver
{
    private Connection conn;
    
    public void createConnection(String location, String user, String password)
    {
        try
        {
            String url = "jdbc:h2:" + location;
            Class.forName("org.h2.Driver");
            conn = DriverManager.getConnection(url, user, password);
        }
        catch (SQLException | ClassNotFoundException e)
        {
            e.printStackTrace();
        }
    }
    
    public Connection getConnection()
    {
        return conn;
    }

    public void closeConnection()
    {
        try
        {
            conn.close();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args)
    {    
        DbDriver dbDriver = new DbDriver();
        String location = ".";
        String user = "username";
        String password = "password";
        dbDriver.createConnection(location, user, password);

        try
        {
            PersonTable.createPersonTable(dbDriver.getConnection());
            PersonTable.insertPerson(dbDriver.getConnection(), Person.createPerson());
            PersonTable.printPersonTable(dbDriver.getConnection());

            System.out.println("\n\nPrint results of SELECT * FROM person");
            ResultSet result = PersonTable.queryPersonTable(dbDriver.getConnection(), new ArrayList<String>(), new ArrayList<String>());            

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
            
            System.out.println("\n\nPrint results of SELECT "
                    + "id, first_name "
                    + "FROM person "
                    + "WHERE first_name = \'Jordan\' "
                    + "AND last_name = \'Rosario\'");
            
            ArrayList<String> columns = new ArrayList<String>();
            columns.add("personid");
            columns.add("first_name");
            columns.add("last_name");
            
            ArrayList<String> whereClauses = new ArrayList<String>();
            whereClauses.add("first_name = \'Jordan\'");
            whereClauses.add("last_name = \'Rosario\'");
            
            ResultSet results2 = PersonTable.queryPersonTable(dbDriver.getConnection(), columns, whereClauses);
            while(results2.next())
            {
                System.out.printf("\tPerson %d: %s %s\n", results2.getInt(1), results2.getString(2), results2.getString(3));
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }
}
