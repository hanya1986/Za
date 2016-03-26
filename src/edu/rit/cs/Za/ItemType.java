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
    public static ItemType parseItemType(String s)
    {
        for (ItemType itemType : ItemType.values())
            if (s.equalsIgnoreCase(itemType.itemType)) return itemType;
        StringBuilder builder = new StringBuilder();
        builder.append('\"');
        builder.append(s);
        builder.append("\" IS NOT AN ItemType.");
        throw new IllegalArgumentException(builder.toString());
    }
}