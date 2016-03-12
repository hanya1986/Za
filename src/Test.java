import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import org.h2.tools.DeleteDbFiles;

public class Test {

	public static void main(String[] args) throws Exception {
		DeleteDbFiles.execute("~", "test", true);
		Class.forName("org.h2.Driver");
		Connection conn = DriverManager.getConnection("jdbc:h2:~/test");
		Statement stat = conn.createStatement();
		stat.execute("create table test(id int primary key, name varchar(255))");
		stat.execute("insert into test values(1, 'Hello')");
		ResultSet rs;
		rs = stat.executeQuery("select * from test");
		while (rs.next()) {
			System.out.println(rs.getString("name"));
		}
		stat.close();
		conn.close();
	}
}
