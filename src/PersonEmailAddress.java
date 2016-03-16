import java.util.ArrayList;
import java.util.Iterator;

public class PersonEmailAddress
{
	ArrayList<String> emails = new ArrayList<String>();
	public long personid;
	
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("personid=");
		sb.append(personid);
		sb.append(";emails={");
		Iterator<String> emailIt = emails.iterator();
		while (emailIt.hasNext())
		{
		    String email_addr = emailIt.next();
		    sb.append(email_addr);
		    if (emailIt.hasNext()) sb.append(',');
			
		}
		sb.append('}');
		return sb.toString();
	}
	
	public static PersonEmailAddress createPersonEmailAddress(long personid)
	{
		PersonEmailAddress pea = new PersonEmailAddress();
		pea.personid = personid;
		pea.emails.add("pizza@rit.edu");
		pea.emails.add("wings@rit.edu");
		pea.emails.add("cola@rit.edu");
		return pea;
	}
}