package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FileDialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import controller.MessageGenClient;
import model.MessageGenerator;

public class GraphicalView extends JPanel {

	private MessageGenerator msgGen;
	private MessageGenClient msgGenClient;
	private JButton genText, browse;
	private JTextArea result;
	private FileDialog fd;
	private String fileName;
	
	public GraphicalView(MessageGenerator msgGenIn, MessageGenClient frame, int width, int height) {
		this.msgGen = msgGenIn;
		this.msgGenClient = frame;
		
		this.result = new JTextArea();
		this.result.setLineWrap(true);
		this.result.setEditable(false);
		
		this.setBackground(Color.WHITE);
		this.setSize(width, height);
		this.fileName = null;
		
		this.genText = new JButton("Generate text");
		this.browse = new JButton("Browse...");
		
		this.fd = new FileDialog(msgGenClient, "Choose a file", FileDialog.LOAD);
		this.fd.setDirectory("C:\\");
		this.fd.setFile("*.txt");
		
		this.setLayout(new BorderLayout());
		
		browse.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				fd.setVisible(true);
				fileName = fd.getDirectory() + fd.getFile();
				
				// Clear all previous input and read in the new input
				if (fileName != null) {
					msgGen.clearInput();
					
					String text = "";
					
					try {
						Scanner io = new Scanner(new File(fileName));

						while (io.hasNextLine()) {
							text += io.nextLine() + " ";
							System.out.println("Reading");
						}

						io.close();
					} catch (FileNotFoundException exception) {
						System.out.println("An exception has appeared: " + fileName);
						fileName = null;
					}
					
					if (fileName != null) {
						String[] input = text.split("\\s+");
						for (int i = 0; i < input.length - 1; i++) {
							msgGen.addInput(input[i], input[i + 1]);
						}
						System.out.println("Input has been read");
						System.out.println(text.length());
					}
				}
			}
		});
		
		genText.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String text = msgGen.generateText();
				result.setText(text);
				System.out.println("Generating...");
				System.out.println(text);
			}
		});
		
		this.add(result, BorderLayout.NORTH);
		this.add(genText, BorderLayout.WEST);
		this.add(browse, BorderLayout.EAST);
	}
}
