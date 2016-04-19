package edu.rit.cs.Za;

/**
 * ItemType.java
 * Contributor(s):  Jordan Rosario (jar2119@rit.edu)
 */

/**
 * Enumeration for valid item types. Item's sold at 'Za fall into one of a few
 * categories, namely pizzas, beverages, and side dishes.
 */
public enum ItemType
{
    /* the values */
    PIZZA("PIZZA"), DRINK("DRINK"), SIDE("SIDE");
    
    /* the string representation of this value */
    private final String itemType;
    
    /**
     * Initializes an ItemType with its string representation
     * @param itemType  the value's string representation
     */
    private ItemType(String itemType) { this.itemType = itemType; }
    
    /**
     * Retrieves the string representation of the ItemType.
     * @return the string representation of the ItemType
     */
    public String toString() { return itemType; }
    
    /**
     * Attempts to extract a valid ItemType from a String.
     * 
     * @param s the String from which to attempt the extraction
     * 
     * @return  the ItemType whose string representation corresponds to that in
     *          the String parameter, if any
     */
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