/**
 * Month.java
 * Contributor(s):  Jordan Rosario (jar2119@rit.edu)
 * 					Jeremy Friedman (jsf6410@g.rit.edu)
 */

package edu.rit.cs.Za;

/**
 * Enumeration for integral values for months compatible with java.sql.Date,
 * which are zero-indexed (i.e., 0 corresponds to January and 11 to December).
 */
public enum Month
{
    /* the values */
    JANAURY(0), FEBRUARY(1), MARCH(2), APRIL(3), MAY(4), JUNE(5),
    JULY(6), AUGUST(7), SEPTEMBER(8), OCTOBER(9), NOVEMBER(10), DECEMBER(11);
    
    /* this Month's integral value */
    private int month;
    
    /**
     * Initializes a Month with its value.
     * @param month
     */
    private Month(int month) { this.month = month; }
    
    /**
     * Retrieves the integral value of this Month.
     * @return the integral value of this Month.
     */
    public int value() { return month; }
    
    /**
     * Attempts to convert an int into its corresponding Month value.
     * @param inputMonth    the zero-indexed Month value
     * @return the Month, if any, that corresponds to the integral value
     */
    public static Month parseMonth(int inputMonth)
    {
        for (Month month : Month.values())
        {
            if (inputMonth == month.month)
            {
            	return month;
            }
        }
        StringBuilder builder = new StringBuilder();
        builder.append(inputMonth);
        builder.append(" IS NOT A VALID VALUE FOR MONTH. ");
        throw new IllegalArgumentException(builder.toString());
	}
}