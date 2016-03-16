import java.util.ArrayList;
import java.util.Iterator;

public class PersonPhoneNumber
{
	ArrayList<String> phoneNumbers = new ArrayList<String>();
	public long personid;
	
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("personid=");
		sb.append(personid);
		sb.append(";phone_numbers={");
		Iterator<String> phoneNumberIt = phoneNumbers.iterator();
		while (phoneNumberIt.hasNext())
		{
		    String phoneNumber = phoneNumberIt.next();
		    sb.append(phoneNumber);
		    if (phoneNumberIt.hasNext()) sb.append(',');
		}
		sb.append('}');
		return sb.toString();
	}
	
	public static PersonPhoneNumber createPersonPhoneNumber(long personid)
	{
		PersonPhoneNumber ppn = new PersonPhoneNumber();
		ppn.personid = personid;
		ppn.phoneNumbers.add("3477777771");
		ppn.phoneNumbers.add("3477777772");
		ppn.phoneNumbers.add("3477777773");
		return ppn;
	}
}