import java.sql.Date;
import java.util.Random;

public class Person
{
    public int personid;
    public String first_name;
    public String middle_name;
    public String last_name;
    public Date date_of_birth;
    public String username;
    public byte[] password_hash;
    public byte[] password_salt;
    public String street;
    public String city;
    public String state;
    public String zip;
    
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append(personid);
        builder.append(' ');
        builder.append(first_name);
        builder.append(' ');
        builder.append(middle_name);
        builder.append(' ');
        builder.append(last_name);
        builder.append(' ');
        builder.append(date_of_birth.toString());
        builder.append(' ');
        builder.append(username);
        builder.append(' ');
        for (byte b : password_hash)
        {
            builder.append("0x");
            builder.append(Integer.toHexString(((int)b) & 0xFF));
            builder.append(' ');
        }
        for (byte b : password_salt)
        {
            builder.append("0x");
            builder.append(Integer.toHexString(((int)b) & 0xFF));
            builder.append(' ');
        }
        builder.append(street);
        builder.append(' ');
        builder.append(city);
        builder.append(' ');
        builder.append(state);
        builder.append(' ');
        builder.append(zip);
        return builder.toString();
    }
    
    public static Person createPerson()
    {
        Random rand = new Random();
        Person p = new Person();
        p.personid = 1;
        p.first_name = "Jordan";
        p.middle_name = "Alexander";
        p.last_name = "Rosario";
        p.date_of_birth = new Date(2016 - 1900, 2, 12);
        p.username = "jar2119";
        p.password_hash = new byte[64];
        rand.nextBytes(p.password_hash);
        p.password_salt = new byte[64];
        rand.nextBytes(p.password_salt);
        p.street = "161D Perkins Road";
        p.city = "Rochester";
        p.state = "NY";
        p.zip = "14623";
        return p;
    }
}
