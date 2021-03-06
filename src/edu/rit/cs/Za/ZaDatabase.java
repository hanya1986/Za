package edu.rit.cs.Za;

/**
 * ZaDatabase.java
 * Contributor(s):  Jordan Rosario (jar2119@rit.edu)
 *                  Nicholas Marchionda (njm3348@rit.edu)
 */

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.PreparedStatement;

/**
 * Static class for creating the 'Za database.
 */
public class ZaDatabase
{
    /**
     * Creates the Person table in the 'Za database. Every Person must also
     * exist in either Customer or Employee.
     */
    private static void createPersonTable()
        throws SQLException
    {
        Connection conn = ConnectionManager.getConnection();
        StringBuilder builder = new StringBuilder();
        builder.append("CREATE TABLE IF NOT EXISTS Person (");
        builder.append("  personid      IDENTITY,");
        builder.append("  first_name    VARCHAR(256) NOT NULL,");
        builder.append("  middle_name   VARCHAR(256),");
        builder.append("  last_name     VARCHAR(256) NOT NULL,");
        builder.append("  date_of_birth DATE,");
        builder.append("  username      VARCHAR(256) UNIQUE NOT NULL,");
        builder.append("  password_hash BINARY(64) NOT NULL,");
        builder.append("  password_salt BINARY(64) NOT NULL,");
        builder.append("  street        VARCHAR(256),");
        builder.append("  city          VARCHAR(256),");
        builder.append("  state         CHAR(2),");
        builder.append("  zip           VARCHAR(10),");
        builder.append("  PRIMARY KEY (personid),");
        builder.append("  CHECK (personid>=0)");
        builder.append(");");
        
        PreparedStatement ps = conn.prepareStatement(builder.toString());
        ps.executeUpdate();
        return;
    }
    
    /**
     * Creates the Customer table in the 'Za database. Customer is a
     * specialization of Person.
     */
    private static void createCustomerTable()
        throws SQLException
    {
        Connection conn = ConnectionManager.getConnection();
        StringBuilder builder = new StringBuilder();
        builder.append("CREATE TABLE IF NOT EXISTS Customer (");
        builder.append("  cust_id    BIGINT,");
        builder.append("  reward_pts INT DEFAULT 0,");
        builder.append("  active     BOOLEAN DEFAULT TRUE,");
        builder.append("  PRIMARY KEY (cust_id),");
        builder.append("  FOREIGN KEY (cust_id) REFERENCES Person(personid),");
        builder.append("  CHECK (reward_pts>=0)");
        builder.append(");");
        
        PreparedStatement ps = conn.prepareStatement(builder.toString());
        ps.executeUpdate();
        return;
    }
    
    /**
     * Creates the Employee table in the 'Za database. Employee is a
     * specialization of Person.
     */
    private static void createEmployeeTable()
        throws SQLException
    {
        Connection conn = ConnectionManager.getConnection();
        StringBuilder builder = new StringBuilder();
        builder.append("CREATE TABLE IF NOT EXISTS Employee (");
        builder.append("  empid           BIGINT,");
        builder.append("  hourly_rate     DECIMAL(5,2) NOT NULL,");
        builder.append("  ssn             VARCHAR(25) NOT NULL,");
        builder.append("  hours_per_week  REAL NOT NULL,");
        builder.append("  date_hired      DATE NOT NULL,");
        builder.append("  date_terminated DATE,");
        builder.append("  job_title       VARCHAR(256) NOT NULL,");
        builder.append("  PRIMARY KEY (empid),");
        builder.append("  FOREIGN KEY (empid) REFERENCES PERSON(personid)");
        builder.append(");");
        
        PreparedStatement ps = conn.prepareStatement(builder.toString());
        ps.executeUpdate();
        return;
    }
    
