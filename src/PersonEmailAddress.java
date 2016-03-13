import java.util.ArrayList;

public class PersonEmailAddress {
	ArrayList<String> emails = new ArrayList<String>();
	public int id;
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append("PersonId = " + id + "\n");
		for(int i = 0; i < emails.size(); i++){
			if(i == emails.size() - 1){
				sb.append(emails.get(i) + "\n");
			}else{
				sb.append(emails.get(i) + ", ");
			}
			
		}
		return sb.toString();
	}
	
	public static PersonEmailAddress createPersonEmailAddress(int person){
		PersonEmailAddress pea = new PersonEmailAddress();
		pea.id = person;
		pea.emails.add("pizza@rit.edu");
		pea.emails.add("wings@rit.edu");
		pea.emails.add("cola@rit.edu");
		return pea;
	}
}
