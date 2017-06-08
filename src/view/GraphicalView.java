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
import javax.swing.JPanel;
import javax.swing.JTextArea;

import controller.MessageGenClient;
import model.MessageGenerator;

/**
 * This is a rough graphical view for the message generator that is meant to demonstrate proof of 
 * concept for the program's use on an input text file.
 * @author Richie
 *
 */
public class GraphicalView extends JPanel {

	private MessageGenerator msgGen;
	private MessageGenClient msgGenClient;
	private JPanel buttonHolder;
	private JButton genText, browse;
	private JTextArea result;
	private FileDialog fd;
	private String fileName;
	
	public GraphicalView(MessageGenerator msgGenIn, MessageGenClient frame, int width, int height) {
		this.msgGen = msgGenIn;
		this.msgGenClient = frame;
		
		this.result = new JTextArea();
		this.result.setLineWrap(true);
		this.result.setWrapStyleWord(true);
		this.result.setEditable(false);
		this.result.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		this.result.setSize(width - (width / 10), height - (height / 10));
		
		this.setBackground(Color.DARK_GRAY);
		this.setSize(width, height);
		this.fileName = null;
		
		this.genText = new JButton("Generate text");
		this.genText.setEnabled(false);
		
		this.browse = new JButton("Browse...");
		
		this.fd = new FileDialog(msgGenClient, "Choose a file", FileDialog.LOAD);
		this.fd.setDirectory("C:\\");
		this.fd.setFile("*.txt");
		
		this.buttonHolder = new JPanel();
		this.buttonHolder.add(genText);
		this.buttonHolder.add(browse);
		
		this.setLayout(new BorderLayout());
		
		browse.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				fd.setVisible(true);
				fileName = fd.getDirectory() + fd.getFile();
				
				// Clear all previous input and read in the new input
				if (fileName != null) {
					msgGen.clearInput();
					
					String text = "";
					boolean analyzingInput = true;
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
		});
		
		genText.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String text = msgGen.generateText();
				result.setText(text);
				System.out.println(text);
			}
		});
		
		this.add(result, BorderLayout.CENTER);
		this.add(buttonHolder, BorderLayout.SOUTH);
	}
}
