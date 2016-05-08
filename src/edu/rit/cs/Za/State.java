/**
 * State.java
 * Contributor(s):  Jordan Rosario (jar2119@rit.edu)
 */

package edu.rit.cs.Za;

/**
 * Enumeration for valid state abbreviations. Used for addresses.
 */
public enum State
{
    /* the values with their two-letter abbreviations */
    ALABAMA("AL"), ALASKA("AK"), ARIZONA("AZ"), ARKANSAS("AR"), CALIFORNIA("CA"),
    COLORADO("CO"), CONNECTICUT("CT"), DELAWARE("DE"), FLORIDA("FL"), GEORGIA("GA"),
    HAWAII("HI"), IDAHO("ID"), ILLINOIS("IL"), INDIANA("IN"), IOWA("IA"),
    KANSAS("KS"), KENTUCKY("KY"), LOUISIANA("LA"), MAINE("ME"), MARYLAND("MD"),
    MASSACHUSETTS("MA"), MICHIGAN("MI"), MINNESOTA("MN"), MISISSIPPI("MS"), MISSOURI("MO"),
    MONTANA("MT"), NEBRASKA("NE"), NEVADA("NV"), NEW_HAMPSHIRE("NH"), NEW_JERSEY("NJ"),
    NEW_MEXICO("NM"), NEW_YORK("NY"), NORTH_CAROLINA("NC"), NORTH_DAKOTA("ND"), OHIO("OH"),
    OKLAHOMA("OK"), OREGON("OR"), PENNSYLVANIA("PA"), RHODE_ISLAND("RI"), SOUTH_CAROLINA("SC"),
    SOUTH_DAKOTA("SD"), TENNESSEE("TN"), TEXAS("TX"), UTAH("UT"), VERMONT("VT"),
    VIRGINIA("VA"), WASHINGTON("WA"), WEST_VIRGINIA("WV"), WISCONSIN("WI"), WYOMING("WY");
    
    /* the State's two-letter abbreviation */
    private final String abbrev;
    
    /**
     * Initializes a State with its two-letter abbreviation.
     * @param abbrev    the State's two-letter abbreviation
     */
    private State(String abbrev) { this.abbrev = abbrev; }
    
    /**
     * Returns the State's two-letter abbreviation.
     * 
     * @return the State's abbreviation
     */
    public String toString() { return abbrev; }
    
    /**
     * Attempts to parse a State from a string expected to contain a two-letter
     * abbreviation.
     * @param s the String from which to parse a State value
     * @return the State value, if any, whose abbreviation matches the String
     */
    public static State parseState(String s)
    {
        for (State state : State.values())
            if (s.equalsIgnoreCase(state.abbrev)) return state;
        StringBuilder builder = new StringBuilder();
        builder.append('\"');
        builder.append(s);
        builder.append("\" IS NOT A State.");
        throw new IllegalArgumentException(builder.toString());
    }
}