    /**
     * Creates the PersonEmailAddressTable. Email address is a multivalued
     * attribute of Person.
     */
    private static void createPersonEmailAddressTable()
        throws SQLException
    {
        Connection conn = ConnectionManager.getConnection();
        StringBuilder builder = new StringBuilder();
        builder.append("CREATE TABLE IF NOT EXISTS PersonEmailAddress (");
        builder.append("  personid   BIGINT,");
        builder.append("  email_addr VARCHAR(256) NOT NULL,");
        builder.append("  PRIMARY KEY (personid, email_addr),");
        builder.append("  FOREIGN KEY (personid) REFERENCES Person(personid)");
        builder.append(");");
        
        PreparedStatement ps = conn.prepareStatement(builder.toString());
        ps.executeUpdate();
        return;
    }
    
    /**
     * Creates the Credit_Card table. Credit_Cards are weak entities, and more
     * than one Customer can share the same card.
     * @throws SQLException
     */
    private static void createCreditCardTable()
        throws SQLException
    {
        Connection conn = ConnectionManager.getConnection();
        StringBuilder builder = new StringBuilder();
        builder.append("CREATE TABLE IF NOT EXISTS Credit_Card (");
        builder.append("  number   CHAR(16),");
        builder.append("  sec_code CHAR(3) NOT NULL,");
        builder.append("  exp_month INT NOT NULL,");
        builder.append("  exp_year INT NOT NULL,");
        builder.append("  PRIMARY KEY (number),");
        builder.append("  CHECK (exp_month BETWEEN 0 AND 11 AND exp_year>=0)");
        builder.append(");");
        
        PreparedStatement ps = conn.prepareStatement(builder.toString());
        ps.executeUpdate();
        return;
    }
    
    /**
     * Creates the CustomerCard table, which associates one or more people with
     * a Credit_Card.
     */
    private static void createCustomerCardTable()
        throws SQLException
    {
        Connection conn = ConnectionManager.getConnection();
        StringBuilder builder = new StringBuilder();
        builder.append("CREATE TABLE IF NOT EXISTS CustomerCard (");
        builder.append("  personid BIGINT, ");
        builder.append("  card_number CHAR(16) NOT NULL, ");
        builder.append("  PRIMARY KEY (personid, card_number),");
        builder.append("  FOREIGN KEY (personid) REFERENCES Person(personid),");
        builder.append("  FOREIGN KEY (card_number) REFERENCES Credit_Card(number)");
        builder.append(");");
        
        PreparedStatement ps = conn.prepareStatement(builder.toString());
        ps.executeUpdate();
        return;
    }
    
    /**
     * Creates the Menu_Item table.
     */
    private static void createMenu_ItemTable()
        throws SQLException
    {
        Connection conn = ConnectionManager.getConnection();
        StringBuilder builder = new StringBuilder();
        builder.append("CREATE TABLE IF NOT EXISTS Menu_Item (");
        builder.append("  name          VARCHAR(256),");
        builder.append("  type          VARCHAR(8) NOT NULL,");
        
        /*
         * TODO: remove price column (now have small, medium, and large price
         * columns)
         */
        builder.append("  price         DECIMAL(4,2) NOT NULL,");
        
        builder.append("  est_prep_time INT,");
        builder.append("  available     BOOLEAN DEFAULT TRUE,");
        builder.append("  small_price   DECIMAL(4,2) NOT NULL,");
        builder.append("  medium_price  DECIMAL(4,2) NOT NULL,");
        builder.append("  large_price   DECIMAL(4,2) NOT NULL,");
        builder.append("  PRIMARY KEY (name),");
        builder.append("  CHECK (type IN (\'PIZZA\',\'SIDE\',\'DRINK\'))");
        builder.append(");");
        
        PreparedStatement ps = conn.prepareStatement(builder.toString());
        ps.executeUpdate();
        return;
    }
    
    /**
     * Creates the PersonPhoneNumberTable. Phone number is a multivalued
     * attribute of Person.
     */
    private static void createPersonPhoneNumberTable()
        throws SQLException
    {
        Connection conn = ConnectionManager.getConnection();
        StringBuilder builder = new StringBuilder();
        builder.append("CREATE TABLE IF NOT EXISTS PersonPhoneNumber (");
        builder.append("  personid     BIGINT,");
        builder.append("  phone_number VARCHAR(17) NOT NULL,");
        builder.append("  PRIMARY KEY (personid, phone_number),");
        builder.append("  FOREIGN KEY (personid) REFERENCES Person(personid)");
        builder.append(");");
        
        PreparedStatement ps = conn.prepareStatement(builder.toString());
        ps.executeUpdate();
        return;
    }
    
