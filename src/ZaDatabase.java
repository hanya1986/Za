/**
 * ZaDatabase.java
 * Contributor(s):  Jordan Rosario (jar2119@rit.edu)
 */

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.PreparedStatement;

public class ZaDatabase
{
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
        builder.append("  FOREIGN KEY (cust_id) REFERENCES Person(personid)");
        builder.append(");");
        
        PreparedStatement ps = conn.prepareStatement(builder.toString());
        ps.executeUpdate();
        return;
    }
    
    private static void createEmployeeTable()
        throws SQLException
    {
        Connection conn = ConnectionManager.getConnection();
        StringBuilder builder = new StringBuilder();
        builder.append("CREATE TABLE Employee (");
        builder.append("  empid           BIGINT,");
        builder.append("  hourly_rate     DECIMAL(3,2) NOT NULL,");
        builder.append("  ssn             INT NOT NULL,");
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
    
    private static void createPersonEmailAddressTable()
        throws SQLException
    {
        Connection conn = ConnectionManager.getConnection();
        StringBuilder builder = new StringBuilder();
        builder.append("CREATE TABLE IF NOT EXISTS PersonEmailAddress (");
        builder.append("  personid   BIGINT,");
        builder.append("  email_addr VARCHAR(256),");
        builder.append("  PRIMARY KEY (personid, email_addr),");
        builder.append("  FOREIGN KEY (personid) REFERENCES Person(personid)");
        builder.append(");");
        
        PreparedStatement ps = conn.prepareStatement(builder.toString());
        ps.executeUpdate();
        return;
    }
    
    private static void createCreditCardTable()
        throws SQLException
    {
        Connection conn = ConnectionManager.getConnection();
        StringBuilder builder = new StringBuilder();
        builder.append("CREATE TABLE IF NOT EXISTS Credit_Card (");
        builder.append("  number   CHAR(16),");
        builder.append("  sec_code CHAR(3),");
        builder.append("  PRIMARY KEY (number)");
        builder.append(");");
        
        PreparedStatement ps = conn.prepareStatement(builder.toString());
        ps.executeUpdate();
        return;
    }
    
    private static void createCustomerCardTable()
        throws SQLException
    {
        Connection conn = ConnectionManager.getConnection();
        StringBuilder builder = new StringBuilder();
        builder.append("CREATE TABLE IF NOT EXISTS CustomerCard (");
        builder.append("  personid BIGINT, ");
        builder.append("  card_number CHAR(16), ");
        builder.append("  PRIMARY KEY (personid, card_number),");
        builder.append("  FOREIGN KEY (personid) REFERENCES Person(personid),");
        builder.append("  FOREIGN KEY (card_number) REFERENCES Credit_Card(number)");
        builder.append(");");
        
        PreparedStatement ps = conn.prepareStatement(builder.toString());
        ps.executeUpdate();
        return;
    }
    
    private static void createMenu_ItemTable()
        throws SQLException
    {
        Connection conn = ConnectionManager.getConnection();
        StringBuilder builder = new StringBuilder();
        builder.append("CREATE TABLE IF NOT EXISTS Menu_Item (");
        builder.append("  name          VARCHAR(256),");
        builder.append("  type          BOOLEAN NOT NULL,");
        builder.append("  price         DECIMAL(2,2) NOT NULL,");
        builder.append("  est_prep_time INT,");
        builder.append("  PRIMARY KEY (name)");
        builder.append(");");
        
        PreparedStatement ps = conn.prepareStatement(builder.toString());
        ps.executeUpdate();
        return;
    }
    
    private static void createPersonPhoneNumberTable()
        throws SQLException
    {
        Connection conn = ConnectionManager.getConnection();
        StringBuilder builder = new StringBuilder();
        builder.append("CREATE TABLE IF NOT EXISTS PersonPhoneNumber (");
        builder.append("  personid     BIGINT,");
        builder.append("  phone_number VARCHAR(17),");
        builder.append("  PRIMARY KEY (personid, phone_number),");
        builder.append("  FOREIGN KEY (personid) REFERENCES Person(personid)");
        builder.append(");");
        
        PreparedStatement ps = conn.prepareStatement(builder.toString());
        ps.executeUpdate();
        return;
    }
    
    private static void createZaOrderTable()
        throws SQLException
    {
        Connection conn = ConnectionManager.getConnection();
        StringBuilder builder = new StringBuilder();
        builder.append("CREATE TABLE IF NOT EXISTS ZaOrder (");
        builder.append("  orderid IDENTITY,");
        builder.append("  custid BIGINT NOT NULL,");
        builder.append("  order_type VARCHAR(10),");
        builder.append("  empid_took_order BIGINT,");
        builder.append("  empid_prepared_order BIGINT,");
        builder.append("  empid_delivered_order BIGINT,");
        builder.append("  time_order_placed TIMESTAMP NOT NULL,");
        builder.append("  time_order_out TIMESTAMP,");
        builder.append("  time_order_delivered TIMESTAMP,");
        builder.append("  subtotal DECIMAL(7,2),");
        builder.append("  tax DECIMAL(7,2),");
        builder.append("  total DECIMAL(8,2),");
        builder.append("  tip DECIMAL(4,2),");
        builder.append("  PRIMARY KEY (orderid),");
        builder.append("  FOREIGN KEY (custid) REFERENCES Customer (cust_id),");
        builder.append("  FOREIGN KEY (empid_took_order) REFERENCES Employee (empid),");
        builder.append("  FOREIGN KEY (empid_prepared_order) REFERENCES Employee (empid),");
        builder.append("  FOREIGN KEY (empid_delivered_order) REFERENCES Employee (empid),");
        builder.append("  CHECK (orderid>=0),");
        builder.append("  CHECK (order_type in (\'Delivery\',\'Carry-out\'))");
        builder.append(");");
        
        PreparedStatement ps = conn.prepareStatement(builder.toString());
        ps.executeUpdate();
        return;
    }
    
    private static void createZaOrderItemTable()
        throws SQLException
    {
        Connection conn = ConnectionManager.getConnection();
        StringBuilder builder = new StringBuilder();
        builder.append("CREATE TABLE IF NOT EXISTS ZaOrderItem (");
        builder.append("  orderid BIGINT,");
        builder.append("  itemid VARCHAR(256),");
        builder.append("  quantity INT,");
        builder.append("  PRIMARY KEY (orderid,itemid),");
        builder.append("  FOREIGN KEY (orderid) REFERENCES ZaOrder (orderid),");
        builder.append("  CHECK (quantity>0)");
        builder.append(");");
        
        PreparedStatement ps = conn.prepareStatement(builder.toString());
        ps.executeUpdate();
        return;
    }
    
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
