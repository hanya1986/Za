package edu.rit.cs.Za;

/**
 * PersonType.java
 * Contributor(s):  Jordan Rosario (jar2119@rit.edu)
 */

/**
 * Enumeration to indicate which category a Person falls under. At 'Za, a Person
 * must be either a Customer or an Employee (well, employees can be customers,
 * too, but they need a separate customer profile), because Person is a disjoint
 * entity that is a total generalization of Customer and Employee. 
 */
public enum PersonType { NOT_A_PERSON, CUSTOMER, EMPLOYEE }