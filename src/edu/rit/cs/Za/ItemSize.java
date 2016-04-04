package edu.rit.cs.Za;

/**
 * Size.java
 * Contributor(s):  Jordan Rosario (jar2119@rit.edu)
 */

public enum ItemSize
{
    SMALL("SMALL"), MEDIUM("MEDIUM"), LARGE("LARGE");
    
    private String size;
    private ItemSize(String size) { this.size = size; }
    public String toString() { return size; }

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
