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
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.LinkedList;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class ProfileManager
{
    private static final int DIGEST_BYTE_SIZE = 64;
    public static final long USERNAME_NOT_IN_TABLE = -1L;
    public static final long INCORRECT_PASSWORD = -2L;
    public enum PersonType { NOT_A_PERSON, CUSTOMER, EMPLOYEE };
    
    private static final SecureRandom sr = new SecureRandom();
    
    private static byte[] computeHash(byte[] password, byte[] salt)
        throws NoSuchAlgorithmException
    {   
        byte[] passConcatSalt = new byte[password.length + salt.length];
        int idx = 0;
        for (int passIdx = 0; passIdx < password.length; ++passIdx)
            passConcatSalt[idx++] = password[passIdx];
        for (int saltIdx = 0; saltIdx < salt.length; ++saltIdx)
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
    
    private static long createPerson(HashMap<String,Object> values, String password)
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
                ps.setString(paramIdx++, ((PersonTable.State)values.get("state")).abbrev());
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
    
    public static long createCustomer(HashMap<String,Object> values, String password)
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
    
    public static long createEmployee(HashMap<String,Object> values, String password)
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
                ps.setFloat(paramIdx++, (float)values.get("hourly_rate"));
                break;
            case "ssn":
                ps.setInt(paramIdx++, (int)values.get("ssn"));
                break;
            case "hours_per_week":
                ps.setInt(paramIdx++, (int)values.get("hours_per_week"));
                break;
            case "date_hired":
                ps.setDate(paramIdx++, (Date)values.get("date_hired"));
            case "job_title":
                ps.setString(paramIdx++, (String)values.get("job_title"));
                break;
            }
        }
        ps.executeUpdate();
        return personid;
    }

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
    
    public static void modifyCustomer(long personid, HashMap<String,Object> values)
        throws SQLException
    {
        Connection conn = ConnectionManager.getConnection();
        StringBuilder builder = new StringBuilder();
        builder.append("UPDATE Customer ");
        builder.append("SET ");
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
            case "reward_pts":
            case "active":
                columns.add(col);
                break;
            }
        }
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
            }
        }
    }
}