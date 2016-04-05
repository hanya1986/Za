package edu.rit.cs.Za;

/**
 * ProfileManager.java
 * Contributor(s):  Jordan Rosario (jar2119@rit.edu)
 */

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.Date;
import java.sql.ResultSet;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.List;
import java.util.LinkedList;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.math.BigDecimal;

public class ProfileManager
{
    private static final int DIGEST_BYTE_SIZE = 64;
    public static final long USERNAME_NOT_IN_TABLE = -1L;
    public static final long INCORRECT_PASSWORD = -2L;
    
    private static final SecureRandom sr = new SecureRandom();
    
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
    
    public PersonType getPersonType(long personid)
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
    
    /*
     * THIS METHOD WAS INTENTIONALLY MADE PRIVATE.
     * WHEN createCustomer OR createEmployee IS CALLED, THIS METHOD IS CALLED IN
     * TURN TO INSERT A NEW PERSON WHOSE personid IS USED IN EITHER THE Customer
     * OR THE Employee TABLE, RESPECTIVELY. 
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
                ps.setDate(paramIdx++, (Date)values.get("date_of_birth"));
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
                ps.setString(paramIdx++, ((State)values.get("state")).toString());
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
    
    /*
     * DO NOT PROVIDE A PASSWORD HASH OR SALT IN values. IF ONE OR BOTH ARE
     * PROVIDED, THEY WILL BE IGNORED. A SALT WILL BE CREATED FOR COMPUTING THE
     * HASH OF THE PASSWORD PROVIDED AS AN ARGUMENT.
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
    
    /*
     * DO NOT PROVIDE A PASSWORD HASH OR SALT IN values. IF ONE OR BOTH ARE
     * PROVIDED, THEY WILL BE IGNORED. A SALT WILL BE CREATED FOR COMPUTING THE
     * HASH OF THE PASSWORD PROVIDED AS AN ARGUMENT.
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
        while (colIt.hasNext())
        {
            String col = colIt.next();
            switch (col)
            {
            case "empid":
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
                ps.setLong(paramIdx++, (long)values.get("empid"));
                break;
            case "hourly_rate":
                ps.setBigDecimal(paramIdx++, (BigDecimal)values.get("hourly_rate"));
                break;
            case "ssn":
                ps.setInt(paramIdx++, (int)values.get("ssn"));
                break;
            case "hours_per_week":
                ps.setInt(paramIdx++, (int)values.get("hours_per_week"));
                break;
            case "date_hired":
                ps.setDate(paramIdx++, (Date)values.get("date_hired"));
                break;
            case "date_terminated":
                ps.setDate(paramIdx++, (Date)values.get("date_terminated"));
                break;
            case "job_title":
                ps.setString(paramIdx++, (String)values.get("job_title"));
                break;
            }
        }
        ps.executeUpdate();
        return personid;
    }

    /*
     * AS IS THE CASE WITH ALL OF THE METHODS IN THIS CLASS THAT TAKE A personid
     * PARAMETER, THE personid PARAMETER REALLY REFERS TO cust_id IF THE PERSON
     * IS A CUSTOMER AND empid IF THE PERSON IS AN EMPLOYEE.
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
                ps.setDate(paramIdx++, (Date)values.get("date_of_birth"));
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
                ps.setString(paramIdx++, ((State)values.get("state")).toString());
                break;
            case "zip":
                ps.setString(paramIdx++, (String)values.get("zip"));
                break;
            }
        }
        ps.setLong(paramIdx++, personid);
        ps.executeUpdate();
    }
    
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
                ps.setBoolean(paramIdx++, (boolean)values.get("ssn"));
                break;
            case "hours_per_week":
                ps.setFloat(paramIdx++, (float)values.get("hours_per_week"));
                break;
            case "date_hired":
                ps.setDate(paramIdx++, (Date)values.get("date_hired"));
                break;
            case "date_terminated":
                ps.setDate(paramIdx++, (Date)values.get("date_terminated"));
                break;
            case "job_title":
                ps.setString(paramIdx++, (String)values.get("job_title"));
                break;
            }
        }
        ps.setLong(paramIdx++, empid);
        ps.executeUpdate();
    }
    
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
        builder.append("VALUES (?,?):");
        ps = conn.prepareStatement(builder.toString());
        ps.setLong(1, cust_id);
        ps.setString(2, cardNumber);
        ps.executeUpdate();
    }
    
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
        if (rs.next()) return;
        
        builder.setLength(0);
        builder.append("DELETE FROM CreditCard ");
        builder.append("WHERE number=?;");
        ps = conn.prepareStatement(builder.toString());
        ps.setString(1, cardNumber);
        ps.executeUpdate();
        return;
    }

    public static List<CreditCard> getCreditCards(long cust_id)
        throws SQLException
    {
        List<CreditCard> cards = new LinkedList<CreditCard>();
        Connection conn = ConnectionManager.getConnection();
        StringBuilder builder = new StringBuilder();
        builder.append("SELECT number,sec_code,exp_month,exp_year");
        builder.append("FROM Credit_Card INNER JOIN CustomerCard");
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
    
    public static void removeEmailAddress(long personid, String emailAddress)
        throws SQLException
    {
        Connection conn = ConnectionManager.getConnection();
        StringBuilder builder = new StringBuilder();
        builder.append("DELETE FROM PersonEmailAddress ");
        builder.append("WHERE personid=? AND emailAddress=?;");
        PreparedStatement ps = conn.prepareStatement(builder.toString());
        ps.setLong(1, personid);
        ps.setString(2, emailAddress);
        ps.executeUpdate();
        return;
    }
    
    public static List<String> getEmailAddresses(long personid)
        throws SQLException
    {
        List<String> numbers = new LinkedList<String>();
        Connection conn = ConnectionManager.getConnection();
        StringBuilder builder = new StringBuilder();
        builder.append("SELECT email_address ");
        builder.append("FROM PersonEmailAddress ");
        builder.append("WHERE personid=?;");
        PreparedStatement ps = conn.prepareStatement(builder.toString());
        ps.setLong(1, personid);
        ResultSet rs = ps.executeQuery();
        while (rs.next())
            numbers.add(rs.getString(1));
        return numbers;
    }
    
    private static Map<String,Object> getPersonInfo(long personid, List<String> attributes)
        throws SQLException
    {
        Map<String,Object> values = new HashMap<String,Object>();
        if (attributes.size() == 0) return values;
        
        Connection conn = ConnectionManager.getConnection();
        
        StringBuilder builder = new StringBuilder();
        builder.append("SELECT ");
        Iterator<String> colIt = attributes.iterator();
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
                values.put(col, State.parseState(rs.getString(col)));
                break;
            case "zip":
                values.put(col, rs.getString(col));
                break;
            }
        }
        return values;
    }
    
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
                values.put(col, rs.getInt(col));
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