import java.sql.Connection;
import java.sql.SQLException;

public class ZaDatabase
{
    private static void createPersonTable()
        throws SQLException
    {
        Connection conn = ConnectionManager.getConnection();
        StringBuilder builder = new StringBuilder();
        builder.append("CREATE TABLE IF NOT EXISTS Person (");
        builder.append("  personid      IDENTITY CHECK >= 0,");
        builder.append("  first_name    VARCHAR(256) NOT NULL,");
        builder.append("  middle_name   VARCHAR(256),");
        builder.append("  last_name     VARCHAR(256) NOT NULL,");
        builder.append("  date_of_birth DATE,");
        builder.append("  username      VARCHAR(256) UNIQUE NOT NULL,");
        builder.append("  password_hash BINARY(64) NOT NULL,");
        builder.append("  password_salt BINARY(64) NOT NULL,");
        builder.append("  street        VARCHAR(256),");
        builder.append("  city          VARCHAR(256),");
        builder.append("  state         CHAR(2),");
        builder.append("  zip           VARCHAR(10),");
        builder.append("  PRIMARY KEY (personid)");
        builder.append(");");
    }
    
    public static void createDatabase()
    {
        
    }
}
