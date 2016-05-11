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
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpinnerDateModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import edu.rit.cs.Za.*;
import javafx.collections.FXCollections;

public class CustomerView {
	
	private String[] profileFields= new String[]{
		"FirstName",
		"MiddleName",
		"LastName",
		"DOB",
		"Address",
		"City",
		"State",
		"Zip",
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
	
	private JFrame frame;
	private JPanel bottomPanel;
	private JTable menuTable;
	private JTable carTable;
	private JPanel orderPanel;
	private JPanel orderButtonPanel;
	private JPanel profilePanel;
	private JSpinner DOBSpinner;
	private JPanel mainPanel;
	private JButton signUpButton;
	private JRadioButton deliverRB;
	private JRadioButton pickupRB;
	private JTextField[] arrayTextField;
	private JComboBox<String> itemSizeComboBox;
	
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
	
	private long userID;
	private Map<String,Object> custProfile;
	private List<String> phoneNumbers;
	private List<String> newPhones;
	private List<String> removePhones;
	private List<String> emailAddress;
	private List<String> newEmails;
	private List<String> removeEmails;
	private Map<String,Map<String,Object>> menu = new HashMap<String,Map<String,Object>>();
	private List<String> custAttrList = new ArrayList<String>(Arrays.asList(custAttr));
	private List<String> itemAttrList = new ArrayList<String>(Arrays.asList(itemsAttr));
	
	/**
	 * CustomerView: Constructor
	 * @param userID
	 */
	public CustomerView(long userID){
		this.userID = userID;
		try {
			List<String> items = MenuManager.getAvailableItems();
			Iterator<String> itemIt = items.iterator();
			while(itemIt.hasNext()){
				String iName = itemIt.next();
				Map<String,Object> itemDetail = MenuManager.getItemInfo(iName, itemAttrList);
				menu.put(iName, itemDetail);
			}
			custProfile = ProfileManager.getCustomerInfo(this.userID, custAttrList);
			phoneNumbers = ProfileManager.getPhoneNumbers(this.userID);
			emailAddress = ProfileManager.getEmailAddresses(this.userID);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		initialize();
		initializeProfileView();
		populateProfileData();
		this.frame.setVisible(true);
	}
	
	/**
	 * populateProfileData: populating the customer data from database
	 */
	private void populateProfileData(){
		for(String attr : custAttr){
			switch (attr)
            {
            case "first_name":
            	arrayTextField[0].setText(custProfile.get(attr).toString());
                break;
            case "middle_name":
            	arrayTextField[1].setText(custProfile.get(attr).toString());
                break;
            case "last_name":
            	arrayTextField[2].setText(custProfile.get(attr).toString());
                break;
            case "date_of_birth":
            	DOBSpinner.setValue(custProfile.get(attr));
                break;
            case "street":
            	arrayTextField[3].setText(custProfile.get(attr).toString());
                break;
            case "city":
            	arrayTextField[4].setText(custProfile.get(attr).toString());
                break;
            case "state":
            	arrayTextField[5].setText(custProfile.get(attr).toString());
                break;
            case "zip":
            	arrayTextField[6].setText(custProfile.get(attr).toString());
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
		for(String attr : custAttr){
			switch (attr)
            {
            case "first_name":
            	custProfile.put(attr, arrayTextField[0].getText());
                break;
            case "middle_name":
            	custProfile.put(attr, arrayTextField[1].getText());
                break;
            case "last_name":
            	custProfile.put(attr, arrayTextField[2].getText());
                break;
            case "date_of_birth":
            	custProfile.put(attr, DOBSpinner.getValue());
                break;
            case "street":
            	custProfile.put(attr, arrayTextField[3].getText());
                break;
            case "city":
            	custProfile.put(attr, arrayTextField[4].getText());
                break;
            case "state":
            	custProfile.put(attr, arrayTextField[5].getText());
                break;
            case "zip":
            	custProfile.put(attr, arrayTextField[6].getText());
                break;
            }
		}
		try {
			ProfileManager.modifyCustomer(userID, custProfile);
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
	
	/**
	 * Launch the application.
	 */
//	public static void main(String[] args) {
//		EventQueue.invokeLater(new Runnable() {
//			public void run() {
//				try {
//					CustomerView window = new CustomerView(0);
//					window.frame.setVisible(true);
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			}
//		});
//	}
//	
	/**
	 * initialize: initializing the frame and the top tool bar.
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
		
		JMenuBar menuBar = new JMenuBar();
		orderButtonPanel.add(menuBar);
		FXCollections.observableArrayList();
		JButton profileButton = new JButton("Edit Profile");
		profileButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				loadProfileView();
				frame.revalidate();
			}
		});
		menuBar.add(profileButton);
		
		JButton orderButton = new JButton("Place Order");
		orderButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				loadOrderView();
				frame.revalidate();
			}
		});
		menuBar.add(orderButton);
		
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
	
	/*
	 * initializeProfileView: initializing the Profile UI.
	 */
	public void initializeProfileView(){
		newPhones = new ArrayList<String>();
		removePhones = new ArrayList<String>();
		newEmails = new ArrayList<String>();
		removeEmails = new ArrayList<String>();
		profilePanel = new JPanel(new GridBagLayout());
		JLabel[] arrayLabel = new JLabel[profileFields.length];
		arrayTextField = new JTextField[profileFields.length - 1];
		SpinnerDateModel model;
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		int j = 0;
		initPhoneNumberPanel();
		initEmailPanel();
		mainPanel = new JPanel(new GridBagLayout());
		for(int i = 0; i < arrayLabel.length; i++){
			arrayLabel[i] = new JLabel(profileFields[i]);
			profilePanel.add(arrayLabel[i],gbc);
			gbc.gridx++;
			if(i == 3){
				Calendar calendar = Calendar.getInstance();
				calendar.add(Calendar.YEAR, -120);
				Date firstDate = calendar.getTime();
				calendar.add(Calendar.YEAR, 120);
				calendar.add(Calendar.YEAR, -13);
				Date lastDate = calendar.getTime();
				model = new SpinnerDateModel(lastDate, firstDate, lastDate, Calendar.YEAR);
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
		gbc = new GridBagConstraints();
		gbc.gridx = 0; gbc.gridy = 0;
        gbc.gridwidth = 1; gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.VERTICAL;
        gbc.anchor = GridBagConstraints.PAGE_END;
        gbc.weightx = 1.0; gbc.weighty = 1.0;
        gbc.ipadx = 2; gbc.ipady = 2;
        gbc.insets = new Insets(4, 4, 4, 4);
		mainPanel.add(profilePanel, gbc);
		
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
				updateProfile();
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
	
	/*
	 * initializeOrderView: initializing the Order UI.
	 */
	public void initializeOrderView(){
		
		orderPanel = new JPanel();
		frame.getContentPane().add(orderPanel, BorderLayout.CENTER);
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
		orderPanel.setLayout(new GridBagLayout());
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
		orderPanel.add(menuLabel, gbc);
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
		orderPanel.add(sp, gbc);
		
		gbc.weightx = 0.3;
		gbc.weighty = 0.3;
		gbc.gridx++;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		orderPanel.add(AddRemovePanel, gbc);
		
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weightx = 0.3;
		gbc.weighty = 0.3;
		gbc.gridx++;
		gbc.gridy = 0;
		JLabel carLabel = new JLabel("Shopping Cart");
		orderPanel.add(carLabel, gbc);
		gbc.gridy++;
		gbc.weightx = 1;
		gbc.weighty = 1;
		carTable = new JTable(createCarTable());
		sp = new JScrollPane(carTable);
		sp.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
		orderPanel.add(sp, gbc);
		
		bottomPanel = new JPanel(new BorderLayout());
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.fill = GridBagConstraints.VERTICAL;
		JButton resetButton = new JButton("Reset");
		resetButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				DefaultTableModel carModel = (DefaultTableModel) carTable.getModel();
				carModel.setRowCount(0);
			}
			
		});
		gbc.gridx++;
		JPanel centerBottomPanel = new JPanel();
		centerBottomPanel.add(resetButton);
		bottomPanel.add(centerBottomPanel, BorderLayout.CENTER);
		JButton pastButton = new JButton("Past Orders");
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
		gbc.gridx++;
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
				PaymentView pv = new PaymentView(userID, false, orderItems, ordertype);
				pv.runGUI();
			}
			
		});
		gbc.gridx++;
		bottomPanel.add(placeButton, BorderLayout.LINE_START);
		frame.getContentPane().add(bottomPanel, BorderLayout.SOUTH);
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
	
	/*
	 * loadProfileView: Reloading the Profile UI.
	 */
	private void loadProfileView(){
	    if (orderPanel != null)
	        frame.getContentPane().remove(orderPanel);
	    
	    if (bottomPanel != null)
	        frame.getContentPane().remove(bottomPanel);
	    
		initializeProfileView();
		populateProfileData();
	}
	
	/*
	 * loadOrderView: Reloading the Order UI.
	 */
	private void loadOrderView(){
	    if (mainPanel != null)
	        frame.getContentPane().remove(mainPanel);
		initializeOrderView();
	}

	/**
	 * populateMenuTable: populating the initial menu items.
	 * @return
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
	
	/**
	 * createCarTable: creating the shopping card table.
	 * @return
	 */
	private MyModel createCarTable(){
		String[] columns = { "Name", "Type","Size", "Price", "Estimate Time", "Quantity" };
		Object[][] data = {};
		return new MyModel(data, columns, 5);
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
