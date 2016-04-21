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
import java.util.Calendar;
import java.util.Date;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JMenuBar;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpinnerDateModel;
import javax.swing.table.DefaultTableModel;

public class EmployeeView {
	
	private String[] profileFields= new String[]{
			"FirstName",
			"MiddleName",
			"LastName",
			"DOB",
			"Address",
			"City",
			"State",
			"Zip",
			"Email",
			"SSN",
			"Hourly Rate",
			"Hours/Week",
			"Date Hired",
			"Date Terminated",
			"Job Title"
		};
	private String[] CustomerProfileFields= new String[]{
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
			"Email:"
	};
	
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
	private JFrame frame;
	private JPanel bottomPanel;
	private JTable menuTable;
	private JTable carTable;
	private JPanel createOrderPanel;
	private JPanel orderButtonPanel;
	private JPanel profilePanel;
	private JPanel ordersPanel;
	private JSpinner DOBSpinner;
	private JScrollPane profileScollPane;
	
	public void run(){
		this.frame.setVisible(true);
	}
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
				LoginView.main(null);
			}
		});
		menuBar.add(logoutButton);
	}
	
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
	
	public void initializeCreateCustomerView(){
		initPhoneNumberPanel();
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
		
		signUpButton = new JButton("Submit");
		cancelButton = new JButton("Cancel");
		JPanel buttonPanel = new JPanel();
		buttonPanel.add(signUpButton);
		buttonPanel.add(cancelButton);
		
		gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 1; gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.PAGE_START;
        gbc.weightx = 1.0; gbc.weighty = 1.0;
        gbc.ipadx = 2; gbc.ipady = 2;
        gbc.insets = new Insets(4, 4, 4, 4);
		mainPanel.add(buttonPanel, gbc);
		
		frame.getContentPane().add(mainPanel, BorderLayout.CENTER);
	}
	
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
		gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.gridwidth = 1; gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 1.0; gbc.weighty = 1.0;
        gbc.ipadx = 2; gbc.ipady = 2;
        gbc.insets = new Insets(4, 4, 4, 4);
        profilePhonePanel.add(phoneNumberPanel, gbc);
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
		carTable = populateCarTable();
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
		carTable = pupulateOrdersTable();
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
		frame.getContentPane().remove(bottomPanel);
		initializeProfileView();
	}
	
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
		frame.getContentPane().remove(bottomPanel);
		initializeCreateCustomerView();
	}
	
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
		frame.getContentPane().remove(bottomPanel);
		initializeCreateOrderView();
	}
	
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
		frame.getContentPane().remove(bottomPanel);
		initializeOrdersView();
	}

	private MyModel populateMenuTable(){
		String[] columns = { "Name", "Type", "Price", "Estimate Time"};
		Object[][] data = { {"Peperoni", "Pizza", new Integer(25), new Integer(30)},
							{"Cola", "Drinks", new Integer(4), new Integer(0)}
		};
		return new MyModel(data, columns);
	}
	
	private JTable populateCarTable(){
		String[] columns = { "Name", "Type", "Price", "Estimate Time", "Quantity" };
		Object[][] data = {};
		return new JTable(data, columns);
	}
	
	private JTable pupulateOrdersTable(){
		String[] columns = { "Order ID", "Customer ID", "Time Placed", "Time Order out", "Subtotal", "Tax", "Total" };
		Object[][] data = {};
		return new JTable(data, columns);
	}
	
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
