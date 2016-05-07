package edu.rit.cs.Za.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import edu.rit.cs.Za.MenuManager;
import edu.rit.cs.Za.OrderManager;
import edu.rit.cs.Za.OrderType;
import edu.rit.cs.Za.ProfileManager;
import edu.rit.cs.Za.ui.CustomerView.MyModel;

public class ModifyOrderPanel extends JPanel{

	private String[] itemsAttr = new String[]{
			"type",
			"price",
			"est_prep_time",
			"small_price",
			"medium_price",
			"large_price",
		};
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTable pastOrderTable;
	private JTable menuTable;
	private JComboBox<String> itemSizeComboBox;
	private long orderid;
	private OrderType type;
	private Map<String,Map<String,Object>> menu = new HashMap<String,Map<String,Object>>();
	private Map<String,Integer> orderItems = new HashMap<String,Integer>();
	private Map<String,Integer> addedItems = new HashMap<String, Integer>();
	private List<String> removedItems = new ArrayList<String>();
	public ModifyOrderPanel(long orderid){
		this.orderid = orderid;
		try {
			List<String> items = MenuManager.getAvailableItems();
			Iterator<String> itemIt = items.iterator();
			while(itemIt.hasNext()){
				String iName = itemIt.next();
				Map<String,Object> itemDetail = MenuManager.getItemInfo(iName, Arrays.asList(itemsAttr));
				menu.put(iName, itemDetail);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		initialize();
	}
	
	@Override
    public Dimension getPreferredSize() {
        return new Dimension(1100, 600);
    }
	
	/**
	 * initialize: initializing the panel
	 */
	private void initialize(){
		this.setLayout(new GridBagLayout());
		this.setBounds(100, 100, 450, 300);
		JButton add = new JButton("Add");
		add.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				int selectedRow = menuTable.getSelectedRow();
				if(selectedRow != -1){
					TableModel model = menuTable.getModel();
					Object[] data = new Object[3];
					data[0] = model.getValueAt(selectedRow, 0);
					data[1] = model.getValueAt(selectedRow, 2);
					data[data.length - 1] = 1;
					if(data[1] == null){
						data[1] = "SMALL";
					}
					DefaultTableModel carModel = (DefaultTableModel) pastOrderTable.getModel();
					if(!isInTable(carModel, data)){
						carModel.addRow(data);
						addedItems.put(data[1] +" "+ data[0], Integer.parseInt(data[2].toString()));
					}
				}
			}
			
		});
		JButton remove = new JButton("Remove");
		remove.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				int selectedRow = pastOrderTable.getSelectedRow();
				if(selectedRow != -1){
					DefaultTableModel carModel = (DefaultTableModel) pastOrderTable.getModel();
					String item = carModel.getValueAt(selectedRow, 1) + " " + carModel.getValueAt(selectedRow, 0);
					carModel.removeRow(selectedRow);
					if(orderItems.containsKey(item)){
						removedItems.add(item);
						orderItems.remove(item);
					}
				}
			}
			
		});
		this.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		JPanel AddRemovePanel = new JPanel(new GridBagLayout());
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		AddRemovePanel.add(add,gbc);
		gbc.gridy = 1;
		AddRemovePanel.add(remove, gbc);
		gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weightx = 1;
		gbc.weighty = 1;
		gbc.gridx = 0;
		gbc.gridy = 0;
		MenuModel table = populateMenuTable();
		menuTable = new JTable(table);
		TableColumn itemColumn = menuTable.getColumnModel().getColumn(2);
		itemSizeComboBox = new JComboBox<String>();
		itemSizeComboBox.addItem("SMALL");
		itemSizeComboBox.addItem("MEDIUM");
		itemSizeComboBox.addItem("LARGE");
		itemSizeComboBox.setSelectedItem(0);
		itemSizeComboBox.setEditable(false);
		itemColumn.setCellEditor(new DefaultCellEditor(itemSizeComboBox));
		JScrollPane sp = new JScrollPane(menuTable);
		this.add(sp, gbc);
		gbc.gridx++;
		gbc.weightx = 0.3;
		gbc.weighty = 0.3;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		this.add(AddRemovePanel, gbc);
		gbc.gridx++;
		gbc.weightx = 1;
		gbc.weighty = 1;
		gbc.fill = GridBagConstraints.BOTH;
		MyModel model = populateOrderItemsTable();
		pastOrderTable = new JTable(model);
		sp = new JScrollPane(pastOrderTable);
		this.add(sp, gbc);
	}
	
	private boolean isInTable(DefaultTableModel model ,Object[] item){
		for(int i = 0; i < model.getRowCount(); i++){
			String name = model.getValueAt(i, 0).toString();
			String size = model.getValueAt(i, 1).toString();
			if(item[0].toString().equals(name) && item[1].toString().equals(size)){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * populatePastOrderTable: populating the past order info table.
	 * @throws SQLException 
	 */
	private MyModel populateOrderItemsTable(){
		String[] columns = { "Name","Size", "Quantity" };
		ArrayList<Object[]> items = new ArrayList<Object[]>();
		try {
			orderItems = OrderManager.getOrderItems(orderid);
			Iterator<Entry<String, Integer>> it = orderItems.entrySet().iterator();
			while(it.hasNext()){
				Object[] data = new Object[3];
				Entry<String, Integer> entry = it.next();
				String size = entry.getKey().split(" ")[0];
				String name = entry.getKey().substring(size.length() + 1);
				data[0] = name;	//name
				data[1] = size;	//size
				data[2] = entry.getValue();	//quantity
				items.add(data);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		Object[][] data = new Object[items.size()][3];
		for(int i = 0; i < items.size(); i++){
			data[i] = items.get(i);
		}
		return new MyModel(data, columns, 2);
	}
	
	/**
	 * populateMenuTable: populating the initial menu items.
	 * @return
	 */
	private MenuModel populateMenuTable(){
		
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
		return new MenuModel(data, columns, 2);
	}
	
	public List<String> itemsRemoved(){
		return removedItems;
	}
	
	public Map<String, Integer> itemsAdded(){
		return addedItems;
	}
	
	public Map<String, Integer> itemsModified(){
		return orderItems;
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
				if(column == 2){
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

	public class MenuModel extends DefaultTableModel{

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private int editableCol;

		MenuModel(Object[][] data, Object[] columns, int editableColumn){
			super(data,columns);
			this.editableCol = editableColumn;
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
