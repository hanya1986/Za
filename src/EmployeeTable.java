import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.sql.PreparedStatement;

public class EmployeeTable
{
    public static void createEmployeeTable(Connection conn)
    {
        try
        {
            String query =  "CREATE TABLE Employee (" +
                            "  empid           BIGINT," +
                            "  hourly_rate     DECIMAL NOT NULL," +
                            "  ssn             INT NOT NULL," +
                            "  hours_per_week  REAL NOT NULL," +
                            "  date_hired      DATE NOT NULL," +
                            "  date_terminated DATE," +
                            "  job_title       VARCHAR(256) NOT NULL," +
                            "  PRIMARY KEY (empid)," +
                            "  FOREIGN KEY (empid) REFERENCES PERSON(personid)" +
                            ");";
            
            Statement stmt = conn.createStatement();
            stmt.execute(query);
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }
        
        public static void insertEmployee(Connection conn, Employee employee)
        {
            try
            {
                PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO Employee (empid, hourly_rate, ssn, hours_per_week, date_hired, date_terminated, job_title) VALUES (?,?,?,?,?,?,?)");
                ps.setLong(1, employee.empid);
                ps.setBigDecimal(2, employee.hourly_rate);
                ps.setInt(3, employee.ssn);
                ps.setFloat(4, employee.hours_per_week);
                ps.setDate(5, employee.date_hired);
                ps.setDate(6, employee.date_terminated);
                ps.setString(7, employee.job_title);
                ps.executeUpdate();
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }   
        }
        
        public static ResultSet queryPersonTable(Connection conn, ArrayList<String> columns, ArrayList<String> whereClauses)
        {
            StringBuilder sb = new StringBuilder();
            sb.append("SELECT ");
            
            if(columns.isEmpty()) sb.append("* ");
            else
            {
                for(int i = 0; i < columns.size(); i++)
                    if(i != columns.size() - 1) sb.append(columns.get(i) + ", ");
                    else sb.append(columns.get(i) + " ");
            }
            
            sb.append("FROM Employee ");
            
            if(!whereClauses.isEmpty())
            {
                sb.append("WHERE ");
                for(int i = 0; i < whereClauses.size(); i++)
                    if(i != whereClauses.size() -1) sb.append(whereClauses.get(i) + " AND ");
                    else sb.append(whereClauses.get(i));
            }
            
            sb.append(";");
            
            try
            {
                Statement stmt = conn.createStatement();
                return stmt.executeQuery(sb.toString());
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }
            return null;
        }
        
    public static void printEmployeeTable(Connection conn)
    {
        String query = "SELECT * FROM Employee;";
        try
        {
            Statement stmt = conn.createStatement();
            ResultSet result = stmt.executeQuery(query);
            
            while(result.next())
            {
                Employee employee = new Employee();
                employee.empid = result.getLong(1);
                employee.hourly_rate = result.getBigDecimal(2);
                employee.ssn = result.getInt(3);
                employee.hours_per_week = result.getFloat(4);
                employee.date_hired = result.getDate(5);
                employee.date_terminated = result.getDate(6);
                employee.job_title = result.getString(7);
                
                System.out.println(employee);
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }
}
