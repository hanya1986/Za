package edu.rit.cs.Za;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
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
import javax.swing.JList;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JMenu;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpinnerDateModel;
import javax.swing.SpringLayout;
import javax.swing.border.Border;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;

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
		"Email",
		"Phone"
	};
	
	private JFrame frame;
	private JPanel bottomPanel;
	private JTable menuTable;
	private JTable carTable;
	private JPanel orderPanel;
	private JPanel orderButtonPanel;
	private JPanel profilePanel;
	private JSpinner DOBSpinner;
	private JScrollPane profileScollPane;
	
	public void run(){
		this.frame.setVisible(true);
	}
	public CustomerView(){
		initialize();
		initializeProfileView();
	}
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					CustomerView window = new CustomerView();
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
		
		JButton historyButton = new JButton("History");
		menuBar.add(historyButton);
		
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
	
	public void initializeProfileView(){
		profilePanel = new JPanel(new GridBagLayout());
		profileScollPane = new JScrollPane(profilePanel);
		JLabel[] arrayLabel = new JLabel[profileFields.length];
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
			}else{
				arrayTextField[j] = new JTextField();
				profilePanel.add(arrayTextField[j],gbc);
				j++;
			}
			gbc.gridy++;
			gbc.gridx--;
		}
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
	
	public void initializeOrderView(){
		
		orderPanel = new JPanel();
		frame.getContentPane().add(orderPanel, BorderLayout.CENTER);
		JButton add = new JButton("Add");
		JButton remove = new JButton("Remove");
		
		orderPanel.setLayout(new GridBagLayout());
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
		orderPanel.add(menuLabel, gbc);
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.weightx = 1;
		gbc.weighty = 1;
		MyModel table = populateMenuTable();
		menuTable = new JTable();
		menuTable.setModel(table);
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
		JLabel carLabel = new JLabel("Shopping car");
		orderPanel.add(carLabel, gbc);
		gbc.gridy++;
		gbc.weightx = 1;
		gbc.weighty = 1;
		carTable = populateCarTable();
		sp = new JScrollPane(carTable);
		sp.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
		orderPanel.add(sp, gbc);
		
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
		gbc.gridx++;
		bottomPanel.add(placeButton, BorderLayout.LINE_START);
		frame.getContentPane().add(bottomPanel, BorderLayout.SOUTH);
	}
	
	private void loadProfileView(){
		frame.getContentPane().remove(orderPanel);
		frame.getContentPane().remove(bottomPanel);
		initializeProfileView();
	}
	
	private void loadOrderView(){
		frame.getContentPane().remove(profilePanel);
		frame.getContentPane().remove(profileScollPane);
		frame.getContentPane().remove(bottomPanel);
		initializeOrderView();
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
