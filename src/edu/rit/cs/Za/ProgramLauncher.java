package edu.rit.cs.Za;

import java.awt.EventQueue;
import java.io.File;

import edu.rit.cs.Za.ui.LoginView;

public class ProgramLauncher {
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					String db_location = "./ZADB/za";
			        String db_path = db_location + ".h2.db";
			        File f = new File(db_path);
			        if (f.exists()) {
			            System.out.println("REMOVING OLD DATABASE\n");
			            //f.delete();
			        }
			        String username = "username";
			        String password = "password";
			        ConnectionManager.initConnection(db_location, username, password);
			        //ZaDatabase.createDatabase();
					//TablePopulator populate = new TablePopulator();
					LoginView window = new LoginView();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}