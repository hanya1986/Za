package edu.rit.cs.Za;

import java.awt.EventQueue;
import java.sql.SQLException;

import javax.swing.JOptionPane;

import edu.rit.cs.Za.ui.LoginView;

/**
 * The 'Za application launcher. Presents the user with the login screen.
 */
public class ProgramLauncher
{
	/**
	 * Connect to database and show login screen.
	 * 
	 * @param args command-line arguments (ignored)
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					String db_location = "./ZADB/za";
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