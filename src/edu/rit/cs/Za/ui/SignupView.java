package edu.rit.cs.Za.ui;

/**
 * SignupView.java
 * Contributor(s):  Yihao Cheng (yc7816@rit.edu)
 */

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.FlowLayout;
import java.awt.Insets;
import javax.swing.BorderFactory;
import java.awt.event.ActionListener;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.awt.event.ActionEvent;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerDateModel;

import edu.rit.cs.Za.ProfileManager;

import javax.swing.JComboBox;

public class SignupView {

	private String[] profileFields= new String[]{
			"First Name:",
			"Middle Name:",
			"Last Name:",
			"Date of Birth:",
			"Username:",
			"Password:",
			"Address:",
			"City:",
			"State:",
			"Zipcode:",
	};
	
	private JFrame frame;
	private JButton signUpButton;
	private JButton cancelButton;
	private JPanel profilePanel;
	private JSpinner DOBSpinner;
	private JTextField[] arrayTextField;
	private JPasswordField pwField;
	//private JScrollPane profileScollPane;
	
	JPanel phoneNumberPanel;
	JComboBox<String> phoneNumberComboBox;
	JTextField phoneNumberTextField;
	JButton addPhoneNumberButton;
	JButton removePhoneNumberButton;
	JLabel phoneNumberLabel;
	
	JPanel emailPanel;
	JComboBox<String> emailComboBox;
	JTextField emailTextField;
	JButton addEmailButton;
	JButton removeEmailButton;
	JLabel emailLabel;
	
