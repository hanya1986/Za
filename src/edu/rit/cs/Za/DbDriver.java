package edu.rit.cs.Za;

import java.io.File;

public class DbDriver
{    
    public static void main(String[] args)
        throws Exception // test-driver program; swallow exceptions
    {
        StringBuilder builder = new StringBuilder();
        builder.append(System.getProperty("user.home"));
        builder.append(File.separatorChar);
        builder.append("ZADB");
        builder.append(File.separatorChar);
        builder.append("za");
        
        String location = builder.toString();
        String username = "username";
        String password = "password";
        
        System.out.println("INITIALIZING DATABASE CONNECTION");
        System.out.printf("location: %s%n", location);
        System.out.printf("username: %s%n", username);
        System.out.printf("password: %s%n", password);
        ConnectionManager.initConnection(location, username, password);
        System.out.println("DATABASE CONNECTION INITIALIZED");
        System.out.println();
        
        System.out.println("CREATING DATABASE");
        ZaDatabase.createDatabase();
        System.out.println("DATABASE CREATED");
        System.out.println();
        
        System.out.println("TESTING State ENUMERATION");
        System.out.println(State.NEW_YORK);
    }
}
