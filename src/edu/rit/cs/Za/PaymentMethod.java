package edu.rit.cs.Za;

/**
 * PaymentMethod.java
 * Contributor(s):  Jordan Rosario (jar2119@rit.edu)
 */

public enum PaymentMethod
{
    CARD("CARD"), CASH("CASH");
    
    private String method;
    private PaymentMethod(String method) { this.method = method; }
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