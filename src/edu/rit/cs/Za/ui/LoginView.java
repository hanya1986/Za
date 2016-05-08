package edu.rit.cs.Za.ui;

/**
 * SignupView.java
 * Contributor(s):  Yihao Cheng (yc7816@rit.edu)
 */

import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.JPanel;
import javax.swing.JPasswordField;

import java.awt.Dimension;

import javax.swing.border.EmptyBorder;

import edu.rit.cs.Za.PersonType;
import edu.rit.cs.Za.ProfileManager;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Map;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;

import javax.swing.JLabel;
import java.awt.Color;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import javax.swing.ImageIcon;
import javax.swing.JButton;

public class LoginView implements ActionListener{

	private JFrame frame;
	private JPanel panel;
	private JPanel panel_1;
	private JPanel panel_2;
	private JPanel panel_3;
	private JPanel panel_4;
	private JButton loginButton;
	private JButton signupButton;
	private JButton forgotPass;
	private JTextField usernameText;
	private JPasswordField passwordText;
	
	/**
	 * Create the application.
	 */
	public LoginView() {
		initialize();
		this.frame.setVisible(true);
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setMinimumSize(new Dimension(700, 500));
		frame.setMaximumSize(new Dimension(700,500));
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.getContentPane().setLayout(new GridLayout(0, 2, 0, 0));
		
		panel = new JPanel();
		ImageIcon image = new ImageIcon("ZA.png");
		ImageIcon logo = new ImageIcon(image.getImage().getScaledInstance(350, 470, Image.SCALE_SMOOTH));
		JLabel logoLabel = new JLabel(logo);
		panel.add(logoLabel);
		image = new ImageIcon("pizza.jpg");
		logo = new ImageIcon(image.getImage().getScaledInstance(350, 200, Image.SCALE_SMOOTH));
		JLabel pizzaLabel = new JLabel(logo);
		frame.getContentPane().add(panel);
		panel_1 = new JPanel();
		frame.getContentPane().add(panel_1);
		panel_1.setLayout(new GridLayout(3, 1, 0, 0));
		panel_3 = new JPanel();
		panel_3.add(pizzaLabel);
		panel_1.add(panel_3);
		panel_2 = new JPanel();
		panel_1.add(panel_2);
		panel_4 = new JPanel();
		image = new ImageIcon("delivery.jpg");
		logo = new ImageIcon(image.getImage().getScaledInstance(350, 165, Image.SCALE_SMOOTH));
		JLabel deliveryLabel = new JLabel(logo);
		panel_4.add(deliveryLabel);
		panel_4.setBackground(Color.WHITE);
		panel_1.add(panel_4);
		panel_2.setLayout(new GridBagLayout());
		panel_2.setBorder(new EmptyBorder(10,10,10,10));
		panel_2.setBackground(Color.WHITE);
		GridBagConstraints cnst = new GridBagConstraints();
		cnst.insets = new Insets(4,2,4,2);
		JLabel usernameLabel = new JLabel("Username:");
		JLabel passwordLabel = new JLabel("Password:");
		loginButton = new JButton("Login");
		loginButton.addActionListener(this);
		signupButton = new JButton("Sign up");
		signupButton.addActionListener(this);
		usernameText = new JTextField();
		passwordText = new JPasswordField();
		passwordText.setEchoChar('*');
		Dimension prefSize = new Dimension(140,20);
		usernameText.setPreferredSize(prefSize);
		passwordText.setPreferredSize(prefSize);
		cnst.gridx = 1;
		cnst.gridy = 0;
		panel_2.add(usernameLabel, cnst);
		cnst.gridy = 1;
		panel_2.add(passwordLabel, cnst);
		cnst.gridx = 2;
		cnst.gridy = 0;
		panel_2.add(usernameText, cnst);
		cnst.gridy = 1;
		panel_2.add(passwordText, cnst);
		cnst.gridx = 1;
		cnst.gridy = 2;
		panel_2.add(loginButton, cnst);
		cnst.gridx = 2;
		panel_2.add(signupButton, cnst);
		forgotPass = new JButton("forgot password?");
		forgotPass.setFocusPainted(true);
		forgotPass.setFocusable(true);
		forgotPass.setForeground(Color.BLUE);
		forgotPass.setBackground(Color.white);
		cnst.gridx = 2;
		cnst.gridy = 3;
		panel_2.add(forgotPass, cnst);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand().equals(loginButton.getActionCommand())){
			long userid;
			try {
				userid = ProfileManager.validateCredentials(usernameText.getText(),String.valueOf(passwordText.getPassword()));
				if(!(userid < 0)){ //need to change to not null
					PersonType type = ProfileManager.getPersonType(userid);
					//passing user data into CustomerView
					this.frame.dispose();
					switch(type.name()){
						case "NOT_A_PERSON":
							return;
						case "CUSTOMER":
							CustomerView cus = new CustomerView(userid);
							break;
						case "EMPLOYEE":
							String[] title = {"job_title"};
							Map<String, Object> empData = ProfileManager.getEmployeeInfo(userid, Arrays.asList(title));
							EmployeeView emp = new EmployeeView(userid, empData.get("job_title").toString().contains("Manager"));
							emp.run();
							break;
					}
				}
			} catch (NoSuchAlgorithmException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		String cmd = e.getActionCommand();
		if(e.getActionCommand().equals(signupButton.getActionCommand())){
			SignupView signup = new SignupView();
		}
		if(e.getActionCommand().equals(forgotPass.getActionCommand())){
			try {
				if(usernameText.getText() != ""){
					long id = ProfileManager.getPersonID(usernameText.getText());
					ProfileManager.changePassword(id, "password");
				}
			} catch (NoSuchAlgorithmException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}

}


