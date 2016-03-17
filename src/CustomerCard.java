import java.util.ArrayList;
import java.util.Iterator;

public class CustomerCard {
	public long personId;
	public ArrayList<String> cardNumber = new ArrayList<String>();
	
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append(personId + ": ");
		Iterator<String> cardNumberIt = cardNumber.iterator();
		while(cardNumberIt.hasNext()){
			String cardNum = cardNumberIt.next();
		    sb.append(cardNum);
		    if (cardNumberIt.hasNext()) sb.append(',');
		}
		return sb.toString();
	}
}
