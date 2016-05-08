/**
 * CreditCard.java
 * Contributor(s):  Jordan Rosario (jar2119@rit.edu)
 */

package edu.rit.cs.Za;

/**
 * Struct for credit card information.
 */
public class CreditCard
{
    /* the credit card number, assumed to be 16 digits */
    public String cardNumber;
    
    /*
     * the security code found on the back of the card, assumed to be 3 digits
     */
    public String securityCode;
    
    /* the month of the credit card expiration date */
    public Month expirationMonth;
    
    /* the year of the credit card expiration date */
    public int expirationYear;
    
    /**
     * Tests for equality of two CreditCard objects. Two CreditCards are
     * if (1) they're both instances of CreditCard and (2) the values for each
     * of their attributes are equivalent.
     * 
     * @param o the other object to compare this CreditCard with
     * 
     * @return  true if the two objects are equivalent CreditCards, false
     *          otherwise
     */
    public boolean equals(Object o)
    {
        if (!(o instanceof CreditCard)) return false;
        CreditCard cc = (CreditCard)o;
        return  cardNumber.equals(cc.cardNumber) &&
                securityCode.equals(cc.securityCode) &&
                expirationMonth == cc.expirationMonth &&
                expirationYear == cc.expirationYear;
                
    }
    
    /**
     * Creates a string representation of this credit card, which is simply the
     * 16-digit card number on the front of the card.
     * 
     * @return the credit card number
     */
    public String toString()
    {
    	return cardNumber;
    }
}