	/**
	 * SignupView: Constructor
	 */
	public SignupView(){
		initialize();
		this.frame.setVisible(true);
	}
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args)
	{
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
	
	/**
	 * initEmailPanel: initializing the feature for adding multiple email.
	 */
	private void initEmailPanel(){
		emailPanel = new JPanel(new GridBagLayout());
	    
		emailComboBox = new JComboBox<String>();
		emailComboBox.setEnabled(false);
        
		emailTextField = new JTextField();
        
		addEmailButton = new JButton("Add");
		addEmailButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent evt)
            {
                String phoneNumber = emailTextField.getText();
                emailTextField.setText("");
                
                for (int i = 0; i < emailComboBox.getItemCount(); ++i)
                {
                    String s = emailComboBox.getItemAt(i);
                    if (s.equals(phoneNumber)) return;
                }
                
                emailComboBox.addItem(phoneNumber);
                if (!emailComboBox.isEnabled())
                	emailComboBox.setEnabled(true);
                if (!removeEmailButton.isEnabled())
                	removeEmailButton.setEnabled(true);
            }
        });
        
		removeEmailButton = new JButton("Remove");
		removeEmailButton.setEnabled(false);
		removeEmailButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent evt)
            {
                if (emailComboBox.getItemCount() < 1) return;
                emailComboBox.removeItemAt(emailComboBox.getSelectedIndex());
                if (emailComboBox.getItemCount() < 1)
                {
                	emailComboBox.setEnabled(false);
                	removeEmailButton.setEnabled(false);
                }
            }
        });
        
		emailLabel = new JLabel("Email Address:");
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.gridwidth = 1; gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.LINE_END;
        gbc.weightx = 0.0; gbc.weighty = 1.0;
        gbc.ipadx = 2; gbc.ipady = 2;
        gbc.insets = new Insets(4, 4, 4, 4);
        emailPanel.add(emailLabel, gbc);
        
        gbc = new GridBagConstraints();
        gbc.gridx = 1; gbc.gridy = 0;
        gbc.gridwidth = 1; gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 0.5; gbc.weighty = 1.0;
        gbc.ipadx = 2; gbc.ipady = 2;
        gbc.insets = new Insets(4, 4, 4, 4);
        emailPanel.add(emailTextField, gbc);
        
        gbc = new GridBagConstraints();
        gbc.gridx = 2; gbc.gridy = 0;
        gbc.gridwidth = 1; gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.LINE_START;
        gbc.weightx = 0.0; gbc.weighty = 1.0;
        gbc.ipadx = 2; gbc.ipady = 2;
        gbc.insets = new Insets(4, 4, 4, 4);
        emailPanel.add(addEmailButton, gbc);
        
        gbc = new GridBagConstraints();
        gbc.gridx = 1; gbc.gridy = 1;
        gbc.gridwidth = 1; gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 0.5; gbc.weighty = 1.0;
        gbc.ipadx = 2; gbc.ipady = 2;
        gbc.insets = new Insets(4, 4, 4, 4);
        emailPanel.add(emailComboBox, gbc);
        
        gbc = new GridBagConstraints();
        gbc.gridx = 2; gbc.gridy = 1;
        gbc.gridwidth = 1; gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.LINE_START;
        gbc.weightx = 0.0; gbc.weighty = 1.0;
        gbc.ipadx = 2; gbc.ipady = 2;
        gbc.insets = new Insets(4, 4, 4, 4);
        emailPanel.add(removeEmailButton, gbc);
	}
	
	/**
	 * initPhoneNumberPanel: initializing the feature for adding multiple phone number.
	 */
	private void initPhoneNumberPanel()
	{
	    phoneNumberPanel = new JPanel(new GridBagLayout());
	    
        phoneNumberComboBox = new JComboBox<String>();
        phoneNumberComboBox.setEnabled(false);
        
        phoneNumberTextField = new JTextField();
        
        addPhoneNumberButton = new JButton("Add");
        addPhoneNumberButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent evt)
            {
                String phoneNumber = phoneNumberTextField.getText();
                phoneNumberTextField.setText("");
                
                for (int i = 0; i < phoneNumberComboBox.getItemCount(); ++i)
                {
                    String s = phoneNumberComboBox.getItemAt(i);
                    if (s.equals(phoneNumber)) return;
                }
                
                phoneNumberComboBox.addItem(phoneNumber);
                if (!phoneNumberComboBox.isEnabled())
                    phoneNumberComboBox.setEnabled(true);
                if (!removePhoneNumberButton.isEnabled())
                    removePhoneNumberButton.setEnabled(true);
            }
        });
        
        removePhoneNumberButton = new JButton("Remove");
        removePhoneNumberButton.setEnabled(false);
        removePhoneNumberButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent evt)
            {
                if (phoneNumberComboBox.getItemCount() < 1) return;
                phoneNumberComboBox.removeItemAt(phoneNumberComboBox.getSelectedIndex());
                if (phoneNumberComboBox.getItemCount() < 1)
                {
                    phoneNumberComboBox.setEnabled(false);
                    removePhoneNumberButton.setEnabled(false);
                }
            }
        });
        
        phoneNumberLabel = new JLabel("Phone Number:");
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.gridwidth = 1; gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.LINE_END;
        gbc.weightx = 0.0; gbc.weighty = 1.0;
        gbc.ipadx = 2; gbc.ipady = 2;
        gbc.insets = new Insets(4, 4, 4, 4);
        phoneNumberPanel.add(phoneNumberLabel, gbc);
        
        gbc = new GridBagConstraints();
        gbc.gridx = 1; gbc.gridy = 0;
        gbc.gridwidth = 1; gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 0.5; gbc.weighty = 1.0;
        gbc.ipadx = 2; gbc.ipady = 2;
        gbc.insets = new Insets(4, 4, 4, 4);
        phoneNumberPanel.add(phoneNumberTextField, gbc);
        
        gbc = new GridBagConstraints();
        gbc.gridx = 2; gbc.gridy = 0;
        gbc.gridwidth = 1; gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.LINE_START;
        gbc.weightx = 0.0; gbc.weighty = 1.0;
        gbc.ipadx = 2; gbc.ipady = 2;
        gbc.insets = new Insets(4, 4, 4, 4);
        phoneNumberPanel.add(addPhoneNumberButton, gbc);
        
        gbc = new GridBagConstraints();
        gbc.gridx = 1; gbc.gridy = 1;
        gbc.gridwidth = 1; gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 0.5; gbc.weighty = 1.0;
        gbc.ipadx = 2; gbc.ipady = 2;
        gbc.insets = new Insets(4, 4, 4, 4);
        phoneNumberPanel.add(phoneNumberComboBox, gbc);
        
        gbc = new GridBagConstraints();
        gbc.gridx = 2; gbc.gridy = 1;
        gbc.gridwidth = 1; gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.LINE_START;
        gbc.weightx = 0.0; gbc.weighty = 1.0;
        gbc.ipadx = 2; gbc.ipady = 2;
        gbc.insets = new Insets(4, 4, 4, 4);
        phoneNumberPanel.add(removePhoneNumberButton, gbc);
	}
	
	/**
	 * initialize: initializing the frame.
	 */
	public void initialize()
	{
		frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		JPanel mainPanel = new JPanel(new GridBagLayout());
		profilePanel = new JPanel(new GridBagLayout());
		arrayTextField = new JTextField[profileFields.length];
		SpinnerDateModel model;
		int j = 0;
		for(int i = 0; i < profileFields.length; i++){
		    
		    JLabel attributeLabel = new JLabel(profileFields[i]);
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.gridx = 0; gbc.gridy = i;
	        gbc.gridwidth = 1; gbc.gridheight = 1;
	        gbc.fill = GridBagConstraints.NONE;
	        gbc.anchor = GridBagConstraints.LINE_END;
	        gbc.weightx = 0.0; gbc.weighty = 1.0;
	        gbc.ipadx = 2; gbc.ipady = 2;
	        gbc.insets = new Insets(4, 4, 4, 4);
			profilePanel.add(attributeLabel, gbc);
			
			gbc.gridx = 1;
            gbc.anchor = GridBagConstraints.LINE_START;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.weightx = 1.0;
			if(i == 3)
			{
				Calendar calendar = Calendar.getInstance();
				Date currentDate = calendar.getTime();
				calendar.add(Calendar.YEAR, -120);
				Date firstDate = calendar.getTime();
				calendar.add(Calendar.YEAR, 120);
				calendar.add(Calendar.YEAR, -13);
				Date lastDate = calendar.getTime();
				model = new SpinnerDateModel(lastDate, firstDate, lastDate, Calendar.YEAR);
				DOBSpinner = new JSpinner(model);
				DOBSpinner.setEditor(new JSpinner.DateEditor(DOBSpinner, "MM/dd/yyyy"));
				profilePanel.add(DOBSpinner,gbc);
			}
			else if(i == 5)
			{
				pwField = new JPasswordField();
				pwField.setEchoChar('*');
				profilePanel.add(pwField, gbc);
			}
			else
			{
				arrayTextField[j] = new JTextField();
				profilePanel.add(arrayTextField[j],gbc);
				j++;
			}
		}
		
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0; gbc.gridy = 0;
        gbc.gridwidth = 1; gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.VERTICAL;
        gbc.anchor = GridBagConstraints.PAGE_END;
        gbc.weightx = 1.0; gbc.weighty = 1.0;
        gbc.ipadx = 2; gbc.ipady = 2;
        gbc.insets = new Insets(4, 4, 4, 4);
		mainPanel.add(profilePanel, gbc);
		
		initPhoneNumberPanel();
		gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.gridwidth = 1; gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 1.0; gbc.weighty = 1.0;
        gbc.ipadx = 2; gbc.ipady = 2;
        gbc.insets = new Insets(4, 4, 4, 4);
		mainPanel.add(phoneNumberPanel, gbc);
		
	    initEmailPanel();
		gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 1; gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 1.0; gbc.weighty = 1.0;
        gbc.ipadx = 2; gbc.ipady = 2;
        gbc.insets = new Insets(4, 4, 4, 4);
		mainPanel.add(emailPanel, gbc);
		
		signUpButton = new JButton("Submit");
		signUpButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				Map<String,Object> values = new HashMap<String,Object>();
				values.put("first_name", arrayTextField[0].getText());
				values.put("middle_name", arrayTextField[1].getText());
				values.put("last_name", arrayTextField[2].getText());
				values.put("date_of_birth", (Date)DOBSpinner.getValue());
				values.put("username", arrayTextField[3].getText());
				values.put("street", arrayTextField[4].getText());
				values.put("city", arrayTextField[5].getText());
				values.put("state", arrayTextField[6].getText());
				values.put("zip", arrayTextField[7].getText());
				try {
					if(arrayTextField[6].getText().length() != 2){
						JOptionPane.showMessageDialog(frame, "State must be 2 character!", "Notice", JOptionPane.ERROR_MESSAGE);
						return;
					}
					if(arrayTextField[3].getText().isEmpty()){
						JOptionPane.showMessageDialog(frame, "Username cannot be empty!", "Notice", JOptionPane.ERROR_MESSAGE);
						return;
					}
					if(pwField.getPassword().length == 0){
						JOptionPane.showMessageDialog(frame, "Passowrd cannot be empty!", "Notice", JOptionPane.ERROR_MESSAGE);
						return;
					}
					if(ProfileManager.getPersonID(arrayTextField[3].getText()) != ProfileManager.USERNAME_NOT_IN_TABLE){
						JOptionPane.showMessageDialog(frame, "Username already exist!", "Notice", JOptionPane.ERROR_MESSAGE);
						return;
					}
					ProfileManager.createCustomer(values, String.valueOf(pwField.getPassword()));
					long personId = ProfileManager.getPersonID(arrayTextField[3].getText());
					if(phoneNumberComboBox.getItemCount() != 0){
						for(int i = 0; i < phoneNumberComboBox.getItemCount(); i++){
							ProfileManager.addPhoneNumber(personId, phoneNumberComboBox.getItemAt(i));
						}
					}
					if(emailComboBox.getItemCount() != 0){
						for(int i = 0; i < emailComboBox.getItemCount(); i++){
							ProfileManager.addEmailAddress(personId, emailComboBox.getItemAt(i));
						}
					}
					frame.dispose();
				} catch (NoSuchAlgorithmException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			
		});
		cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				frame.dispose();		
			}
			
		});
		JPanel buttonPanel = new JPanel();
		buttonPanel.add(signUpButton);
		buttonPanel.add(cancelButton);
		
		gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.gridwidth = 1; gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.PAGE_START;
        gbc.weightx = 1.0; gbc.weighty = 1.0;
        gbc.ipadx = 2; gbc.ipady = 2;
        gbc.insets = new Insets(4, 4, 4, 4);
		mainPanel.add(buttonPanel, gbc);
		
		frame.setLayout(new BorderLayout());
		frame.add(mainPanel, BorderLayout.CENTER);
		frame.setSize(new Dimension(800,575));;
		frame.setLocationRelativeTo(null);
		frame.setResizable(true);
	}
}
