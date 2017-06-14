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
	private final String instructions = "Type in the username of a Twitter profile to use for input.\n"
			+ "Press the 'Analyze' button to allow the program to start analyzing the user's tweets.\n"
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
	 * Sets up the action listeners for the buttons.
	 */
	private void setupListeners() {
		genText.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
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

		// TODO: Move this to a private class that runs its own Thread
		// 		 Also need a state boolean indicating if it's currently analyzing, so 
		// 		 when a user presses the button again, a popup occurs
		analyze.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				boolean analyzingInput = true;
				genText.setEnabled(!analyzingInput);
				msgGen.clearInput();
				System.gc();
				
				String user = username.getText();

				if (user.length() == 0) {
					JOptionPane.showMessageDialog(msgGenClient, "There needs to be a specified username!", "Error", JOptionPane.ERROR_MESSAGE);
					return;
				}
				else if (user.length() > 15) {
					JOptionPane.showMessageDialog(msgGenClient, "The specified username is too long!", "Error", JOptionPane.ERROR_MESSAGE);
					return;
				}

				// Creates a builder using Twitter auth keys
				ConfigurationBuilder cb = new ConfigurationBuilder();

				try {
					Scanner keyReader = new Scanner(new File("assets/keys"));
					cb.setOAuthConsumerKey(keyReader.nextLine());
					cb.setOAuthConsumerSecret(keyReader.nextLine());
					cb.setOAuthAccessToken(keyReader.nextLine());
					cb.setOAuthAccessTokenSecret(keyReader.nextLine());
					keyReader.close();
				} catch (FileNotFoundException fe) {
					fe.printStackTrace();
					System.exit(-1);
				}
				
				// Pulls the Tweets from the specified user's profile
				Twitter twitter = new TwitterFactory(cb.build()).getInstance();

				int pageno = 1;
				List<Status> statuses = new ArrayList<>();

				while (true) {
					try {
						int size = statuses.size(); 
						Paging page = new Paging(pageno++, 100);
						statuses.addAll(twitter.getUserTimeline(user, page));
						if (statuses.size() == size)
							break;
					}
					catch(TwitterException te) {
						JOptionPane.showMessageDialog(msgGenClient, "An error occurred when attempting to parse the user's profile!", "Error", JOptionPane.ERROR_MESSAGE);
						return;
					}
				}
				
				if (statuses.isEmpty()) {
					JOptionPane.showMessageDialog(msgGenClient, "The user has no statuses!", "Error", JOptionPane.ERROR_MESSAGE);
					return;
				}
				
				String text = "";
				
				for (Status s : statuses) {
					text += s.getText() + " ";
				}
				
				String[] input = text.split("\\s+");
				
				for (int i = 0; i < input.length - 1; i++) {
					msgGen.addInput(input[i], input[i + 1]);
				}
				
				analyzingInput = false;
				genText.setEnabled(!analyzingInput);
			}
		});
	}
}