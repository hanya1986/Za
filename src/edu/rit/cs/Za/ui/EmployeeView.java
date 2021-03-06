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
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.io.File;
import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.DefaultCellEditor;
import javax.swing.Icon;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpinnerDateModel;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import edu.rit.cs.Za.OrderType;
import edu.rit.cs.Za.ProfileManager;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Duration;

import edu.rit.cs.Za.ConnectionManager;
import edu.rit.cs.Za.ItemType;
import edu.rit.cs.Za.MenuManager;
import edu.rit.cs.Za.OrderManager;
import edu.rit.cs.Za.Month;
import edu.rit.cs.Za.Queries;
import edu.rit.cs.Za.TablePopulator;
import edu.rit.cs.Za.ZaDatabase;
import edu.rit.cs.Za.Queries.DelivererTime;
import edu.rit.cs.Za.ui.CustomerView.MyModel;

public class EmployeeView {
	
	private String[] profileFields = new String[]{
			"First Name",
			"Middle Name",
			"Last Name",
			"DOB",
			"Address",
			"City",
			"State",
			"Zip",
			"SSN",
			"Hourly Rate",
			"Hours/Week",
			"Date Hired",
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
			"estimate time(Min):",
			"Small price:",
			"Medium price:",
			"Large price:"
	};
	
	private String[] itemsAttr = new String[]{
			"type",
			"est_prep_time",
			"small_price",
			"medium_price",
			"large_price",
			"available",
		};
	private String[] empAttr = new String[]{
			"first_name",
			"middle_name",
			"last_name",
			"date_of_birth",
			"street",
			"city",
			"state",
			"zip",
			"ssn",
			"hourly_rate",
			"hours_per_week",
			"date_hired",
			"date_terminated",
			"job_title",
		};
	
	private enum typeFrame{
		addEmployee,
		modifyEmployee,
		addItem,
		modifyitem
	}
	
	private JButton signUpButton;
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
	private JPanel statPanel;
	private JSpinner DOBSpinner;
	private JSpinner HireDateSpinner;
	private JScrollPane profileScollPane;
	private JSpinner fromSpinner;
	private JSpinner toSpinner;
	private JPanel manageEmpPanel;
	private JTable manageEmpTable;
	private JPanel manageMenuPanel;
	private JTable manageMenuTable;
	private JPanel addEmpPanel;
	private JPanel modifyEmpPanel;
	private JPanel addItemPanel;
	private JPanel modifyItemPanel;
	private JTextField[] arrayTextField;
	private JTextField[] custArrayTextField;
	private JRadioButton deliverRB;
	private JRadioButton pickupRB;
	private JComboBox<String> itemSizeComboBox;
	
	private long userID;
	private boolean isManager;
	
	private JTextField qsItemNameTextField;
	private JTextField qsQuantityTextField;
	private JButton qsRefreshButton;
	
	private Map<String,Object> empProfile;
	private Map<String,Object> menuItem;
	private List<String> phoneNumbers;
	private List<String> newPhones;
	private List<String> removePhones;
	private List<String> emailAddress;
	private List<String> newEmails;
	private List<String> removeEmails;
	private Map<String,Map<String,Object>> menu = new HashMap<String,Map<String,Object>>();
	private List<String> empAttrList = new ArrayList<String>(Arrays.asList(empAttr));
	private List<String> itemAttrList = new ArrayList<String>(Arrays.asList(itemsAttr));
	
	/**
	 * run: Show the frame
	 */
	public void run(){
		this.frame.setVisible(true);
	}
	
