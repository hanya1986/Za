package edu.rit.cs.Za.ui;

import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import edu.rit.cs.Za.CreditCard;
import edu.rit.cs.Za.PersonType;
import edu.rit.cs.Za.ProfileManager;

public class PaymentModel {

	private Map<String, Object> customer;
	private List<CreditCard> creditCards;
	private CreditCard selectedCreditCard;
	
	public PaymentModel() {
		
	}
	
	public void updateUsername(String username) throws SQLException {
		customer = null;
		creditCards = null;
		
		long person_id = ProfileManager.getPersonID(username);
		if (person_id == ProfileManager.USERNAME_NOT_IN_TABLE) return;
		if (ProfileManager.getPersonType(person_id) != PersonType.CUSTOMER) return;

        List<String> attributes = new LinkedList<String>();
        attributes.add("cust_id");
        attributes.add("first_name");
        attributes.add("middle_name");
        attributes.add("last_name");
        attributes.add("date_of_birth");
        attributes.add("street");
        attributes.add("city");
        attributes.add("state");
        attributes.add("zip");
        attributes.add("reward_pts");
        attributes.add("active");
		this.customer = ProfileManager.getCustomerInfo(person_id, attributes);
		
		this.creditCards = ProfileManager.getCreditCards(person_id);
	}
	
	public void selectCreditCard(CreditCard card) {
		this.selectedCreditCard = card;
	}
	
	public void payByCash() {
		this.selectedCreditCard = null;
	}
	
	public Map<String, Object> getCustomer() {
		return this.customer;
	}
	
	public List<CreditCard> getCreditCards() {
		return this.creditCards;
	}
	
	public CreditCard getSelectedCreditCard() {
		return this.selectedCreditCard;
	}
}
