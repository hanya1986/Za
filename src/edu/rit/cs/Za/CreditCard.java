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
}
