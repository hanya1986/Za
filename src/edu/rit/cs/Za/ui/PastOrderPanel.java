package edu.rit.cs.Za.ui;

/**
 * SignupView.java
 * Contributor(s):  Yihao Cheng (yc7816@rit.edu)
 */

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableModel;

import edu.rit.cs.Za.MenuManager;
import edu.rit.cs.Za.OrderManager;

public class PastOrderPanel extends JPanel{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String[] itemAttr = {"type", "est_prep_time", "small_price", "medium_price", "large_price"};
	private String[] orderAttr = {"orderid","custid","order_type","time_order_placed","total", "pay_method"};
	private JTable pastOrderTable;
	private long custId;
	
	public PastOrderPanel(long custId){
		this.custId = custId;
		pastOrderTable = populatePastOrderTable();
		initialize();
	}
	
	public PastOrderPanel(){
		pastOrderTable = populateEmpOrdersTable();
		initialize();
	}
	
	@Override
    public Dimension getPreferredSize() {
        return new Dimension(600, 500);
    }
	
	/**
	 * initialize: initializing the panel
	 */
	private void initialize(){
		this.setLayout(new BorderLayout());
		this.setBounds(100, 100, 450, 300);
		JScrollPane sp = new JScrollPane(pastOrderTable);
		this.add(sp, BorderLayout.CENTER);
	}
	
	/**
	 * populatePastOrderTable: populating the past order info table.
	 * @throws SQLException 
	 */
	private JTable populatePastOrderTable(){
		String[] columns = { "OrderID", "Order Type", "Price", "Date", "Quantity" };
		ArrayList<Object[]> items = new ArrayList<Object[]>();
		try {
			items = OrderManager.getCustomerOrders(custId);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Object[][] data = new Object[items.size()][5];
		for(int i = 0; i < items.size(); i++){
			data[i] = items.get(i);
		}
		return new JTable(data, columns);
	}
	
	/**
	 * populateEmpOrdersTable: populating all past orders info table.
	 * @throws SQLException 
	 */
	private JTable populateEmpOrdersTable(){
		String[] columns = { "Order ID", "Customer ID", "Order Type", "Order Placed", "Total", "Payment Method" };
		ArrayList<Object[]> items = new ArrayList<Object[]>();
		try {
			items = OrderManager.getAllOrders(orderAttr);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Object[][] data = new Object[items.size()][5];
		for(int i = 0; i < items.size(); i++){
			data[i] = items.get(i);
		}
		return new JTable(data, columns);
	}
	
	public ArrayList<Object[]> getSelectedOrder(){
		int selectedRow = pastOrderTable.getSelectedRow();
		Object[] data = null;
		ArrayList<Object[]> itemList = new ArrayList<Object[]>();
		if(selectedRow != -1){
			TableModel model = pastOrderTable.getModel();
			long orderid = Long.parseLong(model.getValueAt(selectedRow, 0).toString());
			ArrayList<String> attr = new ArrayList<String>(Arrays.asList(itemAttr));
			try {
				Map<String,Integer> orderItems = OrderManager.getOrderItems(orderid);
				Iterator<Entry<String, Integer>> it = orderItems.entrySet().iterator();
				while(it.hasNext()){
					data = new Object[6];
					Entry<String, Integer> entry = it.next();
					String size = entry.getKey().split(" ")[0];
					String name = entry.getKey().substring(size.length() + 1);
					data[0] = name;	//name
					data[2] = size;	//size
					data[5] = entry.getValue();	//quantity
					Map<String,Object> itemsDetail = MenuManager.getItemInfo(data[0].toString(), attr);
					Iterator<String> itemIt =itemsDetail.keySet().iterator();
					while(itemIt.hasNext()){
						String column = itemIt.next();
						switch(column){
							case "type":
								data[1] = itemsDetail.get(column);
								break;
							case "est_prep_time":
								data[4] = itemsDetail.get(column);
								break;
							case "small_price":
								if(data[2].equals("SMALL")){
									data[3] = itemsDetail.get(column);
								}
								break;
							case "medium_price":
								if(data[2].equals("MEDIUM")){
									data[3] = itemsDetail.get(column);
								}
								break;
							case "large_price":
								if(data[2].equals("LARGE")){
									data[3] = itemsDetail.get(column);
								}
								break;
						}
					}
					itemList.add(data);
				}
				
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return itemList;
	}
}
