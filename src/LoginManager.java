import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.sql.ResultSet;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class LoginManager
{
    public static final long USERNAME_NOT_IN_TABLE = -1L;
    public static final long INCORRECT_PASSWORD = -2L;
    
    /**
     * Determines whether the provided user-password combination form a valid
     * pair of login credentials.
     * @param username  the username identifying the user to log in
     * @param password  an obfuscation (TBD: hash? encryption?) of the user's
     *                  password          
     * @return  1. USERNAME_NOT_IN_TABLE if username not found in table;2. 
     *          INCORRECT_PASSWORD if password is incorrect;3. The user's
     *          unique ID number 
     * @throws SQLException If a SQL-related error occurs
     * @throws NoSuchAlgorithmException If the current implementation doesn't
     *                                  support SHA-512 hashing
     */
    public static long validateCredentials(String username, byte[] password) throws SQLException, NoSuchAlgorithmException
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
        
        byte[] passConcatSalt = new byte[password.length + passwordSalt.length];
        int idx = 0;
        for (int passIdx = 0; passIdx < password.length; ++passIdx)
            passConcatSalt[idx++] = password[passIdx];
        for (int saltIdx = 0; saltIdx < passwordSalt.length; ++saltIdx)
            passConcatSalt[idx++] = passwordSalt[saltIdx];
        
        MessageDigest md = MessageDigest.getInstance("SHA-512");
        md.update(passConcatSalt);
        byte[] digest = md.digest();
        
        if (!Arrays.equals(passwordHash, digest)) return INCORRECT_PASSWORD;
        return result.getLong("personid");
    }
}
