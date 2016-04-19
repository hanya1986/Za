/**
 * OrderType.java
 * Contributor(s):  Jordan Rosario (jar2119@rit.edu)
 */

package edu.rit.cs.Za;

/**
 * Enumeration for valid order types. 'Za offers two options: delivery and
 * carry-out (no dine-in, though).
 * @author Jordan
 *
 */
public enum OrderType
{
    /* the values */
    DELIVERY("DELIVERY"), CARRY_OUT("CARRY-OUT");
    
    /* this OrderType's string representation */
    private final String orderType;
    
    /**
     * Initializes an OrderType with its string representation.
     * @param orderType the string representation of the value
     */
    private OrderType(String orderType)
    {
        this.orderType = orderType;
    }
    
    /**
     * Retrieves the string representation of the OrderType.
     * @return the string representation of the OrderType
     */
    public String toString() { return orderType; }
    
    /**
     * Attempts to parse an OrderType from a string.
     * @param s the string representing an OrderType
     * @return  the OrderType, if any, whose string representation matches that
     *          of the String parameter
     */
    public static OrderType parseOrderType(String s)
    {
        for (OrderType orderType : OrderType.values())
            if (s.equalsIgnoreCase(orderType.orderType)) return orderType;
        StringBuilder builder = new StringBuilder();
        builder.append('\"');
        builder.append(s);
        builder.append("\" IS NOT AN OrderType.");
        throw new IllegalArgumentException(builder.toString());
    }
}