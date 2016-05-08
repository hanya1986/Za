package edu.rit.cs.Za;

/**
 * ProfileManager.java
 * Contributor(s):  Jordan Rosario (jar2119@rit.edu)
 * 					Jeremy Friedman(jsf6410@g.rit.edu)
 */

import java.sql.Connection;
import java.sql.SQLException;
import java.text.DateFormat;
import java.sql.PreparedStatement;
import java.sql.Date;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.List;
import java.util.LinkedList;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.math.BigDecimal;

/**
 * A static class that provides methods for the creation and management of
 * profiles for people associated with 'Za somehow, whether they are customers
 * or employees.
 */
public class ProfileManager
{
    /* SHA-512 procudes 64 byte digest */
    private static final int DIGEST_BYTE_SIZE = 64;
    
    /* indicates that a username does not exist in the Person table */
    public static final long USERNAME_NOT_IN_TABLE = -1L;
    
    /* indicates that a password provided with a username is incorrect */
    public static final long INCORRECT_PASSWORD = -2L;
    
    /* secure random number generator for generating salts */
    private static final SecureRandom sr = new SecureRandom();
    
    /**
     * Helper method for computing the SHA-512 digest of a combination of a
     * password and a salt.
     * @param password  a user's password
     * @param salt      the salt associated with the user's password
     * @return the SHA-512 digest of the password concatenated with the salt
     * @throws NoSuchAlgorithmException if SHA-512 not supported by
     *                                  implementation
     */
    private static byte[] computeHash(byte[] password, byte[] salt)
        throws NoSuchAlgorithmException
    {   
        /*
         * To compute the password hash to store in the database, first
         * concatenate it with the salt, which we have decided to make the same
         * size as the digest. Then, compute the SHA-512 digest of that whole
         * thing, which will be 512 bits or 64 bytes long. Finally, return the
         * digest, which will be stored as the password hash along with the
         * salt.
         */
        byte[] passConcatSalt = new byte[password.length + DIGEST_BYTE_SIZE];
        int idx = 0;
        for (int passIdx = 0; passIdx < password.length; ++passIdx)
            passConcatSalt[idx++] = password[passIdx];
        for (int saltIdx = 0; saltIdx < DIGEST_BYTE_SIZE; ++saltIdx)
            passConcatSalt[idx++] = salt[saltIdx];
        
        MessageDigest md = MessageDigest.getInstance("SHA-512");
        md.update(passConcatSalt);
        return md.digest();
    }
    
    /**
     * Given a combination of a username and password, this method determines
     * whether the pair of credentials is valid.
     * @param username  a user's username
     * @param password  the password to check
     * @return  USERNAME_NOT_IN_TABLE if the usernmae does not exist in Person,
     *          INCORRECT_PASSWORD if the password is incorrect, or the ID
     *          number of the person if the credentials are correct
     * @throws NoSuchAlgorithmException if SHA-512 not supported by
     *                                  implementation
     */
    public static long validateCredentials(String username, String password)
        throws SQLException, NoSuchAlgorithmException
    {
        Connection conn = ConnectionManager.getConnection();
        
        String sql =    "SELECT personid, password_hash, password_salt " +
                        "FROM Person " +
                        "WHERE username=?";
        
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, username);
        ResultSet result = ps.executeQuery();
        if (!result.next()) return USERNAME_NOT_IN_TABLE;
        
        byte[] passwordHash = result.getBytes("password_hash");
        byte[] passwordSalt = result.getBytes("password_salt");
        byte[] digest = computeHash(password.getBytes(), passwordSalt);
        