    /**
     * Creates the ZaOrder table. This is the largest and most important
     * relation in the 'Za database.
     */
    private static void createZaOrderTable()
        throws SQLException
    {
        Connection conn = ConnectionManager.getConnection();
        StringBuilder builder = new StringBuilder();
        builder.append("CREATE TABLE IF NOT EXISTS ZaOrder (");
        builder.append("  orderid IDENTITY,");
        builder.append("  custid BIGINT NOT NULL DEFAULT 0,");
        builder.append("  order_type VARCHAR(10),");
        builder.append("  active BOOLEAN DEFAULT TRUE,");
        builder.append("  empid_took_order BIGINT,");
        builder.append("  empid_prepared_order BIGINT,");
        builder.append("  empid_delivered_order BIGINT,");
        builder.append("  time_order_placed TIMESTAMP DEFAULT CURRENT_TIMESTAMP,");
        builder.append("  time_order_out TIMESTAMP,");
        builder.append("  time_order_delivered TIMESTAMP,");
        builder.append("  subtotal DECIMAL(9,2),");
        builder.append("  tax DECIMAL(9,2),");
        builder.append("  total DECIMAL(10,2),");
        builder.append("  tip DECIMAL(6,2),");
        builder.append("  pay_method VARCHAR(8) DEFAULT 'CASH',");
        builder.append("  PRIMARY KEY (orderid),");
        builder.append("  FOREIGN KEY (custid) REFERENCES Customer (cust_id),");
        builder.append("  FOREIGN KEY (empid_took_order) REFERENCES Employee (empid),");
        builder.append("  FOREIGN KEY (empid_prepared_order) REFERENCES Employee (empid),");
        builder.append("  FOREIGN KEY (empid_delivered_order) REFERENCES Employee (empid),");
        builder.append("  CHECK (orderid>=0),");
        builder.append("  CHECK (order_type in (\'DELIVERY\',\'CARRY-OUT\')),");
        builder.append("  CHECK (pay_method in (\'CARD\',\'CASH\'))");
        builder.append(");");
        
        PreparedStatement ps = conn.prepareStatement(builder.toString());
        ps.executeUpdate();
        return;
    }
    
    /**
     * Creates the ZaOrderItem table. An order must have at least one order item
     * in it.
     */
    private static void createZaOrderItemTable()
        throws SQLException
    {
        Connection conn = ConnectionManager.getConnection();
        StringBuilder builder = new StringBuilder();
        builder.append("CREATE TABLE IF NOT EXISTS ZaOrderItem (");
        builder.append("  orderid BIGINT,");
        builder.append("  itemid VARCHAR(256) NOT NULL,");
        builder.append("  quantity INT DEFAULT 1,");
        builder.append("  size VARCHAR(8),");
        
        /* TODO: remove available column (not need in ZaOrderItem) */
        builder.append("  available BOOLEAN DEFAULT TRUE,");
        
        builder.append("  PRIMARY KEY (orderid,itemid,size),");
        builder.append("  FOREIGN KEY (orderid) REFERENCES ZaOrder (orderid),");
        builder.append("  FOREIGN KEY (itemid) REFERENCES Menu_Item (name),");
        builder.append("  CHECK (quantity>0),");
        builder.append("  CHECK (size in (\'SMALL\',\'MEDIUM\',\'LARGE\'))");
        builder.append(");");
        
        PreparedStatement ps = conn.prepareStatement(builder.toString());
        ps.executeUpdate();
        return;
    }
    
    /**
     * Creates the 'Za database from the 'Za database schema specified in this
     * class.
     */
    public static void createDatabase()
        throws SQLException
    {
        createPersonTable();
        createPersonEmailAddressTable();
        createPersonPhoneNumberTable();
        createCustomerTable();
        createEmployeeTable();
        createCreditCardTable();
        createCustomerCardTable();
        createMenu_ItemTable();
        createZaOrderTable();
        createZaOrderItemTable();
    }
}