	/**
	 * EmployeeView: Constructor.
	 */
	public EmployeeView(long userID, boolean isManager){
		this.userID = userID;
		this.isManager = isManager;
		try {
			List<String> items = MenuManager.getAvailableItems();
			Iterator<String> itemIt = items.iterator();
			while(itemIt.hasNext()){
				String iName = itemIt.next();
				Map<String,Object> itemDetail = MenuManager.getItemInfo(iName, itemAttrList);
				menu.put(iName, itemDetail);
			}
			empProfile = ProfileManager.getEmployeeInfo(this.userID, empAttrList);
			phoneNumbers = ProfileManager.getPhoneNumbers(this.userID);
			emailAddress = ProfileManager.getEmailAddresses(this.userID);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		initialize();
		initializeOrdersView();
	}
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
	    try {
            String db_location = "./ZADB/za";
            String username = "username";
            String password = "password";
            ConnectionManager.initConnection(db_location, username, password);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    EmployeeView window = new EmployeeView(91, true);
                    window.frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
	}
	
	/**
	 * populateProfileData: populating the customer data from database
	 */
	private void populateProfileData(){
		for(String attr : empAttrList){
			switch (attr)
            {
            case "first_name":
            	arrayTextField[0].setText(empProfile.get(attr).toString());
                break;
            case "middle_name":
            	arrayTextField[1].setText(empProfile.get(attr).toString());
                break;
            case "last_name":
            	arrayTextField[2].setText(empProfile.get(attr).toString());
                break;
            case "date_of_birth":
            	DOBSpinner.setValue(empProfile.get(attr));
                break;
            case "street":
            	arrayTextField[3].setText(empProfile.get(attr).toString());
                break;
            case "city":
            	arrayTextField[4].setText(empProfile.get(attr).toString());
                break;
            case "state":
            	arrayTextField[5].setText(empProfile.get(attr).toString());
                break;
            case "zip":
            	arrayTextField[6].setText(empProfile.get(attr).toString());
                break;
            case "ssn":
            	arrayTextField[7].setText(empProfile.get(attr).toString());
                break;
            case "hourly_rate":
            	arrayTextField[8].setText(empProfile.get(attr).toString());
                break;
            case "hours_per_week":
            	arrayTextField[9].setText(empProfile.get(attr).toString());
                break;
            case "date_hired":
            	HireDateSpinner.setValue(empProfile.get(attr));
                break;
            case "job_title":
            	arrayTextField[10].setText(empProfile.get(attr).toString());
                break;
            }
		}
		for(String phoneNum : phoneNumbers){
			phoneNumberComboBox.addItem(phoneNum);
		}
		if(phoneNumbers.size() != 0){
			removePhoneNumberButton.setEnabled(true);
			phoneNumberComboBox.setEnabled(true);
			
		}
		for(String emailAddr : emailAddress){
			emailComboBox.addItem(emailAddr);
		}
		if(emailAddress.size() != 0){
			removeEmailButton.setEnabled(true);
			emailComboBox.setEnabled(true);
		}
	}
	
	/**
	 * updateProfile: function that updates the profile for the customer
	 */
	private void updateProfile(){
		for(String attr : empAttrList){
			switch (attr)
            {
            case "first_name":
            	empProfile.put(attr, arrayTextField[0].getText());
                break;
            case "middle_name":
            	empProfile.put(attr, arrayTextField[1].getText());
                break;
            case "last_name":
            	empProfile.put(attr, arrayTextField[2].getText());
                break;
            case "date_of_birth":
            	empProfile.put(attr, DOBSpinner.getValue());
                break;
            case "street":
            	empProfile.put(attr, arrayTextField[3].getText());
                break;
            case "city":
            	empProfile.put(attr, arrayTextField[4].getText());
                break;
            case "state":
            	empProfile.put(attr, arrayTextField[5].getText());
                break;
            case "zip":
            	empProfile.put(attr, arrayTextField[6].getText());
                break;
            case "ssn":
            	empProfile.put(attr, arrayTextField[7].getText());
                break;
            case "hourly_rate":
            	empProfile.put(attr, new BigDecimal(arrayTextField[8].getText()));
                break;
            case "hours_per_week":
            	empProfile.put(attr, Float.parseFloat(arrayTextField[9].getText()));
                break;
            case "date_hired":
            	empProfile.put(attr,  (Date)HireDateSpinner.getValue());
                break;
            case "job_title":
            	empProfile.put(attr, arrayTextField[10].getText());
                break;
            }
		}
		try {
			ProfileManager.modifyCustomer(userID, empProfile);
			for(int i = 0; i < removePhones.size(); i++){
				phoneNumbers.remove(removePhones.get(i));
				ProfileManager.removePhoneNumber(userID, removePhones.get(i));
			}
			for(int i = 0; i < newPhones.size(); i++){
				phoneNumbers.add(newPhones.get(i));
				ProfileManager.addPhoneNumber(userID, newPhones.get(i));
			}
			for(int i = 0; i < removeEmails.size(); i++){
				emailAddress.remove(removeEmails.get(i));
				ProfileManager.removeEmailAddress(userID, removeEmails.get(i));
			}
			for(int i = 0; i < newEmails.size(); i++){
				emailAddress.add(newEmails.get(i));
				ProfileManager.addEmailAddress(userID, newEmails.get(i));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private boolean isInTable(DefaultTableModel model ,Object[] item){
		for(int i = 0; i < model.getRowCount(); i++){
			String name = model.getValueAt(i, 0).toString();
			String size = model.getValueAt(i, 2).toString();
			if(item[0].toString().equals(name) && item[2].toString().equals(size)){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * initialize: initializing the frame and the tool bar.
	 */
	private void initialize(){
		frame = new JFrame();
		frame.setSize(new Dimension(1400, 700));
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
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
		
		if(isManager){
			initializeManagerView();
		}
		
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
	    newPhones = new LinkedList<String>();
	    
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
                newPhones.add(phoneNumber);
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
                
                if(!newPhones.contains(phoneNumberComboBox.getSelectedItem())){
                	removePhones.add(phoneNumberComboBox.getSelectedItem().toString());
                }else{
                	newPhones.remove(phoneNumberComboBox.getSelectedItem());
                }
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
	    newEmails = new LinkedList<String>();
	    
		emailPanel = new JPanel(new GridBagLayout());
	    
		emailComboBox = new JComboBox<String>();
		emailComboBox.setEnabled(false);
        
		emailTextField = new JTextField();
        
		addEmailButton = new JButton("Add");
		addEmailButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent evt)
            {
                String email = emailTextField.getText();
                emailTextField.setText("");
                
                for (int i = 0; i < emailComboBox.getItemCount(); ++i)
                {
                    String s = emailComboBox.getItemAt(i);
                    if (s.equals(email)) return;
                }
                
                emailComboBox.addItem(email);
                newEmails.add(email);
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
                if(!newEmails.contains(emailComboBox.getSelectedItem())){
                	removeEmails.add(emailComboBox.getSelectedItem().toString());
                }else{
                	newEmails.remove(emailComboBox.getSelectedItem());
                }
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
		cutomerProfilePanel = new JPanel(new GridBagLayout());
		custArrayTextField = new JTextField[CustomerProfileFields.length];
		SpinnerDateModel model;
		for(int i = 0; i < CustomerProfileFields.length; i++){
		    
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
				custArrayTextField[i] = new JTextField();
				cutomerProfilePanel.add(custArrayTextField[i],gbc);
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
		signUpButton.addActionListener(new ActionListener(){
			
			@Override
			public void actionPerformed(ActionEvent e) {
				Map<String,Object> values = new HashMap<String,Object>();
				values.put("first_name", custArrayTextField[0].getText());
				values.put("middle_name", custArrayTextField[1].getText());
				values.put("last_name", custArrayTextField[2].getText());
				values.put("date_of_birth", (Date)customerDOBSpinner.getValue());
				values.put("username", custArrayTextField[4].getText());
				values.put("street", custArrayTextField[6].getText());
				values.put("city", custArrayTextField[7].getText());
				values.put("state", custArrayTextField[8].getText());
				values.put("zip", custArrayTextField[9].getText());
				try {
					if(custArrayTextField[8].getText().length() != 2){
						JOptionPane.showMessageDialog(frame, "State must be 2 characters!", "Notice", JOptionPane.ERROR_MESSAGE);
						return;
					}
					if(custArrayTextField[4].getText().isEmpty()){
						JOptionPane.showMessageDialog(frame, "Please enter a username.", "Notice", JOptionPane.ERROR_MESSAGE);
						return;
					}
					if(pwField.getPassword().length == 0){
						JOptionPane.showMessageDialog(frame, "Please enter a password", "Notice", JOptionPane.ERROR_MESSAGE);
						return;
					}
					if(ProfileManager.getPersonID(custArrayTextField[4].getText()) != ProfileManager.USERNAME_NOT_IN_TABLE){
						JOptionPane.showMessageDialog(frame, "Please choose a different username.", "Notice", JOptionPane.ERROR_MESSAGE);
						return;
					}
					ProfileManager.createCustomer(values, String.valueOf(pwField.getPassword()));
					long personId = ProfileManager.getPersonID(custArrayTextField[4].getText());
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
					JOptionPane.showMessageDialog(frame, "Customer account created!", "Notice", JOptionPane.INFORMATION_MESSAGE);
				} catch (NoSuchAlgorithmException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});

		JPanel buttonPanel = new JPanel();
		buttonPanel.add(signUpButton);
		
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
		newPhones = new ArrayList<String>();
		removePhones = new ArrayList<String>();
		newEmails = new ArrayList<String>();
		removeEmails = new ArrayList<String>();
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
		arrayTextField = new JTextField[profileFields.length - 2];
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
			}else if( i == 11){
				Calendar calendar = Calendar.getInstance();
				Date currentDate = calendar.getTime();
				calendar.add(Calendar.YEAR, -100);
				Date firstDate = calendar.getTime();
				calendar.add(Calendar.YEAR, 200);
				Date lastDate = calendar.getTime();
				model = new SpinnerDateModel(currentDate, firstDate, lastDate, Calendar.YEAR);
				HireDateSpinner = new JSpinner(model);
				HireDateSpinner.setEditor(new JSpinner.DateEditor(HireDateSpinner, "MM/dd/yyyy"));
				profilePanel.add(HireDateSpinner,gbc);
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
		updateButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				updateProfile();				
			}
			
		});
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
		add.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				int selectedRow = menuTable.getSelectedRow();
				if(selectedRow != -1){
					TableModel model = menuTable.getModel();
					Object[] data = new Object[6];
					for(int i = 0; i < data.length - 1; i++){
						if(i == 3){
							if(data[i - 1] == null){
								data[i - 1] = "SMALL";
							}
							switch(data[i - 1].toString()){
							case "SMALL":
								data[i] = model.getValueAt(selectedRow, 3);
								break;
							case "MEDIUM":
								data[i] = model.getValueAt(selectedRow, 4);
								break;
							case "LARGE":
								data[i] = model.getValueAt(selectedRow, 5);
								break;
							}
						}else{
							if(i == data.length - 2){
								data[i] = model.getValueAt(selectedRow, 6);
							}else{
								data[i] = model.getValueAt(selectedRow, i);
							}
						}
					}
					data[data.length - 1] = 1;
					DefaultTableModel carModel = (DefaultTableModel) carTable.getModel();
					if(!isInTable(carModel, data)){
						carModel.addRow(data);
					}
				}
			}
			
		});
		JButton remove = new JButton("Remove");
		remove.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				int selectedRow = carTable.getSelectedRow();
				if(selectedRow != -1){
					DefaultTableModel carModel = (DefaultTableModel) carTable.getModel();
					carModel.removeRow(selectedRow);
				}
			}
			
		});
		createOrderPanel.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		JPanel AddRemovePanel = new JPanel(new GridBagLayout());
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		AddRemovePanel.add(add,gbc);
		gbc.gridy = 1;
		AddRemovePanel.add(remove, gbc);
		deliverRB = new JRadioButton("Delivery");
		deliverRB.setSelected(true);
		gbc.gridy++;
		AddRemovePanel.add(deliverRB, gbc);
		pickupRB = new JRadioButton("Pick up");
		gbc.gridy++;
		AddRemovePanel.add(pickupRB, gbc);
		
		ButtonGroup RBGroup = new ButtonGroup();
		RBGroup.add(deliverRB);
		RBGroup.add(pickupRB);
		
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
		TableColumn itemColumn = menuTable.getColumnModel().getColumn(2);
		itemSizeComboBox = new JComboBox<String>();
		itemSizeComboBox.addItem("SMALL");
		itemSizeComboBox.addItem("MEDIUM");
		itemSizeComboBox.addItem("LARGE");
		itemSizeComboBox.setSelectedItem(0);
		itemSizeComboBox.setEditable(false);
		itemColumn.setCellEditor(new DefaultCellEditor(itemSizeComboBox));
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
		carTable = new JTable(createCarTable());
		sp = new JScrollPane(carTable);
		sp.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
		createOrderPanel.add(sp, gbc);
		
		bottomPanel = new JPanel(new BorderLayout());
		JButton resetButton = new JButton("Reset");
		resetButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				DefaultTableModel carModel = (DefaultTableModel) carTable.getModel();
				carModel.setRowCount(0);
			}
			
		});
		JPanel centerBottomPanel = new JPanel();
		centerBottomPanel.add(resetButton);
		bottomPanel.add(centerBottomPanel, BorderLayout.CENTER);
		JButton pastButton = new JButton("Past Orders");
		pastButton.setEnabled(false);
		pastButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				PastOrderPanel pastWindow = new PastOrderPanel(userID);
				int result = JOptionPane.showConfirmDialog(null, pastWindow, "Past Orders",
						JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
				if(result == JOptionPane.OK_OPTION){
					ArrayList<Object[]> items = pastWindow.getSelectedOrder();
					DefaultTableModel carModel = (DefaultTableModel) carTable.getModel();
					for(Object[] item : items){
						if(!isInTable(carModel,item)){
							carModel.addRow(item);
						}
					}
				}
			}
			
		});
		bottomPanel.add(pastButton, BorderLayout.LINE_END);
		JButton placeButton = new JButton("Place Order");
		placeButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				DefaultTableModel model = (DefaultTableModel) carTable.getModel();
				Map<String,Integer> orderItems = new HashMap<String, Integer>();
				for(int i = 0; i < model.getRowCount(); i++){
					String item = model.getValueAt(i, 2).toString().concat(" " + model.getValueAt(i, 0).toString());
					int	quantity = Integer.parseInt(model.getValueAt(i, 5).toString());
					orderItems.put(item, quantity);
				}
				OrderType ordertype = OrderType.parseOrderType("CARRY-OUT");
				if(deliverRB.isSelected()){
					ordertype = OrderType.parseOrderType("DELIVERY");
				}
				PaymentView pv = new PaymentView(userID, true, orderItems, ordertype);
				pv.runGUI();
			}
			
		});
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
		String[] columns = { "Order ID", "Customer ID", "Order Type", "Order Placed", "Total", "Payment Method" };
		MyModel model = populateTable(columns);
		carTable = new JTable(model);
		JScrollPane sp = new JScrollPane(carTable);
		sp.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
		ordersPanel.add(sp, gbc);
		
		bottomPanel = new JPanel(new GridBagLayout());
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 1;
		gbc.weighty = 1;
		gbc.fill = GridBagConstraints.VERTICAL;
		JButton pastButton = new JButton("Past Orders");
		pastButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				PastOrderPanel pastWindow = new PastOrderPanel();
				int result = JOptionPane.showConfirmDialog(null, pastWindow, "Past Orders",
						JOptionPane.CLOSED_OPTION, JOptionPane.PLAIN_MESSAGE);
			}
			
		});
		JButton cancelButton = new JButton("Cancel Order");
		cancelButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				int row = carTable.getSelectedRow();
				if(row != -1){
					int result = JOptionPane.showConfirmDialog(frame, "Are you sure to cancel the order ?" + row,
							"Confirm",JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
					if(result == JOptionPane.YES_OPTION){
						try {
					        Map<String,Object> value = new HashMap<String,Object>();
					        value.put("active", false);
					        OrderManager.modifyOrder(Long.parseLong(carTable.getValueAt(row, 0).toString()), value);
							//OrderManager.removeOrder(Long.parseLong(carTable.getValueAt(row, 0).toString()));
							carTable.remove(row);
						} catch (NumberFormatException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (SQLException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
				}
			}
			
		});
		JButton deliveredButton = new JButton("Delivered");
		deliveredButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				int row = carTable.getSelectedRow();
				if(row != -1){
					Map<String,Object> values = new HashMap<String,Object>();
					values.put("active", false);
					values.put("empid_took_order", userID);
					values.put("empid_prepared_order", userID);
					values.put("empid_delivered_order", userID);
					values.put("time_order_out", Calendar.getInstance().getTime());
					values.put("time_order_delivered", Calendar.getInstance().getTime());
					try {
						OrderManager.modifyOrder(Long.parseLong(carTable.getValueAt(row, 0).toString()), values);
						JOptionPane.showMessageDialog(frame, "Order delivered!");
						DefaultTableModel model = (DefaultTableModel) carTable.getModel();
						model.removeRow(row);
					} catch (NumberFormatException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (SQLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
			
		});
		JButton modifyButton = new JButton("Modify Order");
		modifyButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				int row = carTable.getSelectedRow();
				if(row != -1){
					long orderID = Long.parseLong(carTable.getValueAt(row, 0).toString());
					OrderType type = OrderType.parseOrderType(carTable.getValueAt(row, 2).toString());
					ModifyOrderPanel modify = new ModifyOrderPanel(orderID);
					int result = JOptionPane.showConfirmDialog(null, modify, "Modify Order",
							JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
					if(result == JOptionPane.OK_OPTION){
						try {
							List<String> removeItems = modify.itemsRemoved();
							if(!removeItems.isEmpty()){
								OrderManager.removeItems(orderID, removeItems);
							}
							if(!modify.itemsModified().isEmpty()){
								OrderManager.changeQuantities(orderID, modify.itemsModified());
							}
							if(!modify.itemsAdded().isEmpty()){
								OrderManager.addItems(orderID, modify.itemsAdded());
							}
						} catch (SQLException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
				}	
			}
			
		});
		gbc.anchor = GridBagConstraints.LINE_START;
		bottomPanel.add(cancelButton, gbc);
		gbc.gridx++;
		gbc.anchor = GridBagConstraints.CENTER;
		bottomPanel.add(modifyButton, gbc);
		gbc.gridx++;
		bottomPanel.add(deliveredButton, gbc);
		gbc.gridx++;
		gbc.anchor = GridBagConstraints.LINE_END;
		bottomPanel.add(pastButton, gbc);
		frame.getContentPane().add(bottomPanel, BorderLayout.SOUTH);
	}
	
	/**
	 * initializeManagerView: initialize the manager UI
	 */
	public void initializeManagerView(){
		
		JButton statButton = new JButton("Statistics");
		statButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				loadStatView();
				frame.revalidate();
			}
			
		});
		menuBar.add(statButton);
		
		JButton manageEmpButton = new JButton("Manage Employees");
		manageEmpButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				loadManageEmpView();
				frame.revalidate();
			}
			
		});
		menuBar.add(manageEmpButton);
		
		JButton manageMenuButton = new JButton("Manage Menu");
		manageMenuButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				loadManageMenuView();
				frame.revalidate();
			}
			
		});
		menuBar.add(manageMenuButton);
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
                updateQuantitySold();
            }
        });
        
        gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.gridwidth = 1; gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.ipadx = 4; gbc.ipady = 4;
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.LINE_START;
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
        gbc.anchor = GridBagConstraints.LINE_START;
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
        gbc.anchor = GridBagConstraints.PAGE_END;
        gbc.weightx = 1.0; gbc.weighty = 1.0;
        quantitySoldPanel.add(qsRefreshButton, gbc);
        
        quantitySoldPanel.setBorder(
                BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), 
                        "Quantity Sold"));
        
        gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.gridwidth = 1; gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.BOTH;
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
                updateAverageDeliveryTime();
            }
        });
        
        gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.gridwidth = 1; gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.ipadx = 4; gbc.ipady = 4;
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.LINE_START;
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
        gbc.anchor = GridBagConstraints.LINE_START;
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
        gbc.fill = GridBagConstraints.BOTH;
        gbc.ipadx = 4; gbc.ipady = 4;
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.PAGE_START;
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
                updateTopItems();
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
        gbc.weightx = 0.75; gbc.weighty = 1.0;
        topItemsPanel.add(nItemsTextField, gbc);
        
        gbc = new GridBagConstraints();
        gbc.gridx = 2; gbc.gridy = 0;
        gbc.gridwidth = 1; gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipadx = 4; gbc.ipady = 4;
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 0.25; gbc.weighty = 1.0;
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
        gbc.gridwidth = 1; gbc.gridheight = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
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
                updateBestCustomers();
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
        gbc.weightx = 0.75; gbc.weighty = 1.0;
        bestCustomerPanel.add(nCustomersTextField, gbc);
        
        gbc = new GridBagConstraints();
        gbc.gridx = 2; gbc.gridy = 0;
        gbc.gridwidth = 1; gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipadx = 4; gbc.ipady = 4;
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 0.25; gbc.weighty = 1.0;
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
        gbc.gridwidth = 1; gbc.gridheight = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipadx = 4; gbc.ipady = 4;
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 1.0; gbc.weighty = 1.0;
        statPanel.add(bestCustomerPanel, gbc);
    }
    
    private JPanel recentCustomerPanel;
    private JTextField nRecentTextField;
    private JList<Long> nRecentList;
    private JButton nRecentRefreshButton;
    
    private void initRecentCustomersPanel()
    {
        GridBagConstraints gbc;
        
        recentCustomerPanel = new JPanel(new GridBagLayout());
        nRecentTextField = new JTextField();
        nRecentList = new JList<Long>();
        nRecentRefreshButton = new JButton("Refresh");
        nRecentRefreshButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e)
            {
                updateRecentCustomers();
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
        recentCustomerPanel.add(new JLabel("Number of Customers:"), gbc);
        
        gbc = new GridBagConstraints();
        gbc.gridx = 1; gbc.gridy = 0;
        gbc.gridwidth = 1; gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipadx = 4; gbc.ipady = 4;
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 0.75; gbc.weighty = 1.0;
        recentCustomerPanel.add(nRecentTextField, gbc);
        
        gbc = new GridBagConstraints();
        gbc.gridx = 2; gbc.gridy = 0;
        gbc.gridwidth = 1; gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipadx = 4; gbc.ipady = 4;
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 0.25; gbc.weighty = 1.0;
        recentCustomerPanel.add(nRecentRefreshButton, gbc);
        
        JScrollPane nRecentScrollPane = new JScrollPane(nRecentList,
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
        recentCustomerPanel.add(nRecentScrollPane, gbc);
        
        recentCustomerPanel.setBorder(
                BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), 
                        "Recent Customers"));
        
        gbc = new GridBagConstraints();
        gbc.gridx = 3; gbc.gridy = 1;
        gbc.gridwidth = 1; gbc.gridheight = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipadx = 4; gbc.ipady = 4;
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 1.0; gbc.weighty = 1.0;
        statPanel.add(recentCustomerPanel, gbc);
    }
    
    private JPanel fastestDriversPanel;
    private JList<Long> fastestDriversList;
    
    private void initFastestDriversPanel()
    {
        fastestDriversPanel = new JPanel(new BorderLayout());
        fastestDriversList = new JList<Long>();
        updateFastestDrivers();
        
        JScrollPane fastestDriversScrollPane = new JScrollPane(fastestDriversList,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        fastestDriversPanel.add(fastestDriversScrollPane, BorderLayout.CENTER);
        
        fastestDriversPanel.setBorder(
                BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), 
                        "Fastest Drivers"));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 4; gbc.gridy = 1;
        gbc.gridwidth = 1; gbc.gridheight = 2;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.ipadx = 4; gbc.ipady = 4;
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 1.0; gbc.weighty = 1.0;
        statPanel.add(fastestDriversPanel, gbc);
    }

    private JPanel orderCostPanel;
    private JTextField avgOrderCostTextField;
    private JTextField minOrderCostTextField;
    private JTextField maxOrderCostTextField;
    private JTextField medOrderCostTextField;
    private JTextField totalOrderCostTextField;
    
    private void initOrderCostPanel()
    {
        GridBagConstraints gbc;
        orderCostPanel = new JPanel(new GridBagLayout());
        
        avgOrderCostTextField = new JTextField();
        avgOrderCostTextField.setEditable(false);
        
        minOrderCostTextField = new JTextField();
        minOrderCostTextField.setEditable(false);
        
        maxOrderCostTextField = new JTextField();
        maxOrderCostTextField.setEditable(false);
        
        medOrderCostTextField = new JTextField();
        medOrderCostTextField.setEditable(false);
        
        totalOrderCostTextField = new JTextField();
        totalOrderCostTextField.setEditable(false);
        
        gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.gridwidth = 1; gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.ipadx = 4; gbc.ipady = 4;
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.LINE_START;
        gbc.weightx = 0.0; gbc.weighty = 1.0;
        orderCostPanel.add(new JLabel("Average:"), gbc);
        
        gbc = new GridBagConstraints();
        gbc.gridx = 1; gbc.gridy = 0;
        gbc.gridwidth = 1; gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipadx = 4; gbc.ipady = 4;
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 1.0; gbc.weighty = 1.0;
        orderCostPanel.add(avgOrderCostTextField, gbc);
        
        gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.gridwidth = 1; gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.ipadx = 4; gbc.ipady = 4;
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.LINE_START;
        gbc.weightx = 0.0; gbc.weighty = 1.0;
        orderCostPanel.add(new JLabel("Minimum:"), gbc);
        
        gbc = new GridBagConstraints();
        gbc.gridx = 1; gbc.gridy = 1;
        gbc.gridwidth = 1; gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipadx = 4; gbc.ipady = 4;
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 1.0; gbc.weighty = 1.0;
        orderCostPanel.add(minOrderCostTextField, gbc);
        
        gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 1; gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.ipadx = 4; gbc.ipady = 4;
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.LINE_START;
        gbc.weightx = 0.0; gbc.weighty = 1.0;
        orderCostPanel.add(new JLabel("Maximum:"), gbc);
        
        gbc = new GridBagConstraints();
        gbc.gridx = 1; gbc.gridy = 2;
        gbc.gridwidth = 1; gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipadx = 4; gbc.ipady = 4;
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 1.0; gbc.weighty = 1.0;
        orderCostPanel.add(maxOrderCostTextField, gbc);
        
        gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.gridwidth = 1; gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.ipadx = 4; gbc.ipady = 4;
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.LINE_START;
        gbc.weightx = 0.0; gbc.weighty = 1.0;
        orderCostPanel.add(new JLabel("Median:"), gbc);
        
        gbc = new GridBagConstraints();
        gbc.gridx = 1; gbc.gridy = 3;
        gbc.gridwidth = 1; gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipadx = 4; gbc.ipady = 4;
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 1.0; gbc.weighty = 1.0;
        orderCostPanel.add(medOrderCostTextField, gbc);
        
        gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 4;
        gbc.gridwidth = 1; gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.ipadx = 4; gbc.ipady = 4;
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.LINE_START;
        gbc.weightx = 0.0; gbc.weighty = 1.0;
        orderCostPanel.add(new JLabel("Total:"), gbc);
        
        gbc = new GridBagConstraints();
        gbc.gridx = 1; gbc.gridy = 4;
        gbc.gridwidth = 1; gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipadx = 4; gbc.ipady = 4;
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 1.0; gbc.weighty = 1.0;
        orderCostPanel.add(totalOrderCostTextField, gbc);
        
        orderCostPanel.setBorder(
                BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), 
                        "Order Cost"));
        
        gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.gridwidth = 1; gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipadx = 4; gbc.ipady = 4;
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.FIRST_LINE_START;
        gbc.weightx = 1.0; gbc.weighty = 1.0;
        statBottomPanel.add(orderCostPanel, gbc);
    }
    
    private JPanel dailyOrdersPanel;
    private JTextField avgDailyOrdersTextField;
    private JTextField minDailyOrdersTextField;
    private JTextField maxDailyOrdersTextField;
    private JTextField medDailyOrdersTextField;
    private JTextField totalDailyOrdersTextField;
    
    private void initDailyOrdersPanel()
    {
        GridBagConstraints gbc;
        dailyOrdersPanel = new JPanel(new GridBagLayout());
        
        avgDailyOrdersTextField = new JTextField();
        avgDailyOrdersTextField.setEditable(false);
        
        minDailyOrdersTextField = new JTextField();
        minDailyOrdersTextField.setEditable(false);
        
        maxDailyOrdersTextField = new JTextField();
        maxDailyOrdersTextField.setEditable(false);
        
        medDailyOrdersTextField = new JTextField();
        medDailyOrdersTextField.setEditable(false);
        
        totalDailyOrdersTextField = new JTextField();
        totalDailyOrdersTextField.setEditable(false);
        
        gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.gridwidth = 1; gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.ipadx = 4; gbc.ipady = 4;
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.LINE_START;
        gbc.weightx = 0.0; gbc.weighty = 1.0;
        dailyOrdersPanel.add(new JLabel("Average:"), gbc);
        
        gbc = new GridBagConstraints();
        gbc.gridx = 1; gbc.gridy = 0;
        gbc.gridwidth = 1; gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipadx = 4; gbc.ipady = 4;
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 1.0; gbc.weighty = 1.0;
        dailyOrdersPanel.add(avgDailyOrdersTextField, gbc);
        
        gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.gridwidth = 1; gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.ipadx = 4; gbc.ipady = 4;
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.LINE_START;
        gbc.weightx = 0.0; gbc.weighty = 1.0;
        dailyOrdersPanel.add(new JLabel("Minimum:"), gbc);
        
        gbc = new GridBagConstraints();
        gbc.gridx = 1; gbc.gridy = 1;
        gbc.gridwidth = 1; gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipadx = 4; gbc.ipady = 4;
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 1.0; gbc.weighty = 1.0;
        dailyOrdersPanel.add(minDailyOrdersTextField, gbc);
        
        gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 1; gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.ipadx = 4; gbc.ipady = 4;
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.LINE_START;
        gbc.weightx = 0.0; gbc.weighty = 1.0;
        dailyOrdersPanel.add(new JLabel("Maximum:"), gbc);
        
        gbc = new GridBagConstraints();
        gbc.gridx = 1; gbc.gridy = 2;
        gbc.gridwidth = 1; gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipadx = 4; gbc.ipady = 4;
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 1.0; gbc.weighty = 1.0;
        dailyOrdersPanel.add(maxDailyOrdersTextField, gbc);
        
        gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.gridwidth = 1; gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.ipadx = 4; gbc.ipady = 4;
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.LINE_START;
        gbc.weightx = 0.0; gbc.weighty = 1.0;
        dailyOrdersPanel.add(new JLabel("Median:"), gbc);
        
        gbc = new GridBagConstraints();
        gbc.gridx = 1; gbc.gridy = 3;
        gbc.gridwidth = 1; gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipadx = 4; gbc.ipady = 4;
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 1.0; gbc.weighty = 1.0;
        dailyOrdersPanel.add(medDailyOrdersTextField, gbc);
        
        gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 4;
        gbc.gridwidth = 1; gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.ipadx = 4; gbc.ipady = 4;
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.LINE_START;
        gbc.weightx = 0.0; gbc.weighty = 1.0;
        dailyOrdersPanel.add(new JLabel("Total:"), gbc);
        
        gbc = new GridBagConstraints();
        gbc.gridx = 1; gbc.gridy = 4;
        gbc.gridwidth = 1; gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipadx = 4; gbc.ipady = 4;
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 1.0; gbc.weighty = 1.0;
        dailyOrdersPanel.add(totalDailyOrdersTextField, gbc);
        
        dailyOrdersPanel.setBorder(
                BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), 
                        "Daily Orders"));
        
        gbc = new GridBagConstraints();
        gbc.gridx = 1; gbc.gridy = 0;
        gbc.gridwidth = 1; gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipadx = 4; gbc.ipady = 4;
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.FIRST_LINE_END;
        gbc.weightx = 1.0; gbc.weighty = 1.0;
        statBottomPanel.add(dailyOrdersPanel, gbc);
    }
    
    private JPanel monthlyOrdersPanel;
    private JTextField avgMonthlyOrdersTextField;
    private JTextField minMonthlyOrdersTextField;
    private JTextField maxMonthlyOrdersTextField;
    private JTextField medMonthlyOrdersTextField;
    private JTextField totalMonthlyOrdersTextField;
    
    private void initMonthlyOrdersPanel()
    {
        GridBagConstraints gbc;
        monthlyOrdersPanel = new JPanel(new GridBagLayout());
        
        avgMonthlyOrdersTextField = new JTextField();
        avgMonthlyOrdersTextField.setEditable(false);
        
        minMonthlyOrdersTextField = new JTextField();
        minMonthlyOrdersTextField.setEditable(false);
        
        maxMonthlyOrdersTextField = new JTextField();
        maxMonthlyOrdersTextField.setEditable(false);
        
        medMonthlyOrdersTextField = new JTextField();
        medMonthlyOrdersTextField.setEditable(false);
        
        totalMonthlyOrdersTextField = new JTextField();
        totalMonthlyOrdersTextField.setEditable(false);
        
        gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.gridwidth = 1; gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.ipadx = 4; gbc.ipady = 4;
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.LINE_START;
        gbc.weightx = 0.0; gbc.weighty = 1.0;
        monthlyOrdersPanel.add(new JLabel("Average:"), gbc);
        
        gbc = new GridBagConstraints();
        gbc.gridx = 1; gbc.gridy = 0;
        gbc.gridwidth = 1; gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipadx = 4; gbc.ipady = 4;
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 1.0; gbc.weighty = 1.0;
        monthlyOrdersPanel.add(avgMonthlyOrdersTextField, gbc);
        
        gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.gridwidth = 1; gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.ipadx = 4; gbc.ipady = 4;
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.LINE_START;
        gbc.weightx = 0.0; gbc.weighty = 1.0;
        monthlyOrdersPanel.add(new JLabel("Minimum:"), gbc);
        
        gbc = new GridBagConstraints();
        gbc.gridx = 1; gbc.gridy = 1;
        gbc.gridwidth = 1; gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipadx = 4; gbc.ipady = 4;
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 1.0; gbc.weighty = 1.0;
        monthlyOrdersPanel.add(minMonthlyOrdersTextField, gbc);
        
        gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 1; gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.ipadx = 4; gbc.ipady = 4;
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.LINE_START;
        gbc.weightx = 0.0; gbc.weighty = 1.0;
        monthlyOrdersPanel.add(new JLabel("Maximum:"), gbc);
        
        gbc = new GridBagConstraints();
        gbc.gridx = 1; gbc.gridy = 2;
        gbc.gridwidth = 1; gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipadx = 4; gbc.ipady = 4;
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 1.0; gbc.weighty = 1.0;
        monthlyOrdersPanel.add(maxMonthlyOrdersTextField, gbc);
        
        gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.gridwidth = 1; gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.ipadx = 4; gbc.ipady = 4;
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.LINE_START;
        gbc.weightx = 0.0; gbc.weighty = 1.0;
        monthlyOrdersPanel.add(new JLabel("Median:"), gbc);
        
        gbc = new GridBagConstraints();
        gbc.gridx = 1; gbc.gridy = 3;
        gbc.gridwidth = 1; gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipadx = 4; gbc.ipady = 4;
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 1.0; gbc.weighty = 1.0;
        monthlyOrdersPanel.add(medMonthlyOrdersTextField, gbc);
        
        gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 4;
        gbc.gridwidth = 1; gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.ipadx = 4; gbc.ipady = 4;
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.LINE_START;
        gbc.weightx = 0.0; gbc.weighty = 1.0;
        monthlyOrdersPanel.add(new JLabel("Total:"), gbc);
        
        gbc = new GridBagConstraints();
        gbc.gridx = 1; gbc.gridy = 4;
        gbc.gridwidth = 1; gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipadx = 4; gbc.ipady = 4;
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 1.0; gbc.weighty = 1.0;
        monthlyOrdersPanel.add(totalMonthlyOrdersTextField, gbc);
        
        monthlyOrdersPanel.setBorder(
                BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), 
                        "Monthly Orders"));
        
        gbc = new GridBagConstraints();
        gbc.gridx = 2; gbc.gridy = 0;
        gbc.gridwidth = 1; gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipadx = 4; gbc.ipady = 4;
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.PAGE_START;
        gbc.weightx = 1.0; gbc.weighty = 1.0;
        statBottomPanel.add(monthlyOrdersPanel, gbc);
    }
    
    private JPanel dailyRevenuePanel;
    private JTextField avgDailyRevenueTextField;
    private JTextField minDailyRevenueTextField;
    private JTextField maxDailyRevenueTextField;
    private JTextField medDailyRevenueTextField;
    private JTextField totalDailyRevenueTextField;
    
    private void initDailyRevenuePanel()
    {
        GridBagConstraints gbc;
        dailyRevenuePanel = new JPanel(new GridBagLayout());
        
        avgDailyRevenueTextField = new JTextField();
        avgDailyRevenueTextField.setEditable(false);
        
        minDailyRevenueTextField = new JTextField();
        minDailyRevenueTextField.setEditable(false);
        
        maxDailyRevenueTextField = new JTextField();
        maxDailyRevenueTextField.setEditable(false);
        
        medDailyRevenueTextField = new JTextField();
        medDailyRevenueTextField.setEditable(false);
        
        totalDailyRevenueTextField = new JTextField();
        totalDailyRevenueTextField.setEditable(false);
        
        gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.gridwidth = 1; gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.ipadx = 4; gbc.ipady = 4;
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.LINE_START;
        gbc.weightx = 0.0; gbc.weighty = 1.0;
        dailyRevenuePanel.add(new JLabel("Average:"), gbc);
        
        gbc = new GridBagConstraints();
        gbc.gridx = 1; gbc.gridy = 0;
        gbc.gridwidth = 1; gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipadx = 4; gbc.ipady = 4;
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 1.0; gbc.weighty = 1.0;
        dailyRevenuePanel.add(avgDailyRevenueTextField, gbc);
        
        gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.gridwidth = 1; gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.ipadx = 4; gbc.ipady = 4;
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.LINE_START;
        gbc.weightx = 0.0; gbc.weighty = 1.0;
        dailyRevenuePanel.add(new JLabel("Minimum:"), gbc);
        
        gbc = new GridBagConstraints();
        gbc.gridx = 1; gbc.gridy = 1;
        gbc.gridwidth = 1; gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipadx = 4; gbc.ipady = 4;
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 1.0; gbc.weighty = 1.0;
        dailyRevenuePanel.add(minDailyRevenueTextField, gbc);
        
        gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 1; gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.ipadx = 4; gbc.ipady = 4;
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.LINE_START;
        gbc.weightx = 0.0; gbc.weighty = 1.0;
        dailyRevenuePanel.add(new JLabel("Maximum:"), gbc);
        
        gbc = new GridBagConstraints();
        gbc.gridx = 1; gbc.gridy = 2;
        gbc.gridwidth = 1; gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipadx = 4; gbc.ipady = 4;
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 1.0; gbc.weighty = 1.0;
        dailyRevenuePanel.add(maxDailyRevenueTextField, gbc);
        
        gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.gridwidth = 1; gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.ipadx = 4; gbc.ipady = 4;
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.LINE_START;
        gbc.weightx = 0.0; gbc.weighty = 1.0;
        dailyRevenuePanel.add(new JLabel("Median:"), gbc);
        
        gbc = new GridBagConstraints();
        gbc.gridx = 1; gbc.gridy = 3;
        gbc.gridwidth = 1; gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipadx = 4; gbc.ipady = 4;
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 1.0; gbc.weighty = 1.0;
        dailyRevenuePanel.add(medDailyRevenueTextField, gbc);
        
        gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 4;
        gbc.gridwidth = 1; gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.ipadx = 4; gbc.ipady = 4;
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.LINE_START;
        gbc.weightx = 0.0; gbc.weighty = 1.0;
        dailyRevenuePanel.add(new JLabel("Total:"), gbc);
        
        gbc = new GridBagConstraints();
        gbc.gridx = 1; gbc.gridy = 4;
        gbc.gridwidth = 1; gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipadx = 4; gbc.ipady = 4;
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 1.0; gbc.weighty = 1.0;
        dailyRevenuePanel.add(totalDailyRevenueTextField, gbc);
        
        dailyRevenuePanel.setBorder(
                BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), 
                        "Daily Revenue"));
        
        gbc = new GridBagConstraints();
        gbc.gridx = 3; gbc.gridy = 0;
        gbc.gridwidth = 1; gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipadx = 4; gbc.ipady = 4;
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.FIRST_LINE_START;
        gbc.weightx = 1.0; gbc.weighty = 1.0;
        statBottomPanel.add(dailyRevenuePanel, gbc);
    }
    
    private JPanel monthlyRevenuePanel;
    private JTextField avgMonthlyRevenueTextField;
    private JTextField minMonthlyRevenueTextField;
    private JTextField maxMonthlyRevenueTextField;
    private JTextField medMonthlyRevenueTextField;
    private JTextField totalMonthlyRevenueTextField;
    
    private void initMonthlyRevenuePanel()
    {
        GridBagConstraints gbc;
        monthlyRevenuePanel = new JPanel(new GridBagLayout());
        
        avgMonthlyRevenueTextField = new JTextField();
        avgMonthlyRevenueTextField.setEditable(false);
        
        minMonthlyRevenueTextField = new JTextField();
        minMonthlyRevenueTextField.setEditable(false);
        
        maxMonthlyRevenueTextField = new JTextField();
        maxMonthlyRevenueTextField.setEditable(false);
        
        medMonthlyRevenueTextField = new JTextField();
        medMonthlyRevenueTextField.setEditable(false);
        
        totalMonthlyRevenueTextField = new JTextField();
        totalMonthlyRevenueTextField.setEditable(false);
        
        gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.gridwidth = 1; gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.ipadx = 4; gbc.ipady = 4;
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.LINE_START;
        gbc.weightx = 0.0; gbc.weighty = 1.0;
        monthlyRevenuePanel.add(new JLabel("Average:"), gbc);
        
        gbc = new GridBagConstraints();
        gbc.gridx = 1; gbc.gridy = 0;
        gbc.gridwidth = 1; gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipadx = 4; gbc.ipady = 4;
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 1.0; gbc.weighty = 1.0;
        monthlyRevenuePanel.add(avgMonthlyRevenueTextField, gbc);
        
        gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.gridwidth = 1; gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.ipadx = 4; gbc.ipady = 4;
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.LINE_START;
        gbc.weightx = 0.0; gbc.weighty = 1.0;
        monthlyRevenuePanel.add(new JLabel("Minimum:"), gbc);
        
        gbc = new GridBagConstraints();
        gbc.gridx = 1; gbc.gridy = 1;
        gbc.gridwidth = 1; gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipadx = 4; gbc.ipady = 4;
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 1.0; gbc.weighty = 1.0;
        monthlyRevenuePanel.add(minMonthlyRevenueTextField, gbc);
        
        gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 1; gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.ipadx = 4; gbc.ipady = 4;
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.LINE_START;
        gbc.weightx = 0.0; gbc.weighty = 1.0;
        monthlyRevenuePanel.add(new JLabel("Maximum:"), gbc);
        
        gbc = new GridBagConstraints();
        gbc.gridx = 1; gbc.gridy = 2;
        gbc.gridwidth = 1; gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipadx = 4; gbc.ipady = 4;
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 1.0; gbc.weighty = 1.0;
        monthlyRevenuePanel.add(maxMonthlyRevenueTextField, gbc);
        
        gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.gridwidth = 1; gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.ipadx = 4; gbc.ipady = 4;
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.LINE_START;
        gbc.weightx = 0.0; gbc.weighty = 1.0;
        monthlyRevenuePanel.add(new JLabel("Median:"), gbc);
        
        gbc = new GridBagConstraints();
        gbc.gridx = 1; gbc.gridy = 3;
        gbc.gridwidth = 1; gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipadx = 4; gbc.ipady = 4;
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 1.0; gbc.weighty = 1.0;
        monthlyRevenuePanel.add(medMonthlyRevenueTextField, gbc);
        
        gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 4;
        gbc.gridwidth = 1; gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.ipadx = 4; gbc.ipady = 4;
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.LINE_START;
        gbc.weightx = 0.0; gbc.weighty = 1.0;
        monthlyRevenuePanel.add(new JLabel("Total:"), gbc);
        
        gbc = new GridBagConstraints();
        gbc.gridx = 1; gbc.gridy = 4;
        gbc.gridwidth = 1; gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipadx = 4; gbc.ipady = 4;
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 1.0; gbc.weighty = 1.0;
        monthlyRevenuePanel.add(totalMonthlyRevenueTextField, gbc);
        
        monthlyRevenuePanel.setBorder(
                BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), 
                        "Monthly Revenue"));
        
        
        gbc = new GridBagConstraints();
        gbc.gridx = 4; gbc.gridy = 0;
        gbc.gridwidth = 1; gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipadx = 4; gbc.ipady = 4;
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.FIRST_LINE_START;
        gbc.weightx = 1.0; gbc.weighty = 1.0;
        statBottomPanel.add(monthlyRevenuePanel, gbc);
    }
	
    private void updateQuantitySold()
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
    
    private void updateAverageDeliveryTime()
    {
        if (dtEmpIdTextField.getText().equals(""))
            return;
        
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
        
        Duration duration = Duration.ofMillis(avgDeliveryTime);
        long hours = duration.toHours();
        duration = duration.minusHours(hours);
        long minutes = duration.toMinutes();
        dtAvgTimeTextField.setText(String.format("%02d:%02d", hours, minutes));
    }
    
    private void updateTopItems()
    {
        if (nItemsTextField.getText().equals(""))
            return;
        
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

    private void updateBestCustomers()
    {
        if (nCustomersTextField.getText().equals(""))
            return;
        
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
    
    private void updateRecentCustomers()
    {
        if (nRecentTextField.getText().equals(""))
            return;
        
        int n;
        try
        {
            n = Integer.parseInt(nRecentTextField.getText());
        }
        catch (NumberFormatException ex)
        {
            JOptionPane.showMessageDialog(null,
                    "Number of customers must be an integer greater than or equal to 1.",
                    "Recent Customers",
                    JOptionPane.ERROR_MESSAGE);
            nRecentTextField.setText("");
            nRecentList.setModel(new DefaultListModel<Long>());
            return;
        }
        
        if (n < 1)
        {
            JOptionPane.showMessageDialog(null,
                    "Number of customers must be an integer greater than or equal to 1.",
                    "Recent Customers",
                    JOptionPane.ERROR_MESSAGE);
            nRecentTextField.setText("");
            nRecentList.setModel(new DefaultListModel<Long>());
            return;
        }
        
        Map<Long,Timestamp> recentCustomers;
        try
        {
            recentCustomers = Queries.getLastNCust(n);
        }
        catch (SQLException ex)
        {
            JOptionPane.showMessageDialog(null,
                    "A database error occurred:\n" + ex.getMessage(),
                    "Recent Customers",
                    JOptionPane.ERROR_MESSAGE);
            nRecentTextField.setText("");
            nRecentList.setModel(new DefaultListModel<Long>());
            return;
        }
        
        DefaultListModel<Long> nRecentModel = new DefaultListModel<Long>();
        for (Long custID : recentCustomers.keySet())
            nRecentModel.addElement(custID);
        nRecentList.setModel(nRecentModel);
    }
    
    private void updateFastestDrivers()
    {
        List<DelivererTime> drivers;
        try
        {
            drivers = Queries.getFastestDeliverers();
        }
        catch (SQLException ex)
        {
            JOptionPane.showMessageDialog(null,
                    "A database error occurred:\n" + ex.getMessage(),
                    "Fastest Drivers",
                    JOptionPane.ERROR_MESSAGE);
            fastestDriversList.setModel(new DefaultListModel<Long>());
            return;
        }
        
        DefaultListModel<Long> fastestDriversModel = new DefaultListModel<Long>();
        for (DelivererTime driver : drivers)
            fastestDriversModel.addElement(driver.empid);
        fastestDriversList.setModel(fastestDriversModel);
    }
    
    private void updateStats(java.sql.Date start, java.sql.Date end)
    {
        if (end.compareTo(start) < 0)
        {
            java.sql.Date tmp = start;
            start = end;
            end = tmp;
        }
        
        try
        {
            Map<String,BigDecimal> costStats = Queries.getOrderCostStats(start, end);
            if (!costStats.isEmpty())
            {
                avgOrderCostTextField.setText(costStats.get("AVG_ORDER_COST").toString());
                minOrderCostTextField.setText(costStats.get("MIN_ORDER_COST").toString());
                maxOrderCostTextField.setText(costStats.get("MAX_ORDER_COST").toString());
                medOrderCostTextField.setText(costStats.get("MED_ORDER_COST").toString());
                totalOrderCostTextField.setText(costStats.get("TOTAL_ORDER_COST").toString());
            }
            else
            {
                avgOrderCostTextField.setText("");
                minOrderCostTextField.setText("");
                maxOrderCostTextField.setText("");
                medOrderCostTextField.setText("");
                totalOrderCostTextField.setText("");
            }
        }
        catch (SQLException ex)
        {
            JOptionPane.showMessageDialog(null, "A database error occurred:\n" + ex.getMessage(), "Order Cost", JOptionPane.ERROR_MESSAGE);
        }
        
        try
        {
            Map<String,Float> orderStats = Queries.getDailyOrderStats(start, end);
            if (!orderStats.isEmpty())
            {
                avgDailyOrdersTextField.setText(orderStats.get("AVG_DAILY_ORDERS").toString());
                minDailyOrdersTextField.setText(Integer.toString((int)orderStats.get("MIN_DAILY_ORDERS").floatValue()));
                maxDailyOrdersTextField.setText(Integer.toString((int)orderStats.get("MAX_DAILY_ORDERS").floatValue()));
                medDailyOrdersTextField.setText(orderStats.get("MED_DAILY_ORDERS").toString());
                totalDailyOrdersTextField.setText(Integer.toString((int)orderStats.get("TOTAL_DAILY_ORDERS").floatValue()));
            }
            else
            {
                avgDailyOrdersTextField.setText("");
                minDailyOrdersTextField.setText("");
                maxDailyOrdersTextField.setText("");
                medDailyOrdersTextField.setText("");
                totalDailyOrdersTextField.setText("");
            }
        }
        catch (SQLException ex)
        {
            JOptionPane.showMessageDialog(
                    null,
                    "A database error occurred:\n" + ex.getMessage(),
                    "Daily Orders",
                    JOptionPane.ERROR_MESSAGE);
        }
        
        try
        {
            Map<String,Float> orderStats = Queries.getMonthlyOrderStats(
                    Month.parseMonth(start.getMonth()),
                    start.getYear() + 1900,
                    Month.parseMonth(end.getMonth()),
                    end.getYear() + 1900);
            if (!orderStats.isEmpty())
            {
                avgMonthlyOrdersTextField.setText(orderStats.get("AVG_MONTHLY_ORDERS").toString());
                minMonthlyOrdersTextField.setText(Integer.toString((int)orderStats.get("MIN_MONTHLY_ORDERS").floatValue()));
                maxMonthlyOrdersTextField.setText(Integer.toString((int)orderStats.get("MAX_MONTHLY_ORDERS").floatValue()));
                medMonthlyOrdersTextField.setText(orderStats.get("MED_MONTHLY_ORDERS").toString());
                totalMonthlyOrdersTextField.setText(Integer.toString((int)orderStats.get("TOTAL_MONTHLY_ORDERS").floatValue()));
            }
            else
            {
                avgMonthlyOrdersTextField.setText("");
                minMonthlyOrdersTextField.setText("");
                maxMonthlyOrdersTextField.setText("");
                medMonthlyOrdersTextField.setText("");
                totalMonthlyOrdersTextField.setText("");
            }
        }
        catch (SQLException ex)
        {
            JOptionPane.showMessageDialog(null,
                    "A database error occurred:\n" + ex.getMessage(),
                    "Monthly Orders",
                    JOptionPane.ERROR_MESSAGE);
        }
        
        try
        {
            Map<String,BigDecimal> revenueStats = Queries.getDailyRevenueStats(start, end);
            if (!revenueStats.isEmpty())
            {
                avgDailyRevenueTextField.setText(revenueStats.get("AVG_DAILY_REV").toString());
                minDailyRevenueTextField.setText(revenueStats.get("MIN_DAILY_REV").toString());
                maxDailyRevenueTextField.setText(revenueStats.get("MAX_DAILY_REV").toString());
                medDailyRevenueTextField.setText(revenueStats.get("MED_DAILY_REV").toString());
                totalDailyRevenueTextField.setText(revenueStats.get("TOTAL_DAILY_REV").toString());
            }
            else
            {
                avgDailyRevenueTextField.setText("");
                minDailyRevenueTextField.setText("");
                maxDailyRevenueTextField.setText("");
                medDailyRevenueTextField.setText("");
                totalDailyRevenueTextField.setText("");
            }
        }
        catch (SQLException ex)
        {
            JOptionPane.showMessageDialog(
                    null,
                    "A database error occurred:\n" + ex.getMessage(),
                    "Daily Revenue",
                    JOptionPane.ERROR_MESSAGE);
        }
        
        try
        {
            Map<String,BigDecimal> revenueStats = Queries.getMonthlyRevenueStats(
                    Month.parseMonth(start.getMonth()),
                    start.getYear() + 1900,
                    Month.parseMonth(end.getMonth()),
                    end.getYear() + 1900);
            if (!revenueStats.isEmpty())
            {
                avgMonthlyRevenueTextField.setText(revenueStats.get("AVG_MONTHLY_REV").toString());
                minMonthlyRevenueTextField.setText(revenueStats.get("MIN_MONTHLY_REV").toString());
                maxMonthlyRevenueTextField.setText(revenueStats.get("MAX_MONTHLY_REV").toString());
                medMonthlyRevenueTextField.setText(revenueStats.get("MED_MONTHLY_REV").toString());
                totalMonthlyRevenueTextField.setText(revenueStats.get("TOTAL_MONTHLY_REV").toString());
            }
            else
            {
                avgMonthlyRevenueTextField.setText("");
                minMonthlyRevenueTextField.setText("");
                maxMonthlyRevenueTextField.setText("");
                medMonthlyRevenueTextField.setText("");
                totalMonthlyRevenueTextField.setText("");
            }
        }
        catch (SQLException ex)
        {
            JOptionPane.showMessageDialog(null,
                    "A database error occurred:\n" + ex.getMessage(),
                    "Monthly Revenue",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private Date fromDate;
    private Date toDate;
    
    private JPanel statBottomPanel;
    
	/**
	 * initializeStatView: initialize the statistics UI
	 */
	public void initializeStatView()
	{
        GridBagConstraints gbc;
        statPanel = new JPanel(new GridBagLayout());
        
        initQuantitySoldPanel();
        initDeliveryTimePanel();
        initTopItemsPanel();
        initBestCustomersPanel();
        initRecentCustomersPanel();
        initFastestDriversPanel();
        
        statBottomPanel = new JPanel(new GridBagLayout());
        
        initOrderCostPanel();
        initDailyOrdersPanel();
        initMonthlyOrdersPanel();
        initDailyRevenuePanel();
        initMonthlyRevenuePanel();

        JPanel topPanel = new JPanel();
        
        JLabel fromLabel = new JLabel("From:");
        topPanel.add(fromLabel);
        Calendar calendar = Calendar.getInstance();
        fromDate = calendar.getTime();
        calendar.add(Calendar.YEAR, -100);
        Date firstDate = calendar.getTime();
        calendar.add(Calendar.YEAR, 200);
        Date lastDate = calendar.getTime();
        SpinnerDateModel fromModel = new SpinnerDateModel(fromDate, firstDate, lastDate, Calendar.YEAR);
        fromSpinner = new JSpinner(fromModel);
        fromSpinner.setEditor(new JSpinner.DateEditor(fromSpinner, "MM/dd/yyyy"));
        fromSpinner.addChangeListener(new ChangeListener(){
            public void stateChanged(ChangeEvent e)
            {
                Date newFrom = ((SpinnerDateModel)fromSpinner.getModel()).getDate();
                if (newFrom.compareTo(toDate) > 0)
                    fromSpinner.setValue(fromDate);
                else
                {
                    fromDate = newFrom;
                    updateQuantitySold();
                    updateAverageDeliveryTime();
                    updateTopItems();
                    updateBestCustomers();
                    updateRecentCustomers();
                    updateFastestDrivers();
                    updateStats(new java.sql.Date(fromDate.getTime()), new java.sql.Date(toDate.getTime()));
                }
            }
        });
        topPanel.add(fromSpinner);
        
        JLabel toLabel = new JLabel("To:");
        topPanel.add(toLabel);
        calendar = Calendar.getInstance();
        toDate = calendar.getTime();
        calendar.add(Calendar.YEAR, -100);
        firstDate = calendar.getTime();
        calendar.add(Calendar.YEAR, 200);
        lastDate = calendar.getTime();
        SpinnerDateModel toModel = new SpinnerDateModel(toDate, firstDate, lastDate, Calendar.YEAR);
        toSpinner = new JSpinner(toModel);
        toSpinner.setEditor(new JSpinner.DateEditor(toSpinner, "MM/dd/yyyy"));
        toSpinner.addChangeListener(new ChangeListener(){
            public void stateChanged(ChangeEvent e)
            {
                Date newTo = ((SpinnerDateModel)toSpinner.getModel()).getDate();
                if (newTo.compareTo(fromDate) < 0)
                    toSpinner.setValue(toDate);
                else
                {
                    toDate = newTo;
                    updateQuantitySold();
                    updateAverageDeliveryTime();
                    updateTopItems();
                    updateBestCustomers();
                    updateRecentCustomers();
                    updateFastestDrivers();
                    updateStats(new java.sql.Date(fromDate.getTime()), new java.sql.Date(toDate.getTime()));
                }
            }
        });
        topPanel.add(toSpinner);
        
        gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.gridwidth = 5; gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.ipadx = 4; gbc.ipady = 4;
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 1.0; gbc.weighty = 1.0;
        statPanel.add(topPanel, gbc);
        
        gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.gridwidth = 5; gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipadx = 4; gbc.ipady = 4;
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 1.0; gbc.weighty = 1.0;
        statPanel.add(statBottomPanel, gbc);
        
        java.sql.Date start = new java.sql.Date(((Date)fromSpinner.getValue()).getTime());
        java.sql.Date end = new java.sql.Date(((Date)toSpinner.getValue()).getTime());
        
        updateStats(start, end);
        
        frame.getContentPane().add(statPanel, BorderLayout.CENTER);		
	}
	
	private String modifiedItemName;
	private long modifiedempID;
	
	/**
	 * initializeManageEmpView: initialize the managing employee UI
	 */
	public void initializeManageEmpView(){
		manageEmpPanel = new JPanel(new BorderLayout());
		String[] columns = { "Employee ID", "First Name", "Middle Name","Last Name", "Job Title", "Hourly Rate", "Date Hired","Terminated"};
		manageEmpTable = new JTable();
		manageEmpTable.setModel(populateEmpTable(columns));
		JScrollPane sp = new JScrollPane(manageEmpTable);
		manageEmpPanel.add(sp, BorderLayout.CENTER);
		JPanel buttonPanel = new JPanel();
		JButton addEmpButton = new JButton("Add Employee");
		addEmpButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				initializeAddModifyView(typeFrame.addEmployee);
			}
			
		});
		buttonPanel.add(addEmpButton);
		JButton removeButton = new JButton("Terminate Employee");
		removeButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				int row = manageEmpTable.getSelectedRow();
				if(row == -1){
					return;
				}
				modifiedempID = Long.parseLong(manageEmpTable.getValueAt(row, 0).toString());
				Object[] options = {"Yes", "No"};
				int n = JOptionPane.showOptionDialog(frame,
						"Would you like to terminate the employee?",
						"Confirm",
						JOptionPane.YES_NO_OPTION,
						JOptionPane.QUESTION_MESSAGE,
						null,        //do not use a custom Icon
						options,     //the titles of buttons
						options[1]); //default button title
				if(n == JOptionPane.YES_OPTION){
					Map<String,Object> values = new HashMap<String,Object>();
					values.put("date_terminated" , Calendar.getInstance().getTime());
					try {
						ProfileManager.modifyEmployee(modifiedempID, values);
						JOptionPane.showMessageDialog(frame, "Employee Terminated!", "Notice", JOptionPane.INFORMATION_MESSAGE);
						loadManageEmpView();
						frame.revalidate();
					} catch (SQLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
			
		});
		buttonPanel.add(removeButton);
		JButton modifyButton = new JButton("Modify Employee");
		modifyButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				int row = manageEmpTable.getSelectedRow();
				if(row == -1){
					return;
				}
				modifiedempID = Long.parseLong(manageEmpTable.getValueAt(row, 0).toString());
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
		String[] columns = {"Item Name", "Type", "Estimate Time", "Small", "Medium", "Large", "Available"};
		manageMenuTable = new JTable();
		manageMenuTable.setModel(populateMenuTable(columns));
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
		JButton removeButton = new JButton("Remove Item");
		removeButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				int row = manageMenuTable.getSelectedRow();
				if(row == -1){
					return;
				}
				modifiedItemName = manageMenuTable.getValueAt(row, 0).toString();
				Object[] options = {"Yes, remove it", "No, keep it"};
				int n = JOptionPane.showOptionDialog(frame,
						"Would you like to remove the item?",
						"Confirm",
						JOptionPane.YES_NO_OPTION,
						JOptionPane.QUESTION_MESSAGE,
						null,        //do not use a custom Icon
						options,     //the titles of buttons
						options[1]); //default button title
				if(n == JOptionPane.YES_OPTION){
					try {
						MenuManager.setItemAvailability(modifiedItemName, false);
						loadManageMenuView();
						frame.revalidate();
					} catch (SQLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
			
		});
		buttonPanel.add(removeButton);
		JButton modifyButton = new JButton("Modify Item");
		modifyButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				int row = manageMenuTable.getSelectedRow();
				if(row == -1){
					return;
				}
				modifiedItemName = manageMenuTable.getValueAt(row, 0).toString();
				initializeAddModifyView(typeFrame.modifyitem);
			}
			
		});
		buttonPanel.add(modifyButton);
		manageMenuPanel.add(buttonPanel, BorderLayout.SOUTH);
		frame.getContentPane().add(manageMenuPanel, BorderLayout.CENTER);
	}
	
	private JFrame localFrame;
	
	/**
	 * initializeAddModifyView: initialize the add or modify UI
	 */
	public void initializeAddModifyView(typeFrame viewType){
		if(localFrame != null){
			if(localFrame.isActive()){
				localFrame.dispose();
			}
		}
		localFrame = new JFrame();
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
	
	private JPasswordField passwordField;
	private JTextField usernameTextField;
	private JSpinner localDOB;
	private JSpinner localHiredDate;
	private JTextField[] empArrayTextField;
	private JTextField[] menuArrayTextField;
	private JButton bottonButton;
	private JCheckBox availableCB;
	
	/**
	 * initializeEmpMenuProfileView: initialize the menu and employee profile UI
	 */
	private void initializeEmpMenuProfileView(JFrame localFrame, JPanel currentPanel, typeFrame viewType, String[] columns){
		currentPanel = new JPanel(new GridBagLayout());
		JPanel profilePhonePanel = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
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
		empArrayTextField = new JTextField[columns.length - 2];
		menuArrayTextField = new JTextField[columns.length];
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
				}else if (i == 11){
					Calendar calendar = Calendar.getInstance();
					Date currentDate = calendar.getTime();
					calendar.add(Calendar.YEAR, -100);
					Date firstDate = calendar.getTime();
					calendar.add(Calendar.YEAR, 200);
					Date lastDate = calendar.getTime();
					model = new SpinnerDateModel(currentDate, firstDate, lastDate, Calendar.YEAR);
					localHiredDate = new JSpinner(model);
					localHiredDate.setEditor(new JSpinner.DateEditor(localHiredDate, "MM/dd/yyyy"));
					currentPanel.add(localHiredDate,gbc);
				}else{
					empArrayTextField[j] = new JTextField();
					currentPanel.add(empArrayTextField[j],gbc);
					j++;
				}
			
				gbc.gridy++;
				gbc.gridx--;
			}
			if(viewType == typeFrame.addEmployee){
				JLabel usernameLabel = new JLabel("Username:");
				currentPanel.add(usernameLabel, gbc);
				gbc.gridx++;
				usernameTextField = new JTextField();
				currentPanel.add(usernameTextField,gbc);
				gbc.gridy++;
				gbc.gridx--;
				JLabel pwLabel = new JLabel("Password:");
				currentPanel.add(pwLabel, gbc);
				gbc.gridx++;
				passwordField = new JPasswordField();
				currentPanel.add(passwordField, gbc);
			}
			availableCB = new JCheckBox("Terminated:");
			currentPanel.add(availableCB, gbc);
			newPhones = new ArrayList<String>();
			removePhones = new ArrayList<String>();
			newEmails = new ArrayList<String>();
			removeEmails = new ArrayList<String>();
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
	        initEmailPanel();
			gbc = new GridBagConstraints();
	        gbc.gridx = 0; gbc.gridy = 2;
	        gbc.gridwidth = 1; gbc.gridheight = 1;
	        gbc.fill = GridBagConstraints.HORIZONTAL;
	        gbc.anchor = GridBagConstraints.CENTER;
	        gbc.weightx = 1.0; gbc.weighty = 1.0;
	        gbc.ipadx = 2; gbc.ipady = 2;
	        gbc.insets = new Insets(4, 4, 4, 4);
	        profilePhonePanel.add(emailPanel, gbc);
	        if(viewType == typeFrame.modifyEmployee){
				try {
					empProfile = ProfileManager.getEmployeeInfo(modifiedempID, empAttrList);
					phoneNumbers = ProfileManager.getPhoneNumbers(modifiedempID);
					emailAddress = ProfileManager.getEmailAddresses(modifiedempID);
					populateEmpData();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
	        }
		}else{
			int i = 0;
			if(viewType == typeFrame.modifyitem){
				i = 1;
				try {
					menuItem = MenuManager.getItemInfo(modifiedItemName, itemAttrList);
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			for(; i < arrayLabel.length; i++){
				arrayLabel[i] = new JLabel(columns[i]);
				currentPanel.add(arrayLabel[i], gbc);
				gbc.gridx++;
				menuArrayTextField[i] = new JTextField();
				menuArrayTextField[i].setPreferredSize(new Dimension(150,20));
				currentPanel.add(menuArrayTextField[i], gbc);
				gbc.gridy++;
				gbc.gridx--;
			}
			availableCB = new JCheckBox("Available:");
			currentPanel.add(availableCB, gbc);
			if(viewType == typeFrame.modifyitem){
				populateItemData();
			}
		}
		JPanel localBottomPanel = new JPanel();
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.fill = GridBagConstraints.VERTICAL;
		String[] buttonName = {"Add Employee", "Modify Employee", "Add Item", "Modify Item"};
		bottonButton = new JButton(buttonName[viewType.ordinal()]);
		bottonButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				if(e.getActionCommand().equals("Add Employee")){
					if(!addNewEmp()){
						return;
					}
					loadManageEmpView();
					frame.revalidate();
				}
				if(e.getActionCommand().equals("Add Item")){
					addNewItem();
					loadManageMenuView();
					frame.revalidate();
				}
				if(e.getActionCommand().equals("Modify Item")){
					modifyItem();
					loadManageMenuView();
					frame.revalidate();
				}
				if(e.getActionCommand().equals("Modify Employee")){
					modifyEmp();
					loadManageEmpView();
					frame.revalidate();
				}
				localFrame.dispose();
			}
			
		});
		gbc.gridx++;
		localBottomPanel.add(bottonButton, gbc);
		localFrame.getContentPane().add(localBottomPanel, BorderLayout.SOUTH);
		localFrame.getContentPane().add(localScollPane, BorderLayout.CENTER);
	}
	
	/**
	 * adding new item.
	 */
	private void addNewItem(){
		Map<String,Object> values = new HashMap<String,Object>();
		for(int i = 0; i < menuArrayTextField.length; i++){
			if(menuArrayTextField[i].getText().isEmpty() && i != 3){
				return;
			}
		}
		values.put("name", menuArrayTextField[0].getText());
		values.put("type", ItemType.parseItemType(menuArrayTextField[1].getText()));
		values.put("est_prep_time", Integer.parseInt(menuArrayTextField[2].getText()));
		values.put("available", availableCB.isSelected());
		values.put("small_price", new BigDecimal(menuArrayTextField[3].getText()));
		values.put("medium_price",new BigDecimal(menuArrayTextField[4].getText()));
		values.put("large_price",new BigDecimal(menuArrayTextField[5].getText()));
		values.put("price", BigDecimal.ZERO);
		try {
			MenuManager.addItem(values);
			JOptionPane.showMessageDialog(frame, "New Item created!", "Notice", JOptionPane.INFORMATION_MESSAGE);
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	/**
	 * adding new employee.
	 */
	private boolean addNewEmp(){
		Map<String,Object> values = new HashMap<String,Object>();
		for(int i = 0; i < empArrayTextField.length; i++){
			if(empArrayTextField[i] != null){
				if(empArrayTextField[i].getText().isEmpty()){
					JOptionPane.showMessageDialog(null, "Fields cannot be empty", "Notice", JOptionPane.ERROR_MESSAGE);
					return false;
				}
			}
		}
		values.put("first_name", empArrayTextField[0].getText());
		values.put("middle_name", empArrayTextField[1].getText());
		values.put("last_name", empArrayTextField[2].getText());
		values.put("date_of_birth", (Date)localDOB.getValue());
		values.put("username", usernameTextField.getText());
		values.put("street", empArrayTextField[3].getText());
		values.put("city", empArrayTextField[4].getText());
		values.put("state", empArrayTextField[5].getText());
		values.put("zip", empArrayTextField[6].getText());
		values.put("ssn", empArrayTextField[7].getText());
		values.put("hourly_rate",new BigDecimal((empArrayTextField[8].getText())));
		values.put("hours_per_week", Float.parseFloat(empArrayTextField[9].getText()));
		values.put("date_hired", (Date)localHiredDate.getValue());
		values.put("job_title", empArrayTextField[10].getText());
		if(availableCB.isSelected()){
			values.put("date_terminated" , Calendar.getInstance().getTime());
		}else{
			values.put("date_terminated" , null);
		}
		try {
			if(empArrayTextField[5].getText().length() != 2){
				JOptionPane.showMessageDialog(null, "State must be 2 character!", "Notice", JOptionPane.ERROR_MESSAGE);
				return false;
			}
			if(usernameTextField.getText().isEmpty()){
				JOptionPane.showMessageDialog(null, "Username cannot be empty!", "Notice", JOptionPane.ERROR_MESSAGE);
				return false;
			}
			if(passwordField.getPassword().length == 0){
				JOptionPane.showMessageDialog(null, "Passowrd cannot be empty!", "Notice", JOptionPane.ERROR_MESSAGE);
				return false;
			}
			if(ProfileManager.getPersonID(usernameTextField.getText()) != ProfileManager.USERNAME_NOT_IN_TABLE){
				JOptionPane.showMessageDialog(null, "Username already exist!", "Notice", JOptionPane.ERROR_MESSAGE);
				return false;
			}
			long personId = ProfileManager.createEmployee(values, String.valueOf(passwordField.getPassword()));
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
			JOptionPane.showMessageDialog(frame, "Employee account created!", "Notice", JOptionPane.INFORMATION_MESSAGE);
			return true;
		} catch (NoSuchAlgorithmException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return true;
	}
	
	/**
	 * modifying the employee info.
	 */
	private void modifyEmp(){
		Map<String,Object> values = new HashMap<String,Object>();
		values.put("first_name", empArrayTextField[0].getText());
		values.put("middle_name", empArrayTextField[1].getText());
		values.put("last_name", empArrayTextField[2].getText());
		values.put("date_of_birth", (Date)localDOB.getValue());
		values.put("street", empArrayTextField[3].getText());
		values.put("city", empArrayTextField[4].getText());
		values.put("state", empArrayTextField[5].getText());
		values.put("zip", empArrayTextField[6].getText());
		values.put("ssn", empArrayTextField[7].getText());
		values.put("hourly_rate",new BigDecimal((empArrayTextField[8].getText())));
		values.put("hours_per_week", Float.parseFloat(empArrayTextField[9].getText()));
		values.put("date_hired", (Date)localHiredDate.getValue());
		values.put("job_title", empArrayTextField[10].getText());
		if(availableCB.isSelected()){
			values.put("date_terminated" , Calendar.getInstance().getTime());
		}else{
			values.put("date_terminated" , null);
		}
		try {
			ProfileManager.modifyEmployee(modifiedempID, values);
			for(int i = 0; i < removePhones.size(); i++){
				phoneNumbers.remove(removePhones.get(i));
				ProfileManager.removePhoneNumber(userID, removePhones.get(i));
			}
			for(int i = 0; i < newPhones.size(); i++){
				phoneNumbers.add(newPhones.get(i));
				ProfileManager.addPhoneNumber(userID, newPhones.get(i));
			}
			for(int i = 0; i < removeEmails.size(); i++){
				emailAddress.remove(removeEmails.get(i));
				ProfileManager.removeEmailAddress(userID, removeEmails.get(i));
			}
			for(int i = 0; i < newEmails.size(); i++){
				emailAddress.add(newEmails.get(i));
				ProfileManager.addEmailAddress(userID, newEmails.get(i));
			}
			JOptionPane.showMessageDialog(frame, "Employee account updated!", "Notice", JOptionPane.INFORMATION_MESSAGE);
			
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	/**
	 * modifying the item info.
	 */
	private void modifyItem(){
		Map<String,Object> values = new HashMap<String,Object>();
		for(int i = 1; i < menuArrayTextField.length; i++){
			if(menuArrayTextField[i].getText().isEmpty() && i != 3){
				return;
			}
		}
		values.put("type", ItemType.parseItemType(menuArrayTextField[1].getText()));
		values.put("est_prep_time", Integer.parseInt(menuArrayTextField[2].getText()));
		values.put("available", availableCB.isSelected());
		values.put("small_price", new BigDecimal(menuArrayTextField[3].getText()));
		values.put("medium_price",new BigDecimal(menuArrayTextField[4].getText()));
		values.put("large_price", new BigDecimal(menuArrayTextField[5].getText()));
		try {
			MenuManager.modifyItem(modifiedItemName ,values);
			JOptionPane.showMessageDialog(frame, "New Item updated!", "Notice", JOptionPane.INFORMATION_MESSAGE);
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	
	/**
	 * populate ItemData
	 */
	private void populateItemData(){
		for(String attr : itemAttrList){
			switch (attr)
            {
            case "type":
            	menuArrayTextField[1].setText(menuItem.get(attr).toString());
                break;
            case "est_prep_time":
            	menuArrayTextField[2].setText(menuItem.get(attr).toString());
                break;
            case "small_price":
            	menuArrayTextField[3].setText(menuItem.get(attr).toString());
                break;
            case "medium_price":
            	menuArrayTextField[4].setText(menuItem.get(attr).toString());
                break;
            case "large_price":
            	menuArrayTextField[5].setText(menuItem.get(attr).toString());
                break;
            case "available":
            	if(menuItem.get(attr).equals(true)){
            		availableCB.setSelected(true);
            	}else{
            		availableCB.setSelected(false);
            	}
            }
		}
	}
	
	/**
	 * populate employee data.
	 */
	private void populateEmpData(){
		for(String attr : empAttrList){
			switch (attr)
            {
            case "first_name":
            	empArrayTextField[0].setText(empProfile.get(attr).toString());
                break;
            case "middle_name":
            	empArrayTextField[1].setText(empProfile.get(attr).toString());
                break;
            case "last_name":
            	empArrayTextField[2].setText(empProfile.get(attr).toString());
                break;
            case "date_of_birth":
            	localDOB.setValue(empProfile.get(attr));
                break;
            case "street":
            	empArrayTextField[3].setText(empProfile.get(attr).toString());
                break;
            case "city":
            	empArrayTextField[4].setText(empProfile.get(attr).toString());
                break;
            case "state":
            	empArrayTextField[5].setText(empProfile.get(attr).toString());
                break;
            case "zip":
            	empArrayTextField[6].setText(empProfile.get(attr).toString());
                break;
            case "ssn":
            	empArrayTextField[7].setText(empProfile.get(attr).toString());
                break;
            case "hourly_rate":
            	empArrayTextField[8].setText(empProfile.get(attr).toString());
                break;
            case "hours_per_week":
            	empArrayTextField[9].setText(empProfile.get(attr).toString());
                break;
            case "date_hired":
            	localHiredDate.setValue(empProfile.get(attr));
                break;
            case "job_title":
            	empArrayTextField[10].setText(empProfile.get(attr).toString());
                break;
            case "date_terminated":
            	if(empProfile.get(attr) == null){
            		availableCB.setSelected(false);
            	}else{
            		availableCB.setSelected(true);
            	}            	
                break;
            }
		}
		for(String phoneNum : phoneNumbers){
			phoneNumberComboBox.addItem(phoneNum);
		}
		if(phoneNumbers.size() != 0){
			removePhoneNumberButton.setEnabled(true);
			phoneNumberComboBox.setEnabled(true);
			
		}
		for(String emailAddr : emailAddress){
			emailComboBox.addItem(emailAddr);
		}
		if(emailAddress.size() != 0){
			removeEmailButton.setEnabled(true);
			emailComboBox.setEnabled(true);
		}
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
		populateProfileData();
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
		if(manageMenuPanel != null){
			frame.getContentPane().remove(manageMenuPanel);
		}
		if(bottomPanel != null){
			frame.getContentPane().remove(bottomPanel);
		}
		if(manageEmpPanel != null){
			frame.getContentPane().remove(manageEmpPanel);
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
		if(manageEmpPanel != null){
			frame.getContentPane().remove(manageEmpPanel);
		}
		if(bottomPanel != null){
			frame.getContentPane().remove(bottomPanel);
		}
		if(manageMenuPanel != null){
			frame.getContentPane().remove(manageMenuPanel);
		}
		initializeMenuView();
	}
	
	/**
	 * populateMenuTable: populate the initial menu items.
	 */
	private MyModel populateMenuTable(){
		String[] columns = { "Name", "Type", "Size", "Small Price", "Medium Price", "Large Price", "Estimate Time"};
		Object[][] data = new Object[menu.size()][columns.length];
		Iterator<String> it = menu.keySet().iterator();
		int i = 0;
		while(it.hasNext()){
			String itemKey = it.next();
			Map<String, Object> item = menu.get(itemKey);
			data[i][0] = itemKey;
			Iterator<String> itemInfo = item.keySet().iterator();
			while(itemInfo.hasNext()){
				String col = itemInfo.next();
				switch (col)
	            {
	            case "type":
	                data[i][1] = item.get(col);
	                break;
	            case "small_price":
	            	data[i][3] = item.get(col);
	                break;
	            case "medium_price":
	            	data[i][4] = item.get(col);
	                break;
	            case "large_price":
	            	data[i][5] = item.get(col);
	                break;
	            case "est_prep_time":
	            	data[i][6] = item.get(col);
	                break;
	            }
			}
			i++;
			
		}
		Arrays.sort(data, new Comparator<Object[]>(){

			@Override
			public int compare(Object[] o1, Object[] o2) {				
				return o1[1].toString().compareTo(o2[1].toString());
			}
			
		});
		return new MyModel(data, columns, 2);
	}
	
	private MyModel createCarTable(){
		String[] columns = { "Name", "Type","Size", "Price", "Estimate Time", "Quantity" };
		Object[][] data = {};
		return new MyModel(data, columns, 5);
	}
	
	/**
	 * populateTable: populate the initial ordered items.
	 */
	private MyModel populateTable(String[] columns){
		Object[][] data = null;
		String[] orderAttr = { "custid", "order_type", "time_order_placed", "total", "pay_method"};
		try {
			List<Long> activeOrders = OrderManager.getActiveOrders();
			data = new Object[activeOrders.size()][columns.length];
			for(int i = 0; i < activeOrders.size(); i++){
				Map<String,Object> orders = OrderManager.getOrderInfo(activeOrders.get(i), Arrays.asList(orderAttr));
				data[i][0] = activeOrders.get(i);
				Iterator<String> itemInfo = orders.keySet().iterator();
				while(itemInfo.hasNext()){
					String col = itemInfo.next();
					switch (col)
					{
					case "custid":
						data[i][1] = orders.get(col);
						break;
					case "order_type":
						data[i][2] = orders.get(col);
						break;
					case "time_order_placed":
						data[i][3] = orders.get(col);
						break;
					case "total":
						data[i][4] = orders.get(col);
						break;
					case "pay_method":
						data[i][5] = orders.get(col);
						break;
					}
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Arrays.sort(data, new Comparator<Object[]>(){

			@Override
			public int compare(Object[] o1, Object[] o2) {				
				return o1[3].toString().compareTo(o2[3].toString());
			}
			
		});
		return new MyModel(data, columns, -1);
	}
	
	
	/**
	 * populate the menu table
	 * @param columns
	 * @return new MyModel
	 */
	private MyModel populateMenuTable(String[] columns){
		Object[][] data = null;
		String[] attr = {"type", "est_prep_time", "small_price", "medium_price", "large_price","available"};
		try {
			List<String> items = MenuManager.getAllItems();
			data = new Object[items.size()][attr.length + 1];
			int i = 0;
			for(String name : items){
				Map<String,Object> empDetail = MenuManager.getItemInfo(name, Arrays.asList(attr));
				data[i][0] = name;
				Iterator<String> it = empDetail.keySet().iterator();
				while(it.hasNext()){
					String col = it.next();
					switch (col)
					{
					case "type":
						data[i][1] = empDetail.get(col);
						break;
					case "est_prep_time":
						data[i][2] = empDetail.get(col);
						break;
					case "small_price":
						data[i][3] = empDetail.get(col);
						break;
					case "medium_price":
						data[i][4] = empDetail.get(col);
						break;
					case "large_price":
						data[i][5] = empDetail.get(col);
						break;
					case "available":
						data[i][6] = empDetail.get(col);
						break;
					}
				}
				i++;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new MyModel(data, columns, -1);
	}
	
	/**
	 * populate the employee table
	 * @param columns
	 * @return new MyModel
	 */
	private MyModel populateEmpTable(String[] columns){
		Object[][] data = null;
		String[] attr = {"first_name", "middle_name", "last_name", "job_title", "hourly_rate", "date_hired","date_terminated"};
		try {
			List<Long> emps = ProfileManager.getAllEmployeeID();
			data = new Object[emps.size()][attr.length + 1];
			int i = 0;
			for(long id : emps){
				Map<String,Object> empDetail = ProfileManager.getEmployeeInfo(id, Arrays.asList(attr));
				data[i][0] = id;
				Iterator<String> it = empDetail.keySet().iterator();
				while(it.hasNext()){
					String col = it.next();
					switch (col)
					{
					case "first_name":
						data[i][1] = empDetail.get(col);
						break;
					case "middle_name":
						data[i][2] = empDetail.get(col);
						break;
					case "last_name":
						data[i][3] = empDetail.get(col);
						break;
					case "job_title":
						data[i][4] = empDetail.get(col);
						break;
					case "hourly_rate":
						data[i][5] = empDetail.get(col);
						break;
					case "date_hired":
						data[i][6] = empDetail.get(col);
						break;
					case "date_terminated":
						if(empDetail.get(col) != null){
							data[i][7] = empDetail.get(col);
						}else{
							data[i][7] = "";
						}
						break;
					}
				}
				i++;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new MyModel(data, columns, -1);
	}
	

	
	/**
	 * MyModel: Model for Tables so that the elements cannot be modified.
	 */
	public class MyModel extends DefaultTableModel{

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private int editableCol;

		MyModel(Object[][] data, Object[] columns, int editableColumn){
			super(data,columns);
			this.editableCol = editableColumn;
		}
		
		@Override
		public void setValueAt(Object aValue, int row, int column){
			try{
				if(column == 5){
					int value = Integer.parseInt(aValue.toString());
				}
				super.setValueAt(aValue, row, column);
			}catch(NumberFormatException ex){
				return;
			}
		}

		@Override
		public boolean isCellEditable(int row, int column){
			if(column == editableCol){
				return true;
			}
			return false;
		}
		
	}
}
