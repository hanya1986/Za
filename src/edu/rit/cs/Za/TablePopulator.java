package edu.rit.cs.Za;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;

/**
 * "populateX()" methods broken into two parts
 * 	1) Parse data from .txt files
 *  2) Init db objects
 */
public class TablePopulator {
	
	private static FileReader dataFileReader;
	private static BufferedReader dataBufferedReader;
	
	private static void populatePersons(File personsFile) {
		Map<String, ArrayList<String>> personData = new HashMap<String, ArrayList<String>>();
		personData.put("---STREET_CITY_STATE_ZIP", new ArrayList<String>());
		personData.put("---FIRST_LAST_NAMES", new ArrayList<String>());
		personData.put("---MIDDLE_NAMES", new ArrayList<String>());
		personData.put("---DATES", new ArrayList<String>());
		personData.put("---USERNAMES", new ArrayList<String>());
		personData.put("---PW_HASH", new ArrayList<String>());
		personData.put("---PW_SALT", new ArrayList<String>());
		String currLine;
		
		try {
			String currKey = "";
			while ((currLine = dataBufferedReader.readLine()) != null) {
				if (currLine.startsWith("---")) { //reading in new type of data
					currKey = currLine;	//...so update the key we're pairing vals to
					continue;
				}
				personData.get(currKey).add(currLine);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		for (int personsCreated = 0; personsCreated < 100; personsCreated++) {
			Person person = Person.createPerson();
			person.username = personData.get("---USERNAMES").get(personsCreated);
			person.first_name = personData.get("---FIRST_LAST_NAMES").get(personsCreated).split("\\s+")[0];
			person.middle_name = personData.get("---MIDDLE_NAMES").get(personsCreated);
			person.last_name = personData.get("---FIRST_LAST_NAMES").get(personsCreated).split("\\s+")[1];
			person.date_of_birth = new java.sql.Date(Long.parseLong(personData.get("---DATES").get(personsCreated)));
			person.street = personData.get("---STREET_CITY_STATE_ZIP").get(personsCreated).split(",")[0];
			person.city = personData.get("---STREET_CITY_STATE_ZIP").get(personsCreated).split(",")[1];
			person.state = personData.get("---STREET_CITY_STATE_ZIP").get(personsCreated).split(",")[2];
			person.zip = personData.get("---STREET_CITY_STATE_ZIP").get(personsCreated).split(",")[3];
			
			Random hashSaltGenerator = new Random();
			person.password_hash = new byte[64]; person.password_salt = new byte[64];
			hashSaltGenerator.nextBytes(person.password_hash); hashSaltGenerator.nextBytes(person.password_salt);
			System.out.println(person);
		}
	}

	/**
	 * Populate tables. Persons done. TODO: More tables.
	 */
	public static void main(String[] args) {		
		File[] dataFiles = new File("table_data/").listFiles();
		for (File dataFile : dataFiles) {
			try { dataFileReader = new FileReader(dataFile);}
			catch (FileNotFoundException e) {e.printStackTrace();}
			dataBufferedReader = new BufferedReader(dataFileReader);
			switch(dataFile.getName()) {
				case "person_data.txt":
					populatePersons(dataFile);
			}
		}
	}
}