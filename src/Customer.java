
public class Customer
{
    public long cust_id;
    int reward_pts;
    
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append(cust_id);
        builder.append(' ');
        builder.append(reward_pts);
        return builder.toString();
    }
}
