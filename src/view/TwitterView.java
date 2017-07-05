package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import controller.MessageGenClient;
import model.MessageGenerator;
import twitter4j.Paging;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

/**
 * This is a GUI for the final version of the program, which will allow the user to designate a user profile
 * to pull tweets from to use as input for the markov chaining, and to generate a message based on that 
 * input. The intent is to resemble a Twitter status, but that may be difficult to achieve.
 * @author Richie Ren
 *
 */
public class TwitterView extends JPanel {

	private MessageGenerator msgGen;
	private MessageGenClient msgGenClient;
	private JPanel buttonHolder, usernameHolder;
	private JButton genText, analyze, howToUse;
	private JTextArea result; 
	private JTextField username;
	private boolean analyzingInput;
	private final String instructions = "Type in the username of a Twitter profile to use for input.\n"
			+ "Press the 'Analyze' button to allow the program to start analyzing the user's tweets.\n"
			+ "Wait until the program finishes analyzing (the 'Generate' button will enable itself).\n"
			+ "Press the 'Generate' button in order to randomly generate messages based on the provided input.\n";

	public TwitterView(MessageGenerator msgGenIn, MessageGenClient frame, int width, int height) {
		msgGen = msgGenIn;
		msgGenClient = frame;

		result = new JTextArea();
		result.setLineWrap(true);
		result.setWrapStyleWord(true);
		result.setEditable(false);
		result.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		result.setSize(width - (width / 10), height - (height / 10));

		this.setBackground(Color.DARK_GRAY);
		this.setSize(width, height);

		genText = new JButton("Generate Text");
		genText.setEnabled(false);

		analyze = new JButton("Analyze");
		analyzingInput = false;

		howToUse = new JButton("How To Use");

		buttonHolder = new JPanel();
		buttonHolder.add(genText);
		buttonHolder.add(howToUse);
		buttonHolder.add(analyze);

		username = new JTextField("15CharsUsername");
		username.setPreferredSize(this.username.getPreferredSize());
		username.setBorder(BorderFactory.createLineBorder(Color.BLACK));

		usernameHolder = new JPanel();
		usernameHolder.setBackground(Color.WHITE);
		usernameHolder.add(new JLabel("https://twitter.com/"));
		usernameHolder.add(username);

		this.setLayout(new BorderLayout());

		this.add(usernameHolder, BorderLayout.NORTH);
		this.add(result, BorderLayout.CENTER);
		this.add(buttonHolder, BorderLayout.SOUTH);
		
		setupListeners();
	}
	
	/**
	 * Used to reset the views to their default state upon switching.
	 */
	public void reset() {
		genText.setEnabled(false);
		result.setText("");
		username.setText("15CharsUsername");
	}
	
	/**
	 * Used to prevent changing views while the program is still analyzing data.
	 * @return a boolean indicating if the program is currently analyzing
	 */
	public boolean isAnalyzing() {
		return this.analyzingInput;
	}
	
	/**
	 * Sets up the action listeners for the buttons.
	 */
	private void setupListeners() {
		genText.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (msgGen.isAnalyzing()) {
					JOptionPane.showMessageDialog(msgGenClient, "The program is currently analyzing the input!", "In Progress", JOptionPane.ERROR_MESSAGE);
					return;
				}
				
				String text = msgGen.generateText();
				result.setText(text);
				System.out.println(text);
			}
		});

		howToUse.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(msgGenClient, instructions, "How To Use", JOptionPane.INFORMATION_MESSAGE);
			}
		});

		analyze.addActionListener(new TwitterListener());
	}
	
	/**
	 * An action listener for the analyze button that runs in a separate thread so that the GUI is still responsive.
	 * @author Richie Ren
	 *
	 */
	private class TwitterListener implements ActionListener {
		private String oldUser = "";
		
		/**
		 * Reads in the Twitter timeline on a separate thread.
		 */
		private void startThread() {
			String user = username.getText();
			
			genText.setEnabled(false);
			result.setText("");
			
			int errorCode = msgGen.readFromTwitter(user);	
			analyzingInput = msgGen.isAnalyzing();
			
			if (errorCode == -1) {
				JOptionPane.showMessageDialog(msgGenClient, "An error occurred when attempting to parse the user's profile!", "Error", JOptionPane.ERROR_MESSAGE);
				genText.setEnabled(false);
				return;
			}
			
			analyzingInput = msgGen.isAnalyzing();
			genText.setEnabled(!analyzingInput);
			oldUser = user;
		}
		
		/**
		 * Checks to see if the given username is between 0 and 16 characters long.
		 * @param user - the username to be checked
		 * @return a boolean indicating if the username is of valid length
		 */
		private boolean usernameLengthCheck(String user) {
			if (user.length() <= 0 || user.length() > 15) {
				return false;
			}
			
			return true;
		}
		
		/**
		 * Handles what action to perform when the analyze button is pressed.
		 */
		@Override
		public void actionPerformed(ActionEvent arg0) {
			new Thread() {
				public void run() {
					// Prevents multiple Threads from analyzing the input
					if (msgGen.isAnalyzing()) {
						JOptionPane.showMessageDialog(msgGenClient, "The program is currently analyzing the input!", "In Progress", JOptionPane.ERROR_MESSAGE);
						return;
					}
					else if (!usernameLengthCheck(username.getText())) {
						JOptionPane.showMessageDialog(msgGenClient, "The username is of invalid length!", "Error", JOptionPane.ERROR_MESSAGE);
						return;
					}
					else if (oldUser.equalsIgnoreCase(username.getText())) {
						return;
					}
					startThread();
				}
			}.start();
		}
	}
}