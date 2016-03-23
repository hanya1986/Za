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
    
    public static long validateCredentials(String username, String password) throws SQLException, NoSuchAlgorithmException
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
        byte[] digest = ProfileManager.computeHash(password.getBytes(), passwordSalt);
        
        if (!Arrays.equals(passwordHash, digest)) return INCORRECT_PASSWORD;
        return result.getLong("personid");
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
        SecureRandom sr = new SecureRandom();
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
    
    //public static long createEmployee(HashMap<String,Object> values,)
}
