package edu.rit.cs.Za;

/**
 * Size.java
 * Contributor(s):  Jordan Rosario (jar2119@rit.edu)
 */

/**
 * Enumeration for valid item sizes. Items sold at 'Za can come in one of three
 * typical varieties (i.e., small, medium, and large).
 */
public enum ItemSize
{
    /* the values */
    SMALL("SMALL"), MEDIUM("MEDIUM"), LARGE("LARGE");
    
    /* the string representation of this value */
    private String size;
    
    /**
     * Initializes a value with its string representation.
     * @param size  the string representation of the value
     */
    private ItemSize(String size) { this.size = size; }
    
    /**
     * Retrieves the string representation of the value.
     * @return the string representation of the ItemSize
     */
    public String toString() { return size; }

    /**
     * Attempts to parse a valid ItemSize out of a String.
     * @param s the String to parse
     * @return  the valid ItemSize, if any, that could be extracted from the
     *          String's contents
     */
    public static ItemSize parseItemSize(String s)
    {
        for (ItemSize itemSize : ItemSize.values())
            if (s.equalsIgnoreCase(itemSize.size)) return itemSize;
        StringBuilder builder = new StringBuilder();
        builder.append('\"');
        builder.append(s);
        builder.append("\" IS NOT AN ItemSize.");
        throw new IllegalArgumentException(builder.toString());
    }
}
