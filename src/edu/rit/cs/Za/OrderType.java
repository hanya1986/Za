/**
 * OrderType.java
 * Contributor(s):  Jordan Rosario (jar2119@rit.edu)
 */

package edu.rit.cs.Za;

public enum OrderType
{
    DELIVERY("DELIVERY"), CARRY_OUT("CARRY-OUT");
    
    private final String orderType;
    private OrderType(String orderType)
    {
        this.orderType = orderType;
    }
    public String toString() { return orderType; }
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