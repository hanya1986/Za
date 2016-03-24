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
}
