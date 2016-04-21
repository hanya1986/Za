package edu.rit.cs.Za;

/**
 * SignupView.java
 * Contributor(s):  Yihao Cheng (yc7816@rit.edu)
 */

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.BoxLayout;
import java.awt.FlowLayout;
import javax.swing.JSplitPane;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;

import javax.swing.JLabel;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.ImageIcon;
import javax.swing.JButton;

public class LoginView implements ActionListener{

	private JFrame frame;
	private JPanel panel;
	private JPanel panel_1;
	private JPanel panel_2;
	private JPanel panel_3;
	private JPanel panel_4;
	private JButton loginButton;
	private JButton signupButton;
	

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					LoginView window = new LoginView();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public LoginView() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setMinimumSize(new Dimension(700, 500));
		frame.setMaximumSize(new Dimension(700,500));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new GridLayout(0, 2, 0, 0));
		
		panel = new JPanel();
		ImageIcon image = new ImageIcon("ZA.png");
		ImageIcon logo = new ImageIcon(image.getImage().getScaledInstance(350, 470, Image.SCALE_SMOOTH));
		JLabel logoLabel = new JLabel(logo);
		panel.add(logoLabel);
		image = new ImageIcon("pizza.jpg");
		logo = new ImageIcon(image.getImage().getScaledInstance(350, 200, Image.SCALE_SMOOTH));
		JLabel pizzaLabel = new JLabel(logo);
		frame.getContentPane().add(panel);
		panel_1 = new JPanel();
		frame.getContentPane().add(panel_1);
		panel_1.setLayout(new GridLayout(3, 1, 0, 0));
		panel_3 = new JPanel();
		panel_3.add(pizzaLabel);
		panel_1.add(panel_3);
		panel_2 = new JPanel();
		panel_1.add(panel_2);
		panel_4 = new JPanel();
		image = new ImageIcon("delivery.jpg");
		logo = new ImageIcon(image.getImage().getScaledInstance(350, 165, Image.SCALE_SMOOTH));
		JLabel deliveryLabel = new JLabel(logo);
		panel_4.add(deliveryLabel);
		panel_4.setBackground(Color.WHITE);
		panel_1.add(panel_4);
		panel_2.setLayout(new GridBagLayout());
		panel_2.setBorder(new EmptyBorder(10,10,10,10));
		panel_2.setBackground(Color.WHITE);
		GridBagConstraints cnst = new GridBagConstraints();
		cnst.insets = new Insets(4,2,4,2);
		JLabel usernameLabel = new JLabel("Username:");
		JLabel passwordLabel = new JLabel("Password:");
		loginButton = new JButton("Login");
		signupButton = new JButton("Sign up");
		signupButton.addActionListener(this);
		JTextField usernameText = new JTextField();
		JTextField passwordText = new JTextField();
		Dimension prefSize = new Dimension(140,20);
		usernameText.setPreferredSize(prefSize);
		passwordText.setPreferredSize(prefSize);
		cnst.gridx = 1;
		cnst.gridy = 0;
		panel_2.add(usernameLabel, cnst);
		cnst.gridy = 1;
		panel_2.add(passwordLabel, cnst);
		cnst.gridx = 2;
		cnst.gridy = 0;
		panel_2.add(usernameText, cnst);
		cnst.gridy = 1;
		panel_2.add(passwordText, cnst);
		cnst.gridx = 1;
		cnst.gridy = 2;
		panel_2.add(loginButton, cnst);
		cnst.gridx = 2;
		panel_2.add(signupButton, cnst);
		JButton forgotPass = new JButton("forgot password?");
		forgotPass.setFocusPainted(true);
		forgotPass.setFocusable(true);
		forgotPass.setForeground(Color.BLUE);
		forgotPass.setBackground(Color.white);
		cnst.gridx = 2;
		cnst.gridy = 3;
		panel_2.add(forgotPass, cnst);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();
		if(cmd.equals("Sign up")){
			SignupView signup = new SignupView();
			signup.run();
		}
	}

}


