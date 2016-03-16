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
            Person person = Person.createPerson();
            long personid = PersonTable.insertPerson(dbDriver.getConnection(), person);
            
            System.out.println("Printing Person table");
            PersonTable.printPersonTable(dbDriver.getConnection());

            System.out.println("\n\nPrint results of SELECT * FROM Person");
            ResultSet result = PersonTable.queryPersonTable(dbDriver.getConnection(), new ArrayList<String>(), new ArrayList<String>());            

            while(result.next())
            {
                Person p = new Person();
                p.personid = result.getLong(1);
                p.first_name = result.getString(2);
                p.middle_name = result.getString(3);
                p.last_name = result.getString(4);
                p.date_of_birth = result.getDate(5);
                p.username = result.getString(6);
                p.password_hash = result.getBytes(7);
                p.password_salt = result.getBytes(8);
                p.street = result.getString(9);
                p.city = result.getString(10);
                p.state = result.getString(11);
                p.zip = result.getString(12);
                
                System.out.println(person);
            }
            
            System.out.println("\n\nPrint results of SELECT "
                    + "personid, first_name "
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
                System.out.printf("%d %s %s%n", results2.getLong(1), results2.getString(2), results2.getString(3));
            }
            
            //Person p = Person.createPerson();
            PersonPhoneNumberTable.createPersonPhoneNumberTable(dbDriver.getConnection());
            PersonPhoneNumberTable.insertPersonPhoneNumber(dbDriver.getConnection(), personid,
            		PersonPhoneNumber.createPersonPhoneNumber(personid).phoneNumbers);
            PersonPhoneNumberTable.printPersonPhoneNumberTable(dbDriver.getConnection());
            System.out.println("\n\nPrint results of SELECT phone_number FROM PersonEmailAddress");
            result = PersonPhoneNumberTable.queryPersonPhoneNumberTable(dbDriver.getConnection(), personid);
            while(result.next())
            {
            	PersonPhoneNumber ppn = new PersonPhoneNumber();
                ppn.personid = personid;
                ppn.phoneNumbers.add(result.getString(1));
                System.out.println(ppn);
            }
            
            PersonEmailAddressTable.createPersonEmailAddressTable(dbDriver.getConnection());
            PersonEmailAddressTable.insertPersonEmailAddress(dbDriver.getConnection(), personid,
            		PersonEmailAddress.createPersonEmailAddress(personid).emails);
            PersonEmailAddressTable.printPersonEmailAddressTable(dbDriver.getConnection());
            System.out.println("\n\nPrint results of SELECT email_addr FROM PersonEmailAddress");
            result = PersonEmailAddressTable.queryPersonEmailAddressTable(dbDriver.getConnection(), personid);
            while(result.next())
            {
            	PersonEmailAddress pea = new PersonEmailAddress();
            	pea.personid = personid;
            	pea.emails.add(result.getString(1));
                System.out.println(pea);
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }
}
