package edu.rit.cs.Za.ui;

/**
 * EmployeeView.java
 * Contributor(s):  Yihao Cheng (yc7816@rit.edu)
 *                  Jordan Rosario (jar2119@rit.edu)
 */

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListModel;
import javax.swing.SpinnerDateModel;
import javax.swing.border.EtchedBorder;
import javax.swing.table.DefaultTableModel;

import java.sql.SQLException;

import edu.rit.cs.Za.Queries;

public class EmployeeView {
	
	private String[] profileFields = new String[]{
			"FirstName",
			"MiddleName",
			"LastName",
			"DOB",
			"Address",
			"City",
			"State",
			"Zip",
			"SSN",
			"Hourly Rate",
			"Hours/Week",
			"Date Hired",
			"Date Terminated",
			"Job Title"
		};
	private String[] CustomerProfileFields = new String[]{
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

	private String[] itemFields = new String[]{
			"Name:",
			"Type:",
			"Price:",
			"estimate time(Min):",
			"Availabe:",
			"Small price:",
			"Medium price:",
			"Large price:"
	};
	
	private enum typeFrame{
		addEmployee,
		modifyEmployee,
		addItem,
		modifyitem
	}
	
	private JButton signUpButton;
	private JButton cancelButton;
	private JPanel cutomerProfilePanel;
	private JPanel mainPanel;
	private JSpinner customerDOBSpinner;
	private JPasswordField pwField;
	
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
	
	private JFrame frame;
	private JMenuBar menuBar;
	private JPanel bottomPanel;
	private JTable menuTable;
	private JTable carTable;
	private JPanel createOrderPanel;
	private JPanel orderButtonPanel;
	private JPanel profilePanel;
	private JPanel ordersPanel;
	private JPanel logsPanel;
	private JPanel statPanel;
	private JSpinner DOBSpinner;
	private JScrollPane profileScollPane;
	private JTable logsTable;
	private JTable statTable;
	private JSpinner fromSpinner;
	private JSpinner toSpinner;
	private JTextField searchTextField;
	private JPanel manageEmpPanel;
	private JTable manageEmpTable;
	private JPanel manageMenuPanel;
	private JTable manageMenuTable;
	private JPanel addEmpPanel;
	private JPanel modifyEmpPanel;
	private JPanel addItemPanel;
	private JPanel modifyItemPanel;
	
	private JTextField qsItemNameTextField;
	private JTextField qsQuantityTextField;
	private JButton qsRefreshButton;
	
	/**
	 * run: Show the frame
	 */
	public void run(){
		this.frame.setVisible(true);
	}
	
	/**
	 * EmployeeView: Constructor.
	 */
	public EmployeeView(){
		initialize();
		initializeOrdersView();
	}
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					EmployeeView window = new EmployeeView();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	/**
	 * initialize: initializing the frame and the tool bar.
	 */
	private void initialize(){
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setSize(new Dimension(1100,600));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new BorderLayout());
		
		orderButtonPanel = new JPanel();
		orderButtonPanel.setLayout(new GridLayout(1,1,0,0));
		frame.getContentPane().add(orderButtonPanel, BorderLayout.NORTH);
		
		menuBar = new JMenuBar();
		orderButtonPanel.add(menuBar);
		
		JButton ordersButton = new JButton("Orders");
		ordersButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				loadOrdersView();
				frame.revalidate();
			}
		});
		menuBar.add(ordersButton);
		
		JButton createOrderButton = new JButton("Create Orders");
		createOrderButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				loadCreateOrderView();
				frame.revalidate();
			}
		});
		menuBar.add(createOrderButton);
		
		JButton createCustomerButton = new JButton("Create Customer Profile");
		createCustomerButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				loadCreateCustomerView();
				frame.revalidate();
				
			}
			
		});
		menuBar.add(createCustomerButton);
		
		initializeManagerView();
		
		JButton profileButton = new JButton("My Profile");
		profileButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				loadProfileView();
				frame.revalidate();
			}
		});
		menuBar.add(profileButton);
		
		JButton logoutButton = new JButton("Logout");
		logoutButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				frame.dispose();
				LoginView login = new LoginView();
			}
		});
		menuBar.add(logoutButton);
		
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
	 * initializeCreateCustomerView: initialize the create customer UI.
	 */
	public void initializeCreateCustomerView(){
		initPhoneNumberPanel();
		initEmailPanel();
		mainPanel = new JPanel(new GridBagLayout());
		//mainPanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
		cutomerProfilePanel = new JPanel(new GridBagLayout());
		//profileScollPane = new JScrollPane(profilePanel);
		//JLabel[] arrayLabel = new JLabel[profileFields.length];
		JTextField[] arrayTextField = new JTextField[CustomerProfileFields.length];
		SpinnerDateModel model;
		for(int i = 0; i < CustomerProfileFields.length; i++){
		    
			//arrayLabel[i] = new JLabel(profileFields[i]);
		    JLabel attributeLabel = new JLabel(CustomerProfileFields[i]);
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.gridx = 0; gbc.gridy = i;
	        gbc.gridwidth = 1; gbc.gridheight = 1;
	        gbc.fill = GridBagConstraints.NONE;
	        gbc.anchor = GridBagConstraints.LINE_END;
	        gbc.weightx = 0.0; gbc.weighty = 1.0;
	        gbc.ipadx = 2; gbc.ipady = 2;
	        gbc.insets = new Insets(4, 4, 4, 4);
	        cutomerProfilePanel.add(attributeLabel, gbc);
			
			gbc.gridx = 1;
            gbc.anchor = GridBagConstraints.LINE_START;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.weightx = 1.0;
			if(i == 3)
			{
				Calendar calendar = Calendar.getInstance();
				calendar.add(Calendar.YEAR, -120);
				Date firstDate = calendar.getTime();
				calendar.add(Calendar.YEAR, 120);
				calendar.add(Calendar.YEAR, -13);
				Date lastDate = calendar.getTime();
				model = new SpinnerDateModel(lastDate, firstDate, lastDate, Calendar.YEAR);
				customerDOBSpinner = new JSpinner(model);
				customerDOBSpinner.setEditor(new JSpinner.DateEditor(customerDOBSpinner, "MM/dd/yyyy"));
				cutomerProfilePanel.add(customerDOBSpinner,gbc);
			}
			else if(i == 5)
			{
				pwField = new JPasswordField();
				cutomerProfilePanel.add(pwField, gbc);
			}
			else
			{
				arrayTextField[i] = new JTextField();
				cutomerProfilePanel.add(arrayTextField[i],gbc);
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
		mainPanel.add(cutomerProfilePanel, gbc);
		
		gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.gridwidth = 1; gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 1.0; gbc.weighty = 1.0;
        gbc.ipadx = 2; gbc.ipady = 2;
        gbc.insets = new Insets(4, 4, 4, 4);
		mainPanel.add(phoneNumberPanel, gbc);
		
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
		cancelButton = new JButton("Cancel");
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
		
		frame.getContentPane().add(mainPanel, BorderLayout.CENTER);
	}
	
	/**
	 * initializeProfileView: initialize the employee profile UI.
	 */
	public void initializeProfileView(){
		profilePanel = new JPanel(new GridBagLayout());
		JPanel profilePhonePanel = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0; gbc.gridy = 0;
        gbc.gridwidth = 1; gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.VERTICAL;
        gbc.anchor = GridBagConstraints.PAGE_END;
        gbc.weightx = 1.0; gbc.weighty = 1.0;
        gbc.ipadx = 2; gbc.ipady = 2;
        gbc.insets = new Insets(4, 4, 4, 4);
        profilePhonePanel.add(profilePanel, gbc);
        profileScollPane = new JScrollPane(profilePhonePanel);
		JLabel[] arrayLabel = new JLabel[profileFields.length];
		JTextField[] arrayTextField = new JTextField[profileFields.length - 1];
		SpinnerDateModel model;
		gbc = new GridBagConstraints();
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
			}else{
				arrayTextField[j] = new JTextField();
				profilePanel.add(arrayTextField[j],gbc);
				j++;
			}
			gbc.gridy++;
			gbc.gridx--;
		}
		initPhoneNumberPanel();
		initEmailPanel();
		gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.gridwidth = 1; gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 1.0; gbc.weighty = 1.0;
        gbc.ipadx = 2; gbc.ipady = 2;
        gbc.insets = new Insets(4, 4, 4, 4);
        profilePhonePanel.add(phoneNumberPanel, gbc);
        
		gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 1; gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 1.0; gbc.weighty = 1.0;
        gbc.ipadx = 2; gbc.ipady = 2;
        gbc.insets = new Insets(4, 4, 4, 4);
        profilePhonePanel.add(emailPanel, gbc);
        
		bottomPanel = new JPanel();
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.fill = GridBagConstraints.VERTICAL;
		JButton updateButton = new JButton("Update");
		gbc.gridx++;
		bottomPanel.add(updateButton, gbc);
		frame.getContentPane().add(bottomPanel, BorderLayout.SOUTH);
		frame.getContentPane().add(profileScollPane, BorderLayout.CENTER);
	}
	
	/**
	 * initializeCreateOrderView: initialize the create order UI.
	 */
	public void initializeCreateOrderView(){
		
		createOrderPanel = new JPanel();
		frame.getContentPane().add(createOrderPanel, BorderLayout.CENTER);
		JButton add = new JButton("Add");
		JButton remove = new JButton("Remove");
		
		createOrderPanel.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		JPanel AddRemovePanel = new JPanel(new GridBagLayout());
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		AddRemovePanel.add(add,gbc);
		gbc.gridy = 1;
		AddRemovePanel.add(remove, gbc);
		
		gbc = new GridBagConstraints();
		gbc.weightx = 0.3;
		gbc.weighty = 0.3;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridx = 0;
		gbc.gridy = 0;
		JLabel menuLabel = new JLabel("Menu");
		createOrderPanel.add(menuLabel, gbc);
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.weightx = 1;
		gbc.weighty = 1;
		MyModel table = populateMenuTable();
		menuTable = new JTable();
		menuTable.setModel(table);
		JScrollPane sp = new JScrollPane(menuTable);
		sp.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
		createOrderPanel.add(sp, gbc);
		
		gbc.weightx = 0.3;
		gbc.weighty = 0.3;
		gbc.gridx++;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		createOrderPanel.add(AddRemovePanel, gbc);
		
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weightx = 0.3;
		gbc.weighty = 0.3;
		gbc.gridx++;
		gbc.gridy = 0;
		JLabel carLabel = new JLabel("Shopping car");
		createOrderPanel.add(carLabel, gbc);
		gbc.gridy++;
		gbc.weightx = 1;
		gbc.weighty = 1;
		String[] columns = { "Order ID", "Customer ID", "Time Placed", "Time Order out", "Subtotal", "Tax", "Total" };
		carTable = populateTable(columns);
		sp = new JScrollPane(carTable);
		sp.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
		createOrderPanel.add(sp, gbc);
		
		bottomPanel = new JPanel(new BorderLayout());
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.fill = GridBagConstraints.VERTICAL;
		JButton resetButton = new JButton("Reset");
		gbc.gridx++;
		JPanel centerBottomPanel = new JPanel();
		centerBottomPanel.add(resetButton);
		bottomPanel.add(centerBottomPanel, BorderLayout.CENTER);
		JButton pastButton = new JButton("Past Orders");
		pastButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				PastOrderView pastWindow = new PastOrderView();
				pastWindow.runGUI();
			}
			
		});
		gbc.gridx++;
		bottomPanel.add(pastButton, BorderLayout.LINE_END);
		JButton placeButton = new JButton("Place Order");
		placeButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				PaymentView pv = new PaymentView(true);
				pv.runGUI();
			}
			
		});
		gbc.gridx++;
		bottomPanel.add(placeButton, BorderLayout.LINE_START);
		frame.getContentPane().add(bottomPanel, BorderLayout.SOUTH);
	}
	
	/**
	 * initializeOrdersView: initialize the Order UI for employee
	 */
	public void initializeOrdersView(){
		
		ordersPanel = new JPanel();
		frame.getContentPane().add(ordersPanel, BorderLayout.CENTER);
		
		ordersPanel.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		
		gbc.weightx = 0.3;
		gbc.weighty = 0.3;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridx = 0;
		gbc.gridy = 0;
		JLabel menuLabel = new JLabel("Orders");
		ordersPanel.add(menuLabel, gbc);
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.weightx = 1;
		gbc.weighty = 1;
		String[] columns = { "Name", "Type", "Price", "Estimate Time", "Quantity" };
		carTable = populateTable(columns);
		JScrollPane sp = new JScrollPane(carTable);
		sp.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
		ordersPanel.add(sp, gbc);
		
		bottomPanel = new JPanel(new BorderLayout());
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.fill = GridBagConstraints.VERTICAL;
		JButton deliveredButton = new JButton("Delivered");
		gbc.gridx++;
		JPanel centerBottomPanel = new JPanel();
		centerBottomPanel.add(deliveredButton);
		bottomPanel.add(centerBottomPanel, BorderLayout.CENTER);
		JButton pastButton = new JButton("Past Orders");
		pastButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				PastOrderView pastWindow = new PastOrderView();
				pastWindow.runGUI();
			}
			
		});
		gbc.gridx++;
		bottomPanel.add(pastButton, BorderLayout.LINE_END);
		JButton cancelButton = new JButton("Cancel Order");
		cancelButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
			}
			
		});
		gbc.gridx++;
		bottomPanel.add(cancelButton, BorderLayout.LINE_START);
		frame.getContentPane().add(bottomPanel, BorderLayout.SOUTH);
	}
	
	/**
	 * initializeManagerView: initialize the manager UI
	 */
	public void initializeManagerView(){
		JButton logButton = new JButton("Logs");
		logButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				loadLogsView();
				frame.revalidate();
			}
			
		});
		menuBar.add(logButton);
		
		JButton statButton = new JButton("statistics");
		statButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				loadStatView();
				frame.revalidate();
			}
			
		});
		menuBar.add(statButton);
		
		JButton manageEmpButton = new JButton("Manage employees");
		manageEmpButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				loadManageEmpView();
				frame.revalidate();
			}
			
		});
		menuBar.add(manageEmpButton);
		
		JButton manageMenuButton = new JButton("Manage menu");
		manageMenuButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				loadManageMenuView();
				frame.revalidate();
			}
			
		});
		menuBar.add(manageMenuButton);
	}
	
	/**
	 * initializeLogsView: initialize the Logs UI
	 */
	public void initializeLogsView(){
		logsPanel = new JPanel(new BorderLayout());
		String[] columns = { "Log", "Type", "Time"};
		logsTable = new JTable();
		logsTable.setModel(populateStatLogsTable(columns));
		JScrollPane sp = new JScrollPane(logsTable);
		logsPanel.add(sp, BorderLayout.CENTER);
		frame.getContentPane().add(logsPanel, BorderLayout.CENTER);
	}
	
	private JPanel quantitySoldPanel;
	private JPanel deliveryTimePanel;
	
	private void initQuantitySoldPanel()
	{
	    GridBagConstraints gbc;
	    quantitySoldPanel = new JPanel(new GridBagLayout());
        qsItemNameTextField = new JTextField();
        qsItemNameTextField.setToolTipText("The case sensitive name of the item.");
        JPanel qsQuantityPanel = new JPanel();
        qsQuantityTextField = new JTextField();
        qsQuantityTextField.setEditable(false);
        qsRefreshButton = new JButton("Refresh");
        qsRefreshButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e)
            {
                String itemName = qsItemNameTextField.getText();
                java.sql.Date start = new java.sql.Date(((Date)fromSpinner.getValue()).getTime());
                java.sql.Date end = new java.sql.Date(((Date)toSpinner.getValue()).getTime());
                long qSold;
                try
                {
                    qSold = Queries.getQuantitySold(itemName, start, end);
                }
                catch (SQLException ex)
                {
                    JOptionPane.showMessageDialog(null,
                            "A database error occurred:\n"+ ex.getMessage(),
                            "Quantity Sold",
                            JOptionPane.ERROR_MESSAGE);
                    qsQuantityTextField.setText("");
                    return;
                }
                qsQuantityTextField.setText(Long.toString(qSold));
            }
        });
        
        gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.gridwidth = 1; gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.ipadx = 4; gbc.ipady = 4;
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 0.0; gbc.weighty = 1.0;
        quantitySoldPanel.add(new JLabel("Item Name:"), gbc);
        
        gbc = new GridBagConstraints();
        gbc.gridx = 1; gbc.gridy = 0;
        gbc.gridwidth = 1; gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipadx = 4; gbc.ipady = 4;
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 1.0; gbc.weighty = 1.0;
        quantitySoldPanel.add(qsItemNameTextField, gbc);
        
        gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.gridwidth = 1; gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipadx = 4; gbc.ipady = 4;
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 0.0; gbc.weighty = 1.0;
        quantitySoldPanel.add(new JLabel("Quantity:"), gbc);
        
        gbc = new GridBagConstraints();
        gbc.gridx = 1; gbc.gridy = 1;
        gbc.gridwidth = 1; gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipadx = 4; gbc.ipady = 4;
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 1.0; gbc.weighty = 1.0;
        quantitySoldPanel.add(qsQuantityTextField, gbc);
        
        gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 2; gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipadx = 4; gbc.ipady = 4;
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 1.0; gbc.weighty = 1.0;
        quantitySoldPanel.add(qsRefreshButton, gbc);
        
        quantitySoldPanel.setBorder(
                BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), 
                        "Quantity Sold"));
        
        gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.gridwidth = 1; gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipadx = 4; gbc.ipady = 4;
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 1.0; gbc.weighty = 1.0;
        statPanel.add(quantitySoldPanel, gbc);
	}
	
	private JTextField dtEmpIdTextField;
	private JTextField dtAvgTimeTextField;
	private JButton dtRefreshButton;
	
	private void initDeliveryTimePanel()
    {
        GridBagConstraints gbc;
        deliveryTimePanel = new JPanel(new GridBagLayout());
        dtEmpIdTextField = new JTextField();
        dtAvgTimeTextField = new JTextField();
        dtAvgTimeTextField.setEditable(false);
        dtRefreshButton = new JButton("Refresh");
        dtRefreshButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e)
            {
                long empID;
                try
                {
                    empID = Long.parseLong(dtEmpIdTextField.getText());
                }
                catch (NumberFormatException ex)
                {
                    JOptionPane.showMessageDialog(null,
                            "Invalid employee ID.",
                            "Delivery Time",
                            JOptionPane.ERROR_MESSAGE);
                    dtAvgTimeTextField.setText("");
                    return;
                    
                }
                
                java.sql.Date start = new java.sql.Date(((Date)fromSpinner.getValue()).getTime());
                java.sql.Date end = new java.sql.Date(((Date)toSpinner.getValue()).getTime());
                long avgDeliveryTime;
                try
                {
                    avgDeliveryTime = Queries.getAverageDeliveryTime(empID);
                }
                catch (SQLException ex)
                {
                    JOptionPane.showMessageDialog(null,
                            "A database error occurred:\n" + ex.getMessage(),
                            "Average Delivery Time",
                            JOptionPane.ERROR_MESSAGE);
                    dtAvgTimeTextField.setText("");
                    return;
                }
                
                long minutes = TimeUnit.MILLISECONDS.toMinutes(avgDeliveryTime);
                long seconds = TimeUnit.MILLISECONDS.toSeconds(avgDeliveryTime - TimeUnit.MINUTES.toMillis(minutes));
                
                dtAvgTimeTextField.setText(String.format("%02d:%02d", minutes, seconds));
            }
        });
        
        gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.gridwidth = 1; gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.ipadx = 4; gbc.ipady = 4;
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 0.0; gbc.weighty = 1.0;
        deliveryTimePanel.add(new JLabel("Employee ID:"), gbc);
        
        gbc = new GridBagConstraints();
        gbc.gridx = 1; gbc.gridy = 0;
        gbc.gridwidth = 1; gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipadx = 4; gbc.ipady = 4;
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 1.0; gbc.weighty = 1.0;
        deliveryTimePanel.add(dtEmpIdTextField, gbc);
        
        gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.gridwidth = 1; gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipadx = 4; gbc.ipady = 4;
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 0.0; gbc.weighty = 1.0;
        deliveryTimePanel.add(new JLabel("Average Time:"), gbc);
        
        gbc = new GridBagConstraints();
        gbc.gridx = 1; gbc.gridy = 1;
        gbc.gridwidth = 1; gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipadx = 4; gbc.ipady = 4;
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 1.0; gbc.weighty = 1.0;
        deliveryTimePanel.add(dtAvgTimeTextField, gbc);
        
        gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 2; gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipadx = 4; gbc.ipady = 4;
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 1.0; gbc.weighty = 1.0;
        deliveryTimePanel.add(dtRefreshButton, gbc);
        
        deliveryTimePanel.setBorder(
                BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), 
                        "Average Delivery Time"));
        
        gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 1; gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipadx = 4; gbc.ipady = 4;
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 1.0; gbc.weighty = 1.0;
        statPanel.add(deliveryTimePanel, gbc);
    }
	
	private JPanel topItemsPanel;
	private JTextField nItemsTextField;
	private JButton nItemsRefreshButton;
	private JList nItemsList;
	
	private void initTopItemsPanel()
	{
	    GridBagConstraints gbc;
	    
	    topItemsPanel = new JPanel(new GridBagLayout());
	    nItemsTextField = new JTextField();
	    nItemsList = new JList<String>();
	    nItemsRefreshButton = new JButton("Refresh");
	    nItemsRefreshButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e)
            {
                int n;
                try
                {
                    n = Integer.parseInt(nItemsTextField.getText());
                }
                catch (NumberFormatException ex)
                {
                    JOptionPane.showMessageDialog(null,
                            "Number of items must be an integer greater than or equal to 1.",
                            "Top Items",
                            JOptionPane.ERROR_MESSAGE);
                    nItemsTextField.setText("");
                    nItemsList.setModel(new DefaultListModel<String>());
                    return;
                }
                
                if (n < 1)
                {
                    JOptionPane.showMessageDialog(null,
                            "Number of items must be an integer greater than or equal to 1.",
                            "Top Items",
                            JOptionPane.ERROR_MESSAGE);
                    nItemsTextField.setText("");
                    nItemsList.setModel(new DefaultListModel<String>());
                    return;
                }
                
                Map<String,Integer> topItems;
                try
                {
                    topItems = Queries.getTopNItems(n);
                }
                catch (SQLException ex)
                {
                    JOptionPane.showMessageDialog(null,
                            "A database error occurred:\n" + ex.getMessage(),
                            "Top Items",
                            JOptionPane.ERROR_MESSAGE);
                    nItemsTextField.setText("");
                    nItemsList.setModel(new DefaultListModel<String>());
                    return;
                }
                
                DefaultListModel<String> nItemsModel = new DefaultListModel<String>();
                for (String itemName : topItems.keySet())
                    nItemsModel.addElement(itemName);
                nItemsList.setModel(nItemsModel);
            }
        });
	    
	    gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.gridwidth = 1; gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.ipadx = 4; gbc.ipady = 4;
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 0.0; gbc.weighty = 1.0;
        topItemsPanel.add(new JLabel("Number of Items:"), gbc);
        
        gbc = new GridBagConstraints();
        gbc.gridx = 1; gbc.gridy = 0;
        gbc.gridwidth = 1; gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipadx = 4; gbc.ipady = 4;
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 0.5; gbc.weighty = 1.0;
        topItemsPanel.add(nItemsTextField, gbc);
        
        gbc = new GridBagConstraints();
        gbc.gridx = 2; gbc.gridy = 0;
        gbc.gridwidth = 1; gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipadx = 4; gbc.ipady = 4;
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 0.5; gbc.weighty = 1.0;
        topItemsPanel.add(nItemsRefreshButton, gbc);
        
        JScrollPane nItemsScrollPane = new JScrollPane(nItemsList,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.gridwidth = 3; gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.ipadx = 4; gbc.ipady = 4;
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 1.0; gbc.weighty = 0.5;
        topItemsPanel.add(nItemsScrollPane, gbc);
        
        topItemsPanel.setBorder(
                BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), 
                        "Top Items"));
        
        gbc = new GridBagConstraints();
        gbc.gridx = 1; gbc.gridy = 1;
        gbc.gridwidth = 1; gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.ipadx = 4; gbc.ipady = 4;
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 1.0; gbc.weighty = 1.0;
        statPanel.add(topItemsPanel, gbc);
	}
	
	private JPanel bestCustomerPanel;
	private JTextField nCustomersTextField;
	private JList<Long> nCustomersList;
	private JButton nCustomersRefreshButton;
	
	private void initBestCustomersPanel()
    {
        GridBagConstraints gbc;
        
        bestCustomerPanel = new JPanel(new GridBagLayout());
        nCustomersTextField = new JTextField();
        nCustomersList = new JList<Long>();
        nCustomersRefreshButton = new JButton("Refresh");
        nCustomersRefreshButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e)
            {
                int n;
                try
                {
                    n = Integer.parseInt(nCustomersTextField.getText());
                }
                catch (NumberFormatException ex)
                {
                    JOptionPane.showMessageDialog(null,
                            "Number of customers must be an integer greater than or equal to 1.",
                            "Best Customers",
                            JOptionPane.ERROR_MESSAGE);
                    nCustomersTextField.setText("");
                    nCustomersList.setModel(new DefaultListModel<Long>());
                    return;
                }
                
                if (n < 1)
                {
                    JOptionPane.showMessageDialog(null,
                            "Number of customers must be an integer greater than or equal to 1.",
                            "Best Customers",
                            JOptionPane.ERROR_MESSAGE);
                    nCustomersTextField.setText("");
                    nCustomersList.setModel(new DefaultListModel<Long>());
                    return;
                }
                
                Map<Long,Long> bestCustomers;
                try
                {
                    bestCustomers = Queries.getFrequentCustomers(n);
                }
                catch (SQLException ex)
                {
                    JOptionPane.showMessageDialog(null,
                            "A database error occurred:\n" + ex.getMessage(),
                            "Best Customers",
                            JOptionPane.ERROR_MESSAGE);
                    nCustomersTextField.setText("");
                    nCustomersList.setModel(new DefaultListModel<Long>());
                    return;
                }
                
                DefaultListModel<Long> nCustomersModel = new DefaultListModel<Long>();
                for (Long custID : bestCustomers.keySet())
                    nCustomersModel.addElement(custID);
                nCustomersList.setModel(nCustomersModel);
            }
        });
        
        gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.gridwidth = 1; gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.ipadx = 4; gbc.ipady = 4;
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 0.0; gbc.weighty = 1.0;
        bestCustomerPanel.add(new JLabel("Number of Customers:"), gbc);
        
        gbc = new GridBagConstraints();
        gbc.gridx = 1; gbc.gridy = 0;
        gbc.gridwidth = 1; gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipadx = 4; gbc.ipady = 4;
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 0.5; gbc.weighty = 1.0;
        bestCustomerPanel.add(nCustomersTextField, gbc);
        
        gbc = new GridBagConstraints();
        gbc.gridx = 2; gbc.gridy = 0;
        gbc.gridwidth = 1; gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipadx = 4; gbc.ipady = 4;
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 0.5; gbc.weighty = 1.0;
        bestCustomerPanel.add(nCustomersRefreshButton, gbc);
        
        JScrollPane nCustomersScrollPane = new JScrollPane(nCustomersList,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.gridwidth = 3; gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.ipadx = 4; gbc.ipady = 4;
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 1.0; gbc.weighty = 0.5;
        bestCustomerPanel.add(nCustomersScrollPane, gbc);
        
        bestCustomerPanel.setBorder(
                BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), 
                        "Best Customers"));
        
        gbc = new GridBagConstraints();
        gbc.gridx = 2; gbc.gridy = 1;
        gbc.gridwidth = 1; gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.ipadx = 4; gbc.ipady = 4;
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 1.0; gbc.weighty = 1.0;
        statPanel.add(bestCustomerPanel, gbc);
    }
	
	/**
	 * initializeStatView: initialize the statistics UI
	 */
	public void initializeStatView()
	{
	    GridBagConstraints gbc;
		//statPanel = new JPanel(new BorderLayout());
	    statPanel = new JPanel(new GridBagLayout());
		//String[] columns = { "Log", "Type", "Time"};
		//statTable = new JTable(populateStatLogsTable(columns));
		//JScrollPane sp = new JScrollPane(statTable);
		//statPanel.add(sp, BorderLayout.CENTER);
		
		//JPanel topPanel = new JPanel();
		JPanel topPanel = new JPanel();
		//JComboBox<String> selectionComboBox = new JComboBox<String>();
		//selectionComboBox.setEnabled(true);
		//selectionComboBox.addItem("Store");
		//selectionComboBox.addItem("Employee");
		//selectionComboBox.addItem("Product");
		//topPanel.add(selectionComboBox);
		
		JLabel fromLabel = new JLabel("From:");
		topPanel.add(fromLabel);
		Calendar calendar = Calendar.getInstance();
		Date currentDate = calendar.getTime();
		calendar.add(Calendar.YEAR, -100);
		Date firstDate = calendar.getTime();
		calendar.add(Calendar.YEAR, 200);
		Date lastDate = calendar.getTime();
		SpinnerDateModel currentModel = new SpinnerDateModel(currentDate, firstDate, lastDate, Calendar.YEAR);
		fromSpinner = new JSpinner(currentModel);
		fromSpinner.setEditor(new JSpinner.DateEditor(fromSpinner, "MM/dd/yyyy"));
		topPanel.add(fromSpinner);
		
		JLabel toLabel = new JLabel("To:");
		topPanel.add(toLabel);
		
		toSpinner = new JSpinner(currentModel);
		toSpinner.setEditor(new JSpinner.DateEditor(toSpinner, "MM/dd/yyyy"));
		topPanel.add(toSpinner);
		
		//JLabel Search = new JLabel("      Search:");
		//topPanel.add(Search);
		//searchTextField = new JTextField();
		//searchTextField.setPreferredSize(new Dimension(140,20));
		//topPanel.add(searchTextField);
		
		//statPanel.add(topPanel, BorderLayout.NORTH);
		gbc = new GridBagConstraints();
		gbc.gridx = 0; gbc.gridy = 0;
		gbc.gridwidth = 5; gbc.gridheight = 1;
		gbc.fill = GridBagConstraints.NONE;
		gbc.ipadx = 4; gbc.ipady = 4;
		gbc.insets = new Insets(4, 4, 4, 4);
		gbc.anchor = GridBagConstraints.PAGE_END;
		gbc.weightx = 1.0; gbc.weighty = 1.0;
		statPanel.add(topPanel, gbc);
		
		initQuantitySoldPanel();
		initDeliveryTimePanel();
		initTopItemsPanel();
		initBestCustomersPanel();
		
		frame.getContentPane().add(statPanel, BorderLayout.CENTER);
	}
	
	/**
	 * initializeManageEmpView: initialize the managing employee UI
	 */
	public void initializeManageEmpView(){
		manageEmpPanel = new JPanel(new BorderLayout());
		String[] columns = { "Employee ID", "Employee Name", "States"};
		manageEmpTable = new JTable();
		manageEmpTable.setModel(populateStatLogsTable(columns));
		JScrollPane sp = new JScrollPane(manageEmpTable);
		manageEmpPanel.add(sp, BorderLayout.CENTER);
		JPanel buttonPanel = new JPanel();
		JButton addEmpButton = new JButton("Add employee");
		addEmpButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				initializeAddModifyView(typeFrame.addEmployee);
			}
			
		});
		buttonPanel.add(addEmpButton);
		JButton removeButton = new JButton("Remove employee");
		removeButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				Object[] options = {"No, keep it", "Yes, remove it"};
				int n = JOptionPane.showOptionDialog(frame,
						"Would you like to remove the employee?",
						"Confirm",
						JOptionPane.YES_NO_OPTION,
						JOptionPane.QUESTION_MESSAGE,
						null,        //do not use a custom Icon
						options,     //the titles of buttons
						options[0]); //default button title
			}
			
		});
		buttonPanel.add(removeButton);
		JButton modifyButton = new JButton("Modify employee");
		modifyButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				initializeAddModifyView(typeFrame.modifyEmployee);
			}
			
		});
		buttonPanel.add(modifyButton);
		manageEmpPanel.add(buttonPanel, BorderLayout.SOUTH);
		frame.getContentPane().add(manageEmpPanel, BorderLayout.CENTER);
	}
	
	/**
	 * initializeMenuView: initialize the managing menu UI
	 */
	public void initializeMenuView(){
		manageMenuPanel = new JPanel(new BorderLayout());
		String[] columns = { "Item ID", "Item Name", "Item Price"};
		manageMenuTable = new JTable();
		manageMenuTable.setModel(populateStatLogsTable(columns));
		JScrollPane sp = new JScrollPane(manageMenuTable);
		manageMenuPanel.add(sp, BorderLayout.CENTER);
		JPanel buttonPanel = new JPanel();
		JButton addItemButton = new JButton("Add item");
		addItemButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				initializeAddModifyView(typeFrame.addItem);
			}
			
		});
		buttonPanel.add(addItemButton);
		JButton removeButton = new JButton("Remove item");
		removeButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				Object[] options = {"No, keep it", "Yes, remove it"};
				int n = JOptionPane.showOptionDialog(frame,
						"Would you like to remove the item?",
						"Confirm",
						JOptionPane.YES_NO_OPTION,
						JOptionPane.QUESTION_MESSAGE,
						null,        //do not use a custom Icon
						options,     //the titles of buttons
						options[0]); //default button title
			}
			
		});
		buttonPanel.add(removeButton);
		JButton modifyButton = new JButton("Modify item");
		modifyButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				initializeAddModifyView(typeFrame.modifyitem);
			}
			
		});
		buttonPanel.add(modifyButton);
		manageMenuPanel.add(buttonPanel, BorderLayout.SOUTH);
		frame.getContentPane().add(manageMenuPanel, BorderLayout.CENTER);
	}
	
	/**
	 * initializeAddModifyView: initialize the add or modify UI
	 */
	public void initializeAddModifyView(typeFrame viewType){
		JFrame localFrame = new JFrame();
		localFrame.setBounds(100, 100, 450, 300);
		localFrame.setSize(new Dimension(1100,600));
		localFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		localFrame.getContentPane().setLayout(new BorderLayout());
		
		if(viewType == typeFrame.addEmployee){
			initializeEmpMenuProfileView(localFrame, addEmpPanel, viewType, profileFields);
		}
		if(viewType == typeFrame.modifyEmployee){
			initializeEmpMenuProfileView(localFrame, modifyEmpPanel, viewType, profileFields);
		}
		if(viewType == typeFrame.addItem){
			initializeEmpMenuProfileView(localFrame, addItemPanel, viewType, itemFields);
		}
		if(viewType == typeFrame.modifyitem){
			initializeEmpMenuProfileView(localFrame, modifyItemPanel, viewType, itemFields);
		}
		localFrame.setVisible(true);
	}
	
	/**
	 * initializeEmpMenuProfileView: initialize the menu and employee profile UI
	 */
	private void initializeEmpMenuProfileView(JFrame localFrame, JPanel currentPanel, typeFrame viewType, String[] columns){
		currentPanel = new JPanel(new GridBagLayout());
		JPanel profilePhonePanel = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		JPasswordField passwordField;
		JTextField usernameTextField;
		JSpinner localDOB; 
		gbc.gridx = 0; gbc.gridy = 0;
        gbc.gridwidth = 1; gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.VERTICAL;
        gbc.anchor = GridBagConstraints.PAGE_END;
        gbc.weightx = 1.0; gbc.weighty = 1.0;
        gbc.ipadx = 2; gbc.ipady = 2;
        gbc.insets = new Insets(4, 4, 4, 4);
        profilePhonePanel.add(currentPanel, gbc);
        JScrollPane localScollPane = new JScrollPane(profilePhonePanel);
		JLabel[] arrayLabel = new JLabel[columns.length];
		JTextField[] empArrayTextField = new JTextField[columns.length - 1];
		JTextField[] menuArrayTextField = new JTextField[columns.length];
		SpinnerDateModel model;
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		int j = 0;
		if(viewType == typeFrame.addEmployee || viewType == typeFrame.modifyEmployee){
			for(int i = 0; i < arrayLabel.length; i++){
				arrayLabel[i] = new JLabel(columns[i]);
				currentPanel.add(arrayLabel[i],gbc);
				gbc.gridx++;
				if(i == 3){
					Calendar calendar = Calendar.getInstance();
					Date currentDate = calendar.getTime();
					calendar.add(Calendar.YEAR, -100);
					Date firstDate = calendar.getTime();
					calendar.add(Calendar.YEAR, 200);
					Date lastDate = calendar.getTime();
					model = new SpinnerDateModel(currentDate, firstDate, lastDate, Calendar.YEAR);
					localDOB = new JSpinner(model);
					localDOB.setEditor(new JSpinner.DateEditor(localDOB, "MM/dd/yyyy"));
					currentPanel.add(localDOB,gbc);
				}else{
					empArrayTextField[j] = new JTextField();
					currentPanel.add(empArrayTextField[j],gbc);
					j++;
				}
			
				gbc.gridy++;
				gbc.gridx--;
			}
			if(viewType == typeFrame.addEmployee){
				JLabel pwLabel = new JLabel("Password:");
				currentPanel.add(pwLabel, gbc);
				gbc.gridx++;
				passwordField = new JPasswordField();
				currentPanel.add(passwordField, gbc);
				gbc.gridy++;
				gbc.gridx--;
				JLabel usernameLabel = new JLabel("Username:");
				currentPanel.add(usernameLabel, gbc);
				gbc.gridx++;
				usernameTextField = new JTextField();
				currentPanel.add(usernameTextField,gbc);
			}
			gbc.gridy++;
			gbc.gridx--;
			initPhoneNumberPanel();
			gbc = new GridBagConstraints();
	        gbc.gridx = 0; gbc.gridy = 1;
	        gbc.gridwidth = 1; gbc.gridheight = 1;
	        gbc.fill = GridBagConstraints.HORIZONTAL;
	        gbc.anchor = GridBagConstraints.CENTER;
	        gbc.weightx = 1.0; gbc.weighty = 1.0;
	        gbc.ipadx = 2; gbc.ipady = 2;
	        gbc.insets = new Insets(4, 4, 4, 4);
	        profilePhonePanel.add(phoneNumberPanel, gbc);
		}else{
			for(int i = 0; i < arrayLabel.length; i++){
				arrayLabel[i] = new JLabel(columns[i]);
				currentPanel.add(arrayLabel[i], gbc);
				gbc.gridx++;
				menuArrayTextField[j] = new JTextField();
				menuArrayTextField[j].setPreferredSize(new Dimension(150,20));
				currentPanel.add(menuArrayTextField[j], gbc);
				gbc.gridy++;
				gbc.gridx--;
			}
		}
		JPanel localBottomPanel = new JPanel();
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.fill = GridBagConstraints.VERTICAL;
		String[] buttonName = {"Add employee", "Modify employee", "Add item", "Modify item"};
		JButton bottonButton = new JButton(buttonName[viewType.ordinal()]);
		gbc.gridx++;
		localBottomPanel.add(bottonButton, gbc);
		localFrame.getContentPane().add(localBottomPanel, BorderLayout.SOUTH);
		localFrame.getContentPane().add(localScollPane, BorderLayout.CENTER);
	}
	
	/**
	 * loadProfileView: reload employee profile UI
	 */
 	private void loadProfileView(){
		if(createOrderPanel != null){
			frame.getContentPane().remove(createOrderPanel);
		}
		if(ordersPanel != null){
			frame.getContentPane().remove(ordersPanel);
		}
		if(mainPanel != null){
			frame.getContentPane().remove(mainPanel);
		}
		if(logsPanel != null){
			frame.getContentPane().remove(logsPanel);
		}
		if(statPanel != null){
			frame.getContentPane().remove(statPanel);
		}
		if(manageEmpPanel != null){
			frame.getContentPane().remove(manageEmpPanel);
		}
		if(manageMenuPanel != null){
			frame.getContentPane().remove(manageMenuPanel);
		}
		if(bottomPanel != null){
			frame.getContentPane().remove(bottomPanel);
		}
		initializeProfileView();
	}
	
 	/**
	 * loadCreateCustomerView: reload create customer UI
	 */
	private void loadCreateCustomerView(){
		if(profilePanel != null){
			frame.getContentPane().remove(profilePanel);
			frame.getContentPane().remove(profileScollPane);
		}
		if(ordersPanel != null){
			frame.getContentPane().remove(ordersPanel);
		}
		if(createOrderPanel != null){
			frame.getContentPane().remove(createOrderPanel);
		}
		if(logsPanel != null){
			frame.getContentPane().remove(logsPanel);
		}
		if(statPanel != null){
			frame.getContentPane().remove(statPanel);
		}
		if(bottomPanel != null){
			frame.getContentPane().remove(bottomPanel);
		}
		if(manageMenuPanel != null){
			frame.getContentPane().remove(manageMenuPanel);
		}
		if(manageEmpPanel != null){
			frame.getContentPane().remove(manageEmpPanel);
		}
		initializeCreateCustomerView();
	}
	
	/**
	 * loadCreateOrderView: reload the create order UI
	 */
	private void loadCreateOrderView(){
		if(profilePanel != null){
			frame.getContentPane().remove(profilePanel);
			frame.getContentPane().remove(profileScollPane);
		}
		if(ordersPanel != null){
			frame.getContentPane().remove(ordersPanel);
		}
		if(mainPanel != null){
			frame.getContentPane().remove(mainPanel);
		}
		if(logsPanel != null){
			frame.getContentPane().remove(logsPanel);
		}
		if(statPanel != null){
			frame.getContentPane().remove(statPanel);
		}
		if(manageEmpPanel != null){
			frame.getContentPane().remove(manageEmpPanel);
		}
		if(manageMenuPanel != null){
			frame.getContentPane().remove(manageMenuPanel);
		}
		if(bottomPanel != null){
			frame.getContentPane().remove(bottomPanel);
		}
		initializeCreateOrderView();
	}
	
	/**
	 * loadOrdersView: reload the orders UI for employee
	 */
	private void loadOrdersView(){
		if(profilePanel != null){
			frame.getContentPane().remove(profilePanel);
			frame.getContentPane().remove(profileScollPane);
		}
		if(createOrderPanel != null){
			frame.getContentPane().remove(createOrderPanel);
		}
		if(mainPanel != null){
			frame.getContentPane().remove(mainPanel);
		}
		if(logsPanel != null){
			frame.getContentPane().remove(logsPanel);
		}
		if(statPanel != null){
			frame.getContentPane().remove(statPanel);
		}
		if(manageEmpPanel != null){
			frame.getContentPane().remove(manageEmpPanel);
		}
		if(manageMenuPanel != null){
			frame.getContentPane().remove(manageMenuPanel);
		}
		if(bottomPanel != null){
			frame.getContentPane().remove(bottomPanel);
		}
		initializeOrdersView();
	}

	/**
	 * loadLogsView: reload the logs UI.
	 */
	private void loadLogsView(){
		if(profilePanel != null){
			frame.getContentPane().remove(profilePanel);
			frame.getContentPane().remove(profileScollPane);
		}
		if(createOrderPanel != null){
			frame.getContentPane().remove(createOrderPanel);
		}
		if(mainPanel != null){
			frame.getContentPane().remove(mainPanel);
		}
		if(ordersPanel != null){
			frame.getContentPane().remove(ordersPanel);
		}
		if(statPanel != null){
			frame.getContentPane().remove(statPanel);
		}
		if(manageEmpPanel != null){
			frame.getContentPane().remove(manageEmpPanel);
		}
		if(manageMenuPanel != null){
			frame.getContentPane().remove(manageMenuPanel);
		}
		if(bottomPanel != null){
			frame.getContentPane().remove(bottomPanel);
		}
		initializeLogsView();
	}
	
	/**
	 * loadStatView: reload the statistics UI.
	 */
	private void loadStatView(){
		if(profilePanel != null){
			frame.getContentPane().remove(profilePanel);
			frame.getContentPane().remove(profileScollPane);
		}
		if(createOrderPanel != null){
			frame.getContentPane().remove(createOrderPanel);
		}
		if(mainPanel != null){
			frame.getContentPane().remove(mainPanel);
		}
		if(ordersPanel != null){
			frame.getContentPane().remove(ordersPanel);
		}
		if(logsPanel != null){
			frame.getContentPane().remove(logsPanel);
		}
		if(statPanel != null){
			frame.getContentPane().remove(statPanel);
		}
		if(manageEmpPanel != null){
			frame.getContentPane().remove(manageEmpPanel);
		}
		if(manageMenuPanel != null){
			frame.getContentPane().remove(manageMenuPanel);
		}
		if(bottomPanel != null){
			frame.getContentPane().remove(bottomPanel);
		}
		initializeStatView();
	}
	
	/**
	 * loadManageEmpView: reload the managing employee UI.
	 */
	private void loadManageEmpView(){
		if(profilePanel != null){
			frame.getContentPane().remove(profilePanel);
			frame.getContentPane().remove(profileScollPane);
		}
		if(createOrderPanel != null){
			frame.getContentPane().remove(createOrderPanel);
		}
		if(mainPanel != null){
			frame.getContentPane().remove(mainPanel);
		}
		if(ordersPanel != null){
			frame.getContentPane().remove(ordersPanel);
		}
		if(logsPanel != null){
			frame.getContentPane().remove(logsPanel);
		}
		if(manageMenuPanel != null){
			frame.getContentPane().remove(manageMenuPanel);
		}
		if(bottomPanel != null){
			frame.getContentPane().remove(bottomPanel);
		}
		initializeManageEmpView();
	}
	
	/**
	 * loadManageMenuView: reload the managing menu UI.
	 */
	private void loadManageMenuView(){
		if(profilePanel != null){
			frame.getContentPane().remove(profilePanel);
			frame.getContentPane().remove(profileScollPane);
		}
		if(createOrderPanel != null){
			frame.getContentPane().remove(createOrderPanel);
		}
		if(mainPanel != null){
			frame.getContentPane().remove(mainPanel);
		}
		if(ordersPanel != null){
			frame.getContentPane().remove(ordersPanel);
		}
		if(logsPanel != null){
			frame.getContentPane().remove(logsPanel);
		}
		if(manageEmpPanel != null){
			frame.getContentPane().remove(manageEmpPanel);
		}
		if(bottomPanel != null){
			frame.getContentPane().remove(bottomPanel);
		}
		initializeMenuView();
	}
	
	/**
	 * populateMenuTable: populate the initial menu items.
	 */
	private MyModel populateMenuTable(){
		String[] columns = { "Name", "Type", "Price", "Estimate Time"};
		Object[][] data = { {"Peperoni", "Pizza", new Integer(25), new Integer(30)},
							{"Cola", "Drinks", new Integer(4), new Integer(0)}
		};
		return new MyModel(data, columns);
	}
	
	/**
	 * populateTable: populate the initial ordered items.
	 */
	private JTable populateTable(String[] columns){
		Object[][] data = {};
		return new JTable(data, columns);
	}
	
	/**
	 * populateStatLogsTable: populate the info for statistics and logs table.
	 */
	private MyModel populateStatLogsTable(String[] columns){
		Object[][] data = {};
		return new MyModel(data, columns);
	}
	
	/**
	 * MyModel: Model for Tables so that the elements cannot be modified.
	 */
	public class MyModel extends DefaultTableModel{

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		MyModel(Object[][] data, Object[] columns){
			super(data,columns);
		}
		
		@Override
		public boolean isCellEditable(int row, int column){
			return false;
		}
	}
}
