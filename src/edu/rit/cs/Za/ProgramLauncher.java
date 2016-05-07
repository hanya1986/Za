package edu.rit.cs.Za;

import java.awt.EventQueue;
import java.sql.SQLException;

import javax.swing.JOptionPane;

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
			        /*String db_path = db_location + ".h2.db";
			        File f = new File(db_path);
			        if (f.exists()) {
			            System.out.println("REMOVING OLD DATABASE\n");
			            f.delete();
			        }*/
			        String username = "username";
			        String password = "password";
			        ConnectionManager.initConnection(db_location, username, password);
                    ZaDatabase.createDatabase();
					new LoginView();
				} catch (SQLException ex) {
					JOptionPane.showMessageDialog(null, "A database error occurred:\n" + ex.getMessage(), "\'Za", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
	}
}