package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FileDialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import controller.MessageGenClient;
import model.MessageGenerator;

/**
 * This is a rough graphical view for the message generator that is meant to demonstrate proof of 
 * concept for the program's use on an input text file.
 * @author Richie Ren
 *
 */
public class GraphicalView extends JPanel {

	private MessageGenerator msgGen;
	private MessageGenClient msgGenClient;
	private JPanel buttonHolder;
	private JButton genText, browse, howToUse;
	private JTextArea result;
	private FileDialog fd;
	private String fileName;
	private boolean analyzingInput;
	private final String instructions = "Select a .txt containing some input text using the 'Browse' button.\n"
										+ "Press the 'Generate' button in order to randomly generate messages based on the provided input.\n";
	
	public GraphicalView(MessageGenerator msgGenIn, MessageGenClient frame, int width, int height) {
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
		fileName = null;
		
		genText = new JButton("Generate Text");
		genText.setEnabled(false);
		
		browse = new JButton("Browse...");
		analyzingInput = false;
		
		howToUse = new JButton("How To Use");
		
		fd = new FileDialog(msgGenClient, "Choose a file", FileDialog.LOAD);
		fd.setDirectory("C:\\");
		fd.setFile("*.txt");
		
		buttonHolder = new JPanel();
		buttonHolder.add(genText);
		buttonHolder.add(howToUse);
		buttonHolder.add(browse);
		
		this.setLayout(new BorderLayout());
		
		setupListeners();
		
		this.add(result, BorderLayout.CENTER);
		this.add(buttonHolder, BorderLayout.SOUTH);
	}
	
	/**
	 * Used to prevent changing views while the program is still analyzing data.
	 * @return a boolean indicating if the program is currently analyzing
	 */
	public boolean isAnalyzing() {
		return this.analyzingInput;
	}
	
	/**
	 * Used to reset the views to their default state upon switching.
	 */
	public void reset() {
		genText.setEnabled(false);
		result.setText("");
	}
	
	/**
	 * Sets up the action listeners for the buttons.
	 */
	private void setupListeners() {
		browse.addActionListener(new ActionListener() {
			private void startThread() {
				// Prevents multiple Threads from analyzing the input
				if (analyzingInput) {
					JOptionPane.showMessageDialog(msgGenClient, "The program is currently analyzing the input!", "In Progress", JOptionPane.ERROR_MESSAGE);
					return;
				}
				
				// Starts a new thread
				// Anonymous Thread method taken from ELITE at https://stackoverflow.com/questions/30286705/
				new Thread() {
					public void run() {
						fd.setVisible(true);
						fileName = fd.getDirectory() + fd.getFile();

						// Clear all previous input and read in the new input
						if (fileName != null) {
							msgGen.clearInput();

							String text = "";
							analyzingInput = true;
							genText.setEnabled(!analyzingInput);

							try {
								BufferedReader io = new BufferedReader(new InputStreamReader(new FileInputStream(new File(fileName)), Charset.forName("windows-1252")));

								for (String x = io.readLine(); x != null; x = io.readLine())
								{
									text += x + " ";
								}

								io.close();
							} catch (IOException exception) {
								System.out.println("An exception has appeared: " + fileName);
								fileName = null;
							}

							// Add the input to the message generator
							if (fileName != null) {
								String[] input = text.split("\\s+");

								for (int i = 0; i < input.length - 1; i++) {
									msgGen.addInput(input[i], input[i + 1]);
								}

								analyzingInput = false;
								genText.setEnabled(!analyzingInput);
							}
						}
					}
				}.start();
			}
			
			public void actionPerformed(ActionEvent e) {
				startThread();
			}
		});
		
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
	}
}
