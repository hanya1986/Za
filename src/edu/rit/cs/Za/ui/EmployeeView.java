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
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpinnerDateModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import edu.rit.cs.Za.OrderType;
import edu.rit.cs.Za.ProfileManager;

import java.sql.SQLException;
import java.sql.Timestamp;

import edu.rit.cs.Za.ConnectionManager;
import edu.rit.cs.Za.MenuManager;
import edu.rit.cs.Za.OrderManager;
import edu.rit.cs.Za.Queries;
import edu.rit.cs.Za.TablePopulator;
import edu.rit.cs.Za.ZaDatabase;
import edu.rit.cs.Za.Queries.DelivererTime;
import edu.rit.cs.Za.ui.CustomerView.MyModel;

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
	
	private String[] custAttr = new String[]{
			"first_name",
			"middle_name",
			"last_name",
			"date_of_birth",
			"street",
			"city",
			"state",
			"zip",
		};
	private String[] itemsAttr = new String[]{
			"type",
			"price",
			"est_prep_time",
			"small_price",
			"medium_price",
			"large_price",
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
	private JTextField[] arrayTextField;
	private JRadioButton deliverRB;
	private JRadioButton pickupRB;
	private JComboBox<String> itemSizeComboBox;
	
	private long userID;
	private boolean isManager;
	
	private JTextField qsItemNameTextField;
	private JTextField qsQuantityTextField;
	private JButton qsRefreshButton;
	
	private Map<String,Object> empProfile;
	private List<String> phoneNumbers;
	private List<String> newPhones;
	private List<String> removePhones;
	private List<String> emailAddress;
	private List<String> newEmails;
	private List<String> removeEmails;
	private Map<String,Map<String,Object>> menu = new HashMap<String,Map<String,Object>>();
	private List<String> empAttrList = new ArrayList<String>(Arrays.asList(empAttr));
	private List<String> custAttrList = new ArrayList<String>(Arrays.asList(custAttr));
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
					EmployeeView window = new EmployeeView(1, true);
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
		for(String attr : empAttr){
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
            	arrayTextField[10].setText(empProfile.get(attr).toString());
                break;
            case "date_terminated":
            	arrayTextField[11].setText(empProfile.get(attr).toString());
                break;
            case "job_title":
            	arrayTextField[12].setText(empProfile.get(attr).toString());
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
		for(String attr : profileFields){
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
		arrayTextField = new JTextField[profileFields.length - 1];
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
							OrderManager.removeOrder(Long.parseLong(carTable.getValueAt(row, 0).toString()));
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
							OrderManager.removeItems(orderID, removeItems);
							OrderManager.changeQuantities(orderID, modify.itemsModified());
							OrderManager.addItems(orderID, modify.itemsAdded());
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
	
	/**
	 * initializeStatView: initialize the statistics UI
	 */
	public void initializeStatView(){
		statPanel = new JPanel(new BorderLayout());
		String[] columns = { "Log", "Type", "Time"};
		statTable = new JTable(populateStatLogsTable(columns));
		JScrollPane sp = new JScrollPane(statTable);
		statPanel.add(sp, BorderLayout.CENTER);
		
		//JPanel topPanel = new JPanel();
		JPanel topPanel = new JPanel();
		JComboBox<String> selectionComboBox = new JComboBox<String>();
		selectionComboBox.setEnabled(true);
		selectionComboBox.addItem("Store");
		selectionComboBox.addItem("Employee");
		selectionComboBox.addItem("Product");
		topPanel.add(selectionComboBox);
		
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
		
		JLabel Search = new JLabel("      Search:");
		topPanel.add(Search);
		searchTextField = new JTextField();
		searchTextField.setPreferredSize(new Dimension(140,20));
		topPanel.add(searchTextField);
		
		statPanel.add(topPanel, BorderLayout.NORTH);
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
	 * populateStatLogsTable: populate the info for statistics and logs table.
	 */
	private MyModel populateStatLogsTable(String[] columns){
		Object[][] data = {};
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