        if (!Arrays.equals(passwordHash, digest)) return INCORRECT_PASSWORD;
        return result.getLong("personid");
    }
    
    /**
     * Determines whether an ID number is associated with a customer or an
     * employee.
     * @param personid  a person's ID number
     * @return  PersonType.NOT_A_PERSON if the ID number is invalid, otherwise
     *          either PersonType.CUSTOMER or PersonType.EMPLOYEE as appropriate
     */
    public static PersonType getPersonType(long personid)
        throws SQLException
    {
        Connection conn = ConnectionManager.getConnection();
        PreparedStatement ps = conn.prepareStatement("SELECT cust_id FROM Customer WHERE cust_id=?;");
        ps.setLong(1, personid);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) return PersonType.CUSTOMER;
        ps = conn.prepareStatement("SELECT empid FROM Employee WHERE empid=?;");
        ps.setLong(1, personid);
        rs = ps.executeQuery();
        if (rs.next()) return PersonType.EMPLOYEE;
        return PersonType.NOT_A_PERSON;
    }
    
    /**
     * Helper method to create a corresponding Person record whenever a Customer
     * or an Employee is added. Person is a total generalization, so each Person
     * record must have either a corresponding Customer record or corresponding
     * Employee record, which are disjoint entity sets themselves.
     * @param values    map from Person attribute names to attribute values
     * @param password  the password to securely store for the new Person
     * @return the unique ID number for the new Person
     * @throws NoSuchAlgorithmException if SHA-512 not supported by the
     *                                  implementation
     */
    private static long createPerson(Map<String,Object> values, String password)
        throws SQLException, NoSuchAlgorithmException
    {
        Connection conn = ConnectionManager.getConnection();
        StringBuilder builder = new StringBuilder();
        builder.append("INSERT INTO Person (");
        Iterator<String> colIt = values.keySet().iterator();
        List<String> columns = new LinkedList<String>();
        while (colIt.hasNext())
        {
            String col = colIt.next();
            switch (col)
            {
            case "first_name":
            case "middle_name":
            case "last_name":
            case "date_of_birth":
            case "username":
            case "street":
            case "city":
            case "state":
            case "zip":
                columns.add(col);
                break;
            }
        }
        colIt = columns.iterator();
        while (colIt.hasNext())
        {
            String col = colIt.next();
            builder.append(col);
            builder.append(',');
        }
        builder.append("password_hash,password_salt");
        builder.append(") VALUES (");
        colIt = columns.iterator();
        while (colIt.hasNext())
        {
            colIt.next();
            builder.append('?');
            builder.append(',');
        }
        builder.append("?,?);");
        
        PreparedStatement ps = conn.prepareStatement(builder.toString(), new String[]{ "personid" });
        colIt = columns.iterator();
        int paramIdx = 1;
        while (colIt.hasNext())
        {
            String col = colIt.next();
            switch (col)
            {
            case "first_name":
                ps.setString(paramIdx++, (String)values.get("first_name"));
                break;
            case "middle_name":
                ps.setString(paramIdx++, (String)values.get("middle_name"));
                break;
            case "last_name":
                ps.setString(paramIdx++,  (String)values.get("last_name"));
                break;
            case "date_of_birth":
            	ps.setDate(paramIdx++, new Date(((java.util.Date)values.get("date_of_birth")).getTime()));
                break;
            case "username":
                ps.setString(paramIdx++, (String)values.get("username"));
                break;
            case "street":
                ps.setString(paramIdx++, (String)values.get("street"));
                break;
            case "city":
                ps.setString(paramIdx++, (String)values.get("city"));
                break;
            case "state":
            	ps.setString(paramIdx++, values.get("state").toString());
                break;
            case "zip":
                ps.setString(paramIdx++, (String)values.get("zip"));
                break;
            }
        }
        
        byte[] passwordSalt = new byte[DIGEST_BYTE_SIZE];
        sr.nextBytes(passwordSalt);
        byte[] passwordHash = computeHash(password.getBytes(), passwordSalt);
        ps.setBytes(paramIdx++, passwordHash);
        ps.setBytes(paramIdx++, passwordSalt);
        
        ps.executeUpdate();
        ResultSet generatedKey = ps.getGeneratedKeys();
        generatedKey.next();
        return generatedKey.getLong(1);
    }
    
    /**
     * Creates a new Customer and corresponding Person record (Person is a total
     * generalization, and Customer and Employee are disjoint entity sets).
     * @param values    map from Customer attribute names to attribute values
     * @param password  the password to securely store for the new Customer
     * @return the unique ID number for the new Customer
     * @throws NoSuchAlgorithmException if SHA-512 not supported by the
     *                                  implementation
     */
    public static long createCustomer(Map<String,Object> values, String password)
        throws SQLException, NoSuchAlgorithmException
    {
        long personid = createPerson(values, password);
        Connection conn = ConnectionManager.getConnection();
        StringBuilder builder = new StringBuilder();
        builder.append("INSERT INTO Customer (");
        builder.append("cust_id");
        if (values.containsKey("reward_pts")) builder.append(",reward_pts");
        builder.append(") VALUES (");
        builder.append('?');
        if (values.containsKey("reward_pts")) builder.append(",?");
        builder.append(");");
        PreparedStatement ps = conn.prepareStatement(builder.toString());
        ps.setLong(1, personid);
        if (values.containsKey("reward_pts")) ps.setInt(2, (int)values.get("reward_pts"));
        ps.executeUpdate();
        return personid;
    }
    
    /**
     * Creates a new Employee and corresponding Person record (Person is a total
     * generalization, and Employee and Customer are disjoint entity sets).
     * @param values    map from Employee attribute names to attribute values
     * @param password  the password to securely store for the new Employee
     * @return the unique ID number for the new Employee
     * @throws NoSuchAlgorithmException if SHA-512 not supported by the
     *                                  implementation
     */
    public static long createEmployee(Map<String,Object> values, String password)
        throws SQLException, NoSuchAlgorithmException
    {
        long personid = createPerson(values,password);
        Connection conn = ConnectionManager.getConnection();
        StringBuilder builder = new StringBuilder();
        builder.append("INSERT INTO Employee (");
        Iterator<String> colIt = values.keySet().iterator();
        List<String> columns = new LinkedList<String>();
        columns.add("empid");
        while (colIt.hasNext())
        {
            String col = colIt.next();
            switch (col)
            {
            case "hourly_rate":
            case "ssn":
            case "hours_per_week":
            case "date_hired":
            case "job_title":
                columns.add(col);
                break;
            }
        }
        colIt = columns.iterator();
        while (colIt.hasNext())
        {
            String col = colIt.next();
            builder.append(col);
            if (colIt.hasNext()) builder.append(',');
        }
        builder.append(") VALUES (");
        colIt = columns.iterator();
        while (colIt.hasNext())
        {
            colIt.next();
            builder.append('?');
            if (colIt.hasNext()) builder.append(',');
        }
        builder.append(");");
        
        PreparedStatement ps = conn.prepareStatement(builder.toString());
        colIt = columns.iterator();
        int paramIdx = 1;
        while (colIt.hasNext())
        {
            String col = colIt.next();
            switch (col)
            {
            case "empid":
                ps.setLong(paramIdx++, personid);
                break;
            case "hourly_rate":
                ps.setBigDecimal(paramIdx++, (BigDecimal)values.get("hourly_rate"));
                break;
            case "ssn":
                ps.setString(paramIdx++, (String)values.get("ssn"));
                break;
            case "hours_per_week":
                ps.setFloat(paramIdx++, (float)values.get("hours_per_week"));
                break;
            case "date_hired":
                ps.setDate(paramIdx++, new Date(((java.util.Date)values.get("date_hired")).getTime()));
                break;
            case "date_terminated":
            	if(values.get("date_terminated") == null){
            		ps.setDate(paramIdx++, null);
            	}else{
            		ps.setDate(paramIdx++, new Date(((java.util.Date)values.get("date_terminated")).getTime()));
            	}
                break;
            case "job_title":
                ps.setString(paramIdx++, (String)values.get("job_title"));
                break;
            }
        }
        ps.executeUpdate();
        return personid;
    }

    /**
     * Changes a Person's password by securely generating a new salt value and
     * securely storing the new combination of the hashed password and the salt.
     * @param personid  the unique ID number of the Person whose password needs
     *                  to be changed
     * @param password  the new password
     * @throws NoSuchAlgorithmException if SHA-512 not supported by the
     *                                  implementation
     */
    public static void changePassword(long personid, String password)
        throws NoSuchAlgorithmException, SQLException
    {
        byte[] salt = new byte[DIGEST_BYTE_SIZE];
        sr.nextBytes(salt);
        byte[] passwordHash = computeHash(password.getBytes(), salt);
        
        Connection conn = ConnectionManager.getConnection();
        
        StringBuilder builder = new StringBuilder();
        builder.append("UPDATE Person ");
        builder.append("SET password_hash=?,password_salt=? ");
        builder.append("WHERE personid=?;");
        PreparedStatement ps = conn.prepareStatement(builder.toString());
        ps.setBytes(1, passwordHash);
        ps.setBytes(2, salt);
        ps.setLong(3, personid);
        ps.executeUpdate();
    }
    
    /**
     * Whenever the attributes common to Customers and Employees *i.e., to
     * Persons) need to be changed, they are changed by a call to this helper
     * method.
     * @param personid  the unique ID number identifying the Person whose
     *                  attributes need updating
     * @param values    map from Person attribute names to new values
     */
    private static void modifyPerson(long personid, Map<String,Object> values)
        throws SQLException
    {
        Connection conn = ConnectionManager.getConnection();
        
        Iterator<String> colIt = values.keySet().iterator();
        List<String> columns = new LinkedList<String>();
        while (colIt.hasNext())
        {
            String col = colIt.next();
            switch (col)
            {
            case "first_name":
            case "middle_name":
            case "last_name":
            case "date_of_birth":
            case "username":
            case "street":
            case "city":
            case "state":
            case "zip":
                columns.add(col);
                break;
            }
        }
        
        if (columns.size() == 0) return;
        
        StringBuilder builder = new StringBuilder();
        builder.append("UPDATE Person ");
        builder.append("SET ");
        colIt = columns.iterator();
        while (colIt.hasNext())
        {
            String col = colIt.next();
            builder.append(col);
            builder.append("=?");
            if (colIt.hasNext()) builder.append(',');
        }
        builder.append(" WHERE personid=?;");
        
        PreparedStatement ps = conn.prepareStatement(builder.toString());
        
        colIt = columns.iterator();
        int paramIdx = 1;
        while (colIt.hasNext())
        {
            String col = colIt.next();
            switch (col)
            {
            case "first_name":
                ps.setString(paramIdx++, (String)values.get("first_name"));
                break;
            case "middle_name":
                ps.setString(paramIdx++, (String)values.get("middle_name"));
                break;
            case "last_name":
                ps.setString(paramIdx++, (String)values.get("last_name"));
                break;
            case "date_of_birth":
            	ps.setDate(paramIdx++, new Date(((java.util.Date)values.get("date_of_birth")).getTime()));
                break;
            case "username":
                ps.setString(paramIdx++, (String)values.get("username"));
                break;
            case "street":
                ps.setString(paramIdx++, (String)values.get("street"));
                break;
            case "city":
                ps.setString(paramIdx++, (String)values.get("city"));
                break;
            case "state":
            	ps.setString(paramIdx++, values.get("state").toString());
                break;
            case "zip":
                ps.setString(paramIdx++, (String)values.get("zip"));
                break;
            }
        }
        ps.setLong(paramIdx++, personid);
        ps.executeUpdate();
    }
    
    /**
     * Modifies the attributes of a specific Customer.
     * @param cust_id   the unique ID number identifying the Customer
     * @param values    map from attribute names to new values
     */
    public static void modifyCustomer(long cust_id, Map<String,Object> values)
        throws SQLException
    {
        modifyPerson(cust_id, values);
        
        Connection conn = ConnectionManager.getConnection();
        
        Iterator<String> colIt = values.keySet().iterator();
        List<String> columns = new LinkedList<String>();
        while (colIt.hasNext())
        {
            String col = colIt.next();
            switch (col)
            {
            case "reward_pts":
            case "active":
                columns.add(col);
                break;
            }
        }
        
        if (columns.size() == 0) return;
        
        StringBuilder builder = new StringBuilder();
        builder.append("UPDATE Customer ");
        builder.append("SET ");
        colIt = columns.iterator();
        while (colIt.hasNext())
        {
            String col = colIt.next();
            builder.append(col);
            builder.append("=?");
            if (colIt.hasNext()) builder.append(',');
        }
        
        builder.append(" WHERE cust_id=?;");
        
        PreparedStatement ps = conn.prepareStatement(builder.toString());
        colIt = columns.iterator();
        int paramIdx = 1;
        while (colIt.hasNext())
        {
            String col = colIt.next();
            switch (col)
            {
            case "reward_pts":
                ps.setInt(paramIdx++, (int)values.get("reward_pts"));
                break;
            case "active":
                ps.setBoolean(paramIdx++, (boolean)values.get("active"));
                break;
            }
        }
        ps.setLong(paramIdx++, cust_id);
        ps.executeUpdate();
    }
    
    /**
     * Modifies the attributes of a specific Employee.
     * @param empid     the unique ID number identifying the Employee
     * @param values    map from attribute names to new values
     */
    public static void modifyEmployee(long empid, Map<String,Object> values)
        throws SQLException
    {
        modifyPerson(empid, values);
        
        Connection conn = ConnectionManager.getConnection();
        
        Iterator<String> colIt = values.keySet().iterator();
        List<String> columns = new LinkedList<String>();
        while (colIt.hasNext())
        {
            String col = colIt.next();
            switch (col)
            {
            case "hourly_rate":
            case "ssn":
            case "hours_per_week":
            case "date_hired":
            case "date_terminated":
            case "job_title":
                columns.add(col);
                break;
            }
        }
        
        if (columns.size() == 0) return;
        
        StringBuilder builder = new StringBuilder();
        builder.append("UPDATE Employee ");
        builder.append("SET ");
        colIt = columns.iterator();
        while (colIt.hasNext())
        {
            String col = colIt.next();
            builder.append(col);
            builder.append("=?");
            if (colIt.hasNext()) builder.append(',');
        }
        
        builder.append(" WHERE empid=?;");
        
        PreparedStatement ps = conn.prepareStatement(builder.toString());
        colIt = columns.iterator();
        int paramIdx = 1;
        while (colIt.hasNext())
        {
            String col = colIt.next();
            switch (col)
            {
            case "hourly_rate":
                ps.setBigDecimal(paramIdx++, (BigDecimal)values.get("hourly_rate"));
                break;
            case "ssn":
                ps.setString(paramIdx++, (String)values.get("ssn"));
                break;
            case "hours_per_week":
                ps.setFloat(paramIdx++, (float)values.get("hours_per_week"));
                break;
            case "date_hired":
            	ps.setDate(paramIdx++, new Date(((java.util.Date)values.get("date_hired")).getTime()));
                break;
            case "date_terminated":
            	if(values.get("date_terminated") == null){
            		ps.setDate(paramIdx++, null);
            	}else{
            		ps.setDate(paramIdx++, new Date(((java.util.Date)values.get("date_terminated")).getTime()));
            	}
                break;
            case "job_title":
                ps.setString(paramIdx++, (String)values.get("job_title"));
                break;
            }
        }
        ps.setLong(paramIdx++, empid);
        ps.executeUpdate();
    }
    
    /**
     * Associates a customer with a credit card. If the credit card does not
     * exist in the credit card table, it is first added. Credit cards are weak
     * entities in a many-to-many relationship with customer entities.
     * @param cust_id       unique ID number for the Customer using the card
     * @param cardNumber    the 16-digit card number
     * @param securityCode  the 3-digit security code on the back of the card
     * @param expMonth      the month of the card expiration date
     * @param expYear       the year of the card expiration date
     */
    public static void addCreditCard(long cust_id, String cardNumber, String securityCode, Month expMonth, int expYear)
        throws SQLException
    {
        Connection conn = ConnectionManager.getConnection();
        StringBuilder builder = new StringBuilder();
        builder.append("SELECT number ");
        builder.append("FROM Credit_Card ");
        builder.append("WHERE number=?;");
        
        PreparedStatement ps = conn.prepareStatement(builder.toString());
        ps.setString(1, cardNumber);
        ResultSet rs = ps.executeQuery();
        if (!rs.next())
        {
            builder.setLength(0);
            builder.append("INSERT INTO Credit_Card (number,sec_code,exp_month,exp_year) ");
            builder.append("VALUES (?,?,?,?);");
            ps = conn.prepareStatement(builder.toString());
            ps.setString(1, cardNumber);
            ps.setString(2, securityCode);
            ps.setInt(3, expMonth.value());
            ps.setInt(4, expYear);
            ps.executeUpdate();
        }
        
        builder.setLength(0);
        builder.append("INSERT INTO CustomerCard (personid,card_number) ");
        builder.append("VALUES (?,?);");
        ps = conn.prepareStatement(builder.toString());
        ps.setLong(1, cust_id);
        ps.setString(2, cardNumber);
        ps.executeUpdate();
    }
    
    /**
     * Removes the association between a customer and a credit card. If after
     * breaking the association, no customer is associated with the card, then
     * the card is removed from the credit card table because credit cards are
     * weak entities (i.e., they cannot exist in the 'Za database if no one is
     * using them).
     * @param cust_id       the unique ID number of the customer no longer using
     *                      the card  
     * @param cardNumber    the 16-digit card number
     */
    public static void removeCreditCard(long cust_id, String cardNumber)
        throws SQLException
    {
        Connection conn = ConnectionManager.getConnection();
        StringBuilder builder = new StringBuilder();
        
        builder.append("DELETE FROM CustomerCard ");
        builder.append("WHERE personid=? AND card_number=?;");
        PreparedStatement ps = conn.prepareStatement(builder.toString());
        ps.setLong(1, cust_id);
        ps.setString(2, cardNumber);
        ps.executeUpdate();
        
        builder.setLength(0);
        builder.append("SELECT EXISTS (");
        builder.append("    SELECT card_number ");
        builder.append("    FROM CustomerCard ");
        builder.append("    WHERE card_number=?);");
        ps = conn.prepareStatement(builder.toString());
        ps.setString(1, cardNumber);
        ResultSet rs = ps.executeQuery();
        rs.next();
        if (rs.getBoolean(1)) return;
        
        builder.setLength(0);
        builder.append("DELETE FROM Credit_Card ");
        builder.append("WHERE number=?;");
        ps = conn.prepareStatement(builder.toString());
        ps.setString(1, cardNumber);
        ps.executeUpdate();
        return;
    }

    /**
     * Retrieves a list of credit cards associated with a customer's account.
     * @param cust_id   the unique ID number of the customer
     * @return a list of the customer's credit cards
     */
    public static List<CreditCard> getCreditCards(long cust_id)
        throws SQLException
    {
        List<CreditCard> cards = new LinkedList<CreditCard>();
        Connection conn = ConnectionManager.getConnection();
        StringBuilder builder = new StringBuilder();
        builder.append("SELECT number,sec_code,exp_month,exp_year ");
        builder.append("FROM Credit_Card INNER JOIN CustomerCard ");
        builder.append("ON Credit_Card.number=CustomerCard.card_number ");
        builder.append("WHERE personid=?;");
        PreparedStatement ps = conn.prepareStatement(builder.toString());
        ps.setLong(1, cust_id);
        ResultSet rs = ps.executeQuery();
        while (rs.next())
        {
            CreditCard card = new CreditCard();
            card.cardNumber = rs.getString(1);
            card.securityCode = rs.getString(2);
            int expirationMonth = rs.getInt(3);
            for (Month month : Month.values())
            {
                if (month.value() == expirationMonth)
                {
                    card.expirationMonth = month;
                    break;
                }
            }
            card.expirationYear = rs.getInt(4);
            cards.add(card);
        }
        return cards;
    }
    
    /**
     * Adds a phone number to a person's list of phone numbers.
     * @param personid      the unique ID number of the person adding a phone
     *                      number
     * @param phoneNumber   the phone number to add
     */
    public static void addPhoneNumber(long personid, String phoneNumber)
        throws SQLException
    {
        Connection conn = ConnectionManager.getConnection();
        StringBuilder builder = new StringBuilder();
        builder.append("INSERT INTO PersonPhoneNumber (personid,phone_number) ");
        builder.append("VALUES (?,?);");
        PreparedStatement ps = conn.prepareStatement(builder.toString());
        ps.setLong(1, personid);
        ps.setString(2, phoneNumber);
        ps.executeUpdate();
        return;
    }
    
    /**
     * Removes a phone number from a person's list of phone numbers.
     * @param personid      the unique ID number of the person removing a phone
     *                      number
     * @param phoneNumber   the phone number to remove
     */
    public static void removePhoneNumber(long personid, String phoneNumber)
        throws SQLException
    {
        Connection conn = ConnectionManager.getConnection();
        StringBuilder builder = new StringBuilder();
        builder.append("DELETE FROM PersonPhoneNumber ");
        builder.append("WHERE personid=? AND phone_number=?;");
        PreparedStatement ps = conn.prepareStatement(builder.toString());
        ps.setLong(1, personid);
        ps.setString(2, phoneNumber);
        ps.executeUpdate();
        return;
    }
    
    /**
     * Retrieves a list of the phone numbers associated with a person's account.
     * @param personid  the unique ID number of the person
     * @return a list of phone numbers associated with a person's account.
     */
    public static List<String> getPhoneNumbers(long personid)
        throws SQLException
    {
        List<String> numbers = new LinkedList<String>();
        Connection conn = ConnectionManager.getConnection();
        StringBuilder builder = new StringBuilder();
        builder.append("SELECT phone_number ");
        builder.append("FROM PersonPhoneNumber ");
        builder.append("WHERE personid=?;");
        PreparedStatement ps = conn.prepareStatement(builder.toString());
        ps.setLong(1, personid);
        ResultSet rs = ps.executeQuery();
        while (rs.next())
            numbers.add(rs.getString(1));
        return numbers;
    }

    /**
     * Adds an email address to a person's list of email addresses.
     * @param personid      the unique ID number of the person adding an email
     *                      address
     * @param emailAddress  the email address to add
     */
    public static void addEmailAddress(long personid, String emailAddress)
        throws SQLException
    {
        Connection conn = ConnectionManager.getConnection();
        StringBuilder builder = new StringBuilder();
        builder.append("INSERT INTO PersonEmailAddress (personid,email_addr) ");
        builder.append("VALUES (?,?);");
        PreparedStatement ps = conn.prepareStatement(builder.toString());
        ps.setLong(1, personid);
        ps.setString(2, emailAddress);
        ps.executeUpdate();
        return;
    }
    
    /**
     * Removes an email address from a person's list of email addresses.
     * @param personid      the unique ID number of the person removing an email
     *                      address
     * @param emailAddress  the email address to remove
     */
    public static void removeEmailAddress(long personid, String emailAddress)
        throws SQLException
    {
        Connection conn = ConnectionManager.getConnection();
        StringBuilder builder = new StringBuilder();
        builder.append("DELETE FROM PersonEmailAddress ");
        builder.append("WHERE personid=? AND email_addr=?;");
        PreparedStatement ps = conn.prepareStatement(builder.toString());
        ps.setLong(1, personid);
        ps.setString(2, emailAddress);
        ps.executeUpdate();
        return;
    }
    
    /**
     * Retrieves a list of the email addresses associated with a person's
     * account.
     * @param personid  the unique ID number of the person
     * @return a list of email addresses associated with a person's account.
     */
    public static List<String> getEmailAddresses(long personid)
        throws SQLException
    {
        List<String> numbers = new LinkedList<String>();
        Connection conn = ConnectionManager.getConnection();
        StringBuilder builder = new StringBuilder();
        builder.append("SELECT email_addr ");
        builder.append("FROM PersonEmailAddress ");
        builder.append("WHERE personid=?;");
        PreparedStatement ps = conn.prepareStatement(builder.toString());
        ps.setLong(1, personid);
        ResultSet rs = ps.executeQuery();
        while (rs.next())
            numbers.add(rs.getString(1));
        return numbers;
    }
    
    /**
     * Helper method to retrieve the values of those attributes common to
     * customers and employees.
     * @param personid      the unique ID of the person of interest
     * @param attributes    list of desired attributes whose values to retrieve
     * @return a map from person attribute names to their values
     */
    private static Map<String,Object> getPersonInfo(long personid, List<String> attributes)
        throws SQLException
    {
        Map<String,Object> values = new HashMap<String,Object>();
        if (attributes.size() == 0) return values;
        
        Connection conn = ConnectionManager.getConnection();
        Iterator<String> colIt = attributes.iterator();
        List<String> columns = new LinkedList<String>();
        while (colIt.hasNext())
        {
            String col = colIt.next();
            switch (col)
            {
            case "first_name":
            case "middle_name":
            case "last_name":
            case "date_of_birth":
            case "username":
            case "street":
            case "city":
            case "state":
            case "zip":
                columns.add(col);
                break;
            }
        }
        
        if (columns.size() == 0) return values;
        
        StringBuilder builder = new StringBuilder();
        builder.append("SELECT ");
        colIt = columns.iterator();
        while (colIt.hasNext())
        {
            String col = colIt.next();
            builder.append(col);
            if (colIt.hasNext()) builder.append(',');
        }
        builder.append(" FROM Person ");
        builder.append("WHERE personid=?;");
        
        PreparedStatement ps = conn.prepareStatement(builder.toString());
        ps.setLong(1, personid);
        ResultSet rs = ps.executeQuery();
        if (!rs.next()) return values;
        colIt = attributes.iterator();
        while (colIt.hasNext())
        {
            String col = colIt.next();
            switch (col)
            {
            case "first_name":
                values.put(col, rs.getString(col));
                break;
            case "middle_name":
                values.put(col, rs.getString(col));
                break;
            case "last_name":
                values.put(col, rs.getString(col));
                break;
            case "date_of_birth":
                values.put(col, rs.getDate(col));
                break;
            case "username":
                values.put(col, rs.getString(col));
                break;
            case "street":
                values.put(col, rs.getString(col));
                break;
            case "city":
                values.put(col, rs.getString(col));
                break;
            case "state":
                values.put(col, rs.getString(col));
                break;
            case "zip":
                values.put(col, rs.getString(col));
                break;
            }
        }
        return values;
    }
    
    /**
     * Retrieves the desired information about a customer.
     * @param cust_id       the unique ID number identifying the customer in
     *                      question
     * @param attributes    list of desired attributes whose values to retrieve
     * @return map from attribute names to their current values
     */
    public static Map<String,Object> getCustomerInfo(long cust_id, List<String> attributes)
        throws SQLException
    {
        Map<String,Object> values = getPersonInfo(cust_id, attributes);
        
        Connection conn = ConnectionManager.getConnection();
        
        Iterator<String> colIt = attributes.iterator();
        List<String> columns = new LinkedList<String>();
        while (colIt.hasNext())
        {
            String col = colIt.next();
            switch (col)
            {
            case "reward_pts":
            case "active":
                columns.add(col);
                break;
            }
        }
        
        if (columns.size() == 0) return values;
        
        StringBuilder builder = new StringBuilder();
        builder.append("SELECT ");
        
        colIt = columns.iterator();
        while (colIt.hasNext())
        {
            String col = colIt.next();
            builder.append(col);
            if (colIt.hasNext()) builder.append(',');
        }
        builder.append(" FROM Customer ");
        builder.append("WHERE cust_id=?;");
        
        PreparedStatement ps = conn.prepareStatement(builder.toString());
        ps.setLong(1, cust_id);
        ResultSet rs = ps.executeQuery();
        if (!rs.next()) return values;
        
        colIt = columns.iterator();
        while (colIt.hasNext())
        {
            String col = colIt.next();
            switch (col)
            {
            case "reward_pts":
                values.put(col, rs.getInt(col));
                break;
            case "active":
                values.put(col, rs.getBoolean(col));
                break;
            }
        }
        
        return values;
    }
    
    /**
     * Retrieves the desired information about an employee.
     * @param empid         the unique ID number identifying the employee in
     *                      question
     * @param attributes    list of desired attributes whose values to retrieve
     * @return map from attribute names to their current values
     */
    public static Map<String,Object> getEmployeeInfo(long empid, List<String> attributes)
        throws SQLException
    {
        Map<String,Object> values = getPersonInfo(empid, attributes);
        
        Connection conn = ConnectionManager.getConnection();
        
        Iterator<String> colIt = attributes.iterator();
        List<String> columns = new LinkedList<String>();
        while (colIt.hasNext())
        {
            String col = colIt.next();
            switch (col)
            {
            case "hourly_rate":
            case "ssn":
            case "hours_per_week":
            case "date_hired":
            case "date_terminated":
            case "job_title":
                columns.add(col);
                break;
            }
        }
        
        if (columns.size() == 0) return values;
        
        StringBuilder builder = new StringBuilder();
        builder.append("SELECT ");
        
        colIt = columns.iterator();
        while (colIt.hasNext())
        {
            String col = colIt.next();
            builder.append(col);
            if (colIt.hasNext()) builder.append(',');
        }
        builder.append(" FROM Employee ");
        builder.append("WHERE empid=?;");
        
        PreparedStatement ps = conn.prepareStatement(builder.toString());
        ps.setLong(1, empid);
        ResultSet rs = ps.executeQuery();
        if (!rs.next()) return values;
        
        colIt = columns.iterator();
        while (colIt.hasNext())
        {
            String col = colIt.next();
            switch (col)
            {
            case "hourly_rate":
                values.put(col, rs.getBigDecimal(col));
                break;
            case "ssn":
                values.put(col, rs.getString(col));
                break;
            case "hours_per_week":
                values.put(col, rs.getFloat(col));
                break;
            case "date_hired":
                values.put(col, rs.getDate(col));
                break;
            case "date_terminated":
                values.put(col, rs.getDate(col));
                break;
            case "job_title":
                values.put(col, rs.getString(col));
                break;
            }
        }
        return values;
    }

    /**
     * Getting all the employee IDs.
     * 
     * @return values
     * @throws SQLException
     */
    public static List<Long> getAllEmployeeID() throws SQLException{
    	List<Long> values = new ArrayList<Long>();
    	Connection conn = ConnectionManager.getConnection();
    	StringBuilder query = new StringBuilder();
    	query.append("SELECT empid FROM Employee;");
    	PreparedStatement ps = conn.prepareStatement(query.toString());
    	ResultSet rs = ps.executeQuery();
    	while(rs.next()){
    		values.add(rs.getLong("empid"));
    	}
    	return values;
    }
    
    /**
     * Gets the ID number associated with the person with the specified
     * username.
     * @param username  the username of the person whose ID number is wanted
     * @return  USERNAME_NOT_IN_TABLE if the username does not exist in the
     *          Person table, or the unique ID number of the person
     */
    public static long getPersonID(String username)
        throws SQLException
    {
        Connection conn = ConnectionManager.getConnection();
        String sql = "SELECT personid FROM Person WHERE username=?;";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, username);
        ResultSet rs = ps.executeQuery();
        if (!rs.next()) return USERNAME_NOT_IN_TABLE;
        else return rs.getLong(1);
    }
}