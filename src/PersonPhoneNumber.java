import java.util.ArrayList;

public class PersonPhoneNumber {
	ArrayList<String> phoneNumbers = new ArrayList<String>();
	public int id;
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append("PersonId = " + id + "\n");
		for(int i = 0; i < phoneNumbers.size(); i++){
			if(i == phoneNumbers.size() - 1){
				sb.append(phoneNumbers.get(i) + "\n");
			}else{
				sb.append(phoneNumbers.get(i) + ", ");
			}
			
		}
		return sb.toString();
	}
	
	public static PersonPhoneNumber createPersonPhoneNumber(int person){
		PersonPhoneNumber ppn = new PersonPhoneNumber();
		ppn.id = person;
		ppn.phoneNumbers.add("3477777771");
		ppn.phoneNumbers.add("3477777772");
		ppn.phoneNumbers.add("3477777773");
		return ppn;
	}
}
