import java.sql.*;
import java.util.ArrayList;
import java.util.Random;

import org.h2.table.Table;

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
        String location = "C:\\ZADB\\za";
        String user = "username";
        String password = "password";
        dbDriver.createConnection(location, user, password);

        try
        {
            PersonTable.createPersonTable(dbDriver.getConnection());
            Person person = Person.createPerson();
            
            System.out.println("Adding person to table...");
            long personid = PersonTable.insertPerson(dbDriver.getConnection(), person);
            System.out.println("insertPerson returned " + personid);
            
            System.out.println("Printing Person table");
            PersonTable.printPersonTable(dbDriver.getConnection());

            System.out.println("Printing results of SELECT * FROM Person");
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
                
                System.out.println(p);
            }
            
            System.out.println("Printing results of SELECT "
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
            System.out.println("Creating PersonPhoneNumber table...");
            PersonPhoneNumberTable.createPersonPhoneNumberTable(dbDriver.getConnection());
            
            System.out.println("Creating person-phone number association...");
            PersonPhoneNumber ppn = PersonPhoneNumber.createPersonPhoneNumber(personid);
            
            System.out.println("Adding numbers for person with personid " + ppn.personid);
            PersonPhoneNumberTable.insertPersonPhoneNumber(dbDriver.getConnection(), ppn.personid, ppn.phoneNumbers);
            
            System.out.println("Printing PersonPhoneNumber table...");
            PersonPhoneNumberTable.printPersonPhoneNumberTable(dbDriver.getConnection());
            
            System.out.println("Print results of SELECT phone_number FROM PersonEmailAddress WHERE personid=" + personid);
            result = PersonPhoneNumberTable.queryPersonPhoneNumberTable(dbDriver.getConnection(), personid);
            while(result.next())
            {
            	ppn = new PersonPhoneNumber();
                ppn.personid = personid;
                ppn.phoneNumbers.add(result.getString(1));
                System.out.println(ppn);
            }
            
            System.out.println("Creating PersonEmailAddress table...");
            PersonEmailAddressTable.createPersonEmailAddressTable(dbDriver.getConnection());
            
            System.out.println("Creating person-email address association...");
            PersonEmailAddress pea = PersonEmailAddress.createPersonEmailAddress(personid);
            
            System.out.println("Adding addresses for perosn with personid " + personid);
            PersonEmailAddressTable.insertPersonEmailAddress(dbDriver.getConnection(), pea.personid, pea.emails);
            
            System.out.println("Printing PersonEmailAddressTable...");
            PersonEmailAddressTable.printPersonEmailAddressTable(dbDriver.getConnection());
            
            System.out.println("Print results of SELECT email_addr FROM PersonEmailAddress WHERE personid=" + personid);
            result = PersonEmailAddressTable.queryPersonEmailAddressTable(dbDriver.getConnection(), personid);
            while(result.next())
            {
            	pea = new PersonEmailAddress();
            	pea.personid = personid;
            	pea.emails.add(result.getString(1));
                System.out.println(pea);
            }
            
            Person p = new Person();
            p.city = "Rochester";
            p.date_of_birth = new Date(1993 - 1900, 3, 29);
            p.first_name = "Jordan";
            p.last_name = "Rosario";
            p.middle_name = "Alexander";
            p.password_hash = new byte[64];
            p.password_salt = new byte[64];
            p.state = "NY";
            p.street = "161D Perkins Road";
            p.username = "jar";
            Random rand = new Random();
            int i = rand.nextInt(1000);
            p.username += Integer.toString(i);
            p.zip = "14623";
            
            System.out.println("Adding person to table...");
            long pid = PersonTable.insertPerson(dbDriver.getConnection(), p);
            System.out.println("Printing Person table...");
            PersonTable.printPersonTable(dbDriver.getConnection());
            
            CustomerTable.createCustomerTable(dbDriver.getConnection());
            Customer c = new Customer();
            c.cust_id = pid;
            c.reward_pts = 10000;
            
            System.out.println("Adding customer to table...");
            CustomerTable.insertCustomer(dbDriver.getConnection(), c);
            
            System.out.println("Printing Customer table...");
            CustomerTable.printCustomerTable(dbDriver.getConnection());
            
            Credit_CardTable.createCredit_CardTable(dbDriver.getConnection());
            Credit_Card cd = new Credit_Card();
            
            java.util.Random r = new java.util.Random();
            cd.cardNo = "";
            for (i = 0; i < 16; ++i)
            {
                cd.cardNo += Integer.toString(r.nextInt(10));
            }
            cd.secNo = "123";
            Credit_CardTable.insertCredit_Card(dbDriver.getConnection(), cd);
            
            CustomerCardTable.createCustomerCardTable(dbDriver.getConnection());
            CustomerCard card = new CustomerCard();
            card.personId = pid;
            card.cardNumber.add("1234567890123456");
            System.out.println("Adding CustomerCard to table...");
            CustomerCardTable.insertCustomerCard(dbDriver.getConnection(), card.personId, card.cardNumber);
            System.out.println("Printing CustomerCard table...");
            CustomerCardTable.printCustomerCardTable(dbDriver.getConnection());
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }
}
