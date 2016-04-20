package edu.rit.cs.Za;

/**
 * PaymentMethod.java
 * Contributor(s):  Jordan Rosario (jar2119@rit.edu)
 */

/**
 * Enumeration for payment methods accepted by 'Za. Orders may be paid for with
 * cash or a credit card (sorry, no checks).
 */
public enum PaymentMethod
{
    /* the values */
    CARD("CARD"), CASH("CASH");
    
    /* the string representation of this payment method */
    private String method;
    
    /**
     * Creates a PaymentMethod value from its string representation
     * @param method    the string representation of the PaymentMethod
     */
    private PaymentMethod(String method) { this.method = method; }
    
    /**
     * Retrives the string representation of this PaymentMethod.
     * @return the string representation of this PaymentMethod
     */
    public String toString() { return method; }
    
    /**
     * Attempts to parse a PaymentMethod from a string possibly containing the
     * string representation of a PaymentMethod.
     * @param s the string from which to attempt to extract a value
     * @return the valid PaymentMethod, if any, represented by the string
     */
    public static PaymentMethod parsePaymentMethod(String s)
    {
        for (PaymentMethod payMeth : PaymentMethod.values())
            if (s.equalsIgnoreCase(payMeth.method)) return payMeth;
        StringBuilder builder = new StringBuilder();
        builder.append('\"');
        builder.append(s);
        builder.append("\" IS NOT A PaymentMethod.");
        throw new IllegalArgumentException(builder.toString());
    }
}