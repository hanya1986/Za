package edu.rit.cs.Za;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Calendar;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerDateModel;

public class SignupView {

	private String[] profileFields= new String[]{
			"FirstName",
			"MiddleName",
			"LastName",
			"DOB",
			"Username",
			"Password",
			"Address",
			"City",
			"State",
			"Zip",
			"Email",
			"Phone"
		};
	private JFrame frame;
	private JButton signUpButton;
	private JButton cancelButton;
	private JPanel profilePanel;
	private JSpinner DOBSpinner;
	private JPasswordField pwField;
	private JScrollPane profileScollPane;
	
	public void run(){
		this.frame.setVisible(true);
	}
	public SignupView(){
		initialize();
	}
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					SignupView window = new SignupView();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	public void initialize(){
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);	
		frame.setSize(700, 500);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.getContentPane().setLayout(new BorderLayout());
		profilePanel = new JPanel(new GridBagLayout());
		profileScollPane = new JScrollPane(profilePanel);
		JLabel[] arrayLabel = new JLabel[12];
		JTextField[] arrayTextField = new JTextField[10];
		SpinnerDateModel model;
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		int j = 0;
		for(int i = 0; i < arrayLabel.length; i++){
			arrayLabel[i] = new JLabel(profileFields[i]);
			profilePanel.add(arrayLabel[i],gbc);
			gbc.gridx++;
			if(i == 3){
				Calendar calendar = Calendar.getInstance();
				Date currentDate = calendar.getTime();
				calendar.add(Calendar.YEAR, -100);
				Date firstDate = calendar.getTime();
				calendar.add(Calendar.YEAR, 200);
				Date lastDate = calendar.getTime();
				model = new SpinnerDateModel(currentDate, firstDate, lastDate, Calendar.YEAR);
				DOBSpinner = new JSpinner(model);
				DOBSpinner.setEditor(new JSpinner.DateEditor(DOBSpinner, "MM/dd/yyyy"));
				profilePanel.add(DOBSpinner,gbc);
			}else if(i == 5){
				pwField = new JPasswordField(); 
				profilePanel.add(pwField, gbc);
			}else{
				arrayTextField[j] = new JTextField();
				profilePanel.add(arrayTextField[j],gbc);
				j++;
			}
			gbc.gridy++;
			gbc.gridx--;
		}
		frame.getContentPane().add(profileScollPane, BorderLayout.CENTER);
		signUpButton = new JButton("Submit");
		cancelButton = new JButton("Cancel");
		JPanel buttonPanel = new JPanel();
		buttonPanel.add(signUpButton);
		buttonPanel.add(cancelButton);
		frame.getContentPane().add(buttonPanel, BorderLayout.SOUTH);
	}
}
