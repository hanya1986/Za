package edu.rit.cs.Za;

/**
 * ItemType.java
 * Contributor(s):  Jordan Rosario (jar2119@rit.edu)
 */

public enum ItemType
{
    FOOD("FOOD"), DRINK("DRINK"), SIDE("SIDE");
    
    private final String itemType;
    private ItemType(String itemType) { this.itemType = itemType; }
    public String toString() { return itemType; }
}
