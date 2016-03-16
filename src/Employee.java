import java.math.BigDecimal;
import java.sql.Date;

public class Employee
{
    public long empid;
    public BigDecimal hourly_rate;
    public int ssn;
    public float hours_per_week;
    public Date date_hired;
    public Date date_terminated;
    public String job_title;
    
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append(empid);
        builder.append(' ');
        builder.append(hourly_rate);
        builder.append(' ');
        builder.append(ssn);
        builder.append(' ');
        builder.append(hours_per_week);
        builder.append(' ');
        builder.append(date_hired);
        builder.append(' ');
        builder.append(date_terminated);
        builder.append(' ');
        builder.append(job_title);
        return builder.toString();
    }
}
