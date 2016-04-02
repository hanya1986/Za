package edu.rit.cs.Za;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

public class PastOrderView {
	private JFrame frame;
	private JTable pastOrderTable;
	public void runGUI(){
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					initialize();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	private void initialize(){
		frame = new JFrame();
		frame.getContentPane().setLayout(new BorderLayout());
		frame.setBounds(100, 100, 450, 300);
		frame.setSize(new Dimension(600,300));
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.getContentPane().setLayout(new BorderLayout());
		pastOrderTable = populatePastOrderTable();
		JScrollPane sp = new JScrollPane(pastOrderTable);
		frame.getContentPane().add(sp, BorderLayout.CENTER);
		JPanel bottomPanel = new JPanel();
		JButton selectButton = new JButton("Select");
		bottomPanel.add(selectButton);
		frame.getContentPane().add(bottomPanel, BorderLayout.SOUTH);
	}
	
	private JTable populatePastOrderTable(){
		String[] columns = { "Name", "Type", "Price", "Estimate Time", "Quantity" };
		Object[][] data = {};
		return new JTable(data, columns);
	}
}
