package controller;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

import model.MessageGenerator;
import view.GraphicalView;
import view.TwitterView;

/**
 * This is a simple controller for the message generator that will initialize the GUI 
 * and create an instance of the message generator itself.
 * @author Richie Ren
 *
 */
public class MessageGenClient extends JFrame {
	
	public static void main(String[] args) {
		MessageGenClient client = new MessageGenClient();
		client.setVisible(true);
	}
	
	private MessageGenerator msgGen;
	private TwitterView twitterView;
	private GraphicalView graphicView;
	private JPanel currentView;
	private int width, height;
	
	public MessageGenClient() {
		this.msgGen = new MessageGenerator();
		
		this.twitterView = new TwitterView(msgGen, this, width, height);
		this.graphicView = new GraphicalView(msgGen, this, width, height);
		
		this.width = 500;
		this.height = 300;
		this.setSize(width, height);
		this.setResizable(false);
		this.setTitle("Markov Chain Message Generator");

		// Starts the instance in the middle of the screen; Works for multi-monitor
		GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		this.setLocation((int) (gd.getDisplayMode().getWidth() / 3), (int) (gd.getDisplayMode().getHeight() / 3));
		
		this.addWindowListener(new windowListener());
		
		setupMenu();
		setViewTo(twitterView);
	}
	
	/**
	 * Sets up the composite menu that allows for options.
	 */
	private void setupMenu() {
		JMenuItem menu = new JMenu("Options");
		
		//Adds the views to a menu
		JMenuItem input = new JMenu("Input");
		JMenuItem twitter = new JMenuItem("Twitter");
		JMenuItem text = new JMenuItem("Text File");
		input.add(twitter);
		input.add(text);
		menu.add(input);

		// Set the menu bar
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		menuBar.add(menu);

		// Add the same listener to all menu items requiring action
		MenuItemListener menuListener = new MenuItemListener();
		twitter.addActionListener(menuListener);
		text.addActionListener(menuListener);
	}
	
	/**
	 * Changes the current view of the program depending on the menu item selected.
	 * @param newView
	 */
	private void setViewTo(JPanel newView) {
		if (currentView != null) {
			remove(currentView);
		}
		
		currentView = newView;
		add(currentView);
		currentView.repaint();
		validate();
	}
	
	/**
	 * Processes what action to perform when a menu item is selected.
	 * @author Richie Ren
	 *
	 */
	private class MenuItemListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			// Find out the text of the JMenuItem that was just clicked
			String text = ((JMenuItem) e.getSource()).getText();

			msgGen.clearInput();
			System.gc();
			
			if (text.equals("Twitter")) {
				setViewTo(twitterView);
				repaint();
			}	
			else if (text.equals("Text File")) {
				setViewTo(graphicView);
				repaint();
			}
		}
	}
	
	/**
	 * A window listener in order to modify the operations of closing a window.
	 * @author Richie Ren
	 *
	 */
	private class windowListener extends WindowAdapter implements WindowListener {
		
		@Override
		public void windowActivated(WindowEvent e) {
		}

		@Override
		public void windowClosed(WindowEvent e) {
		}

		/**
		 * Handles what operations occur when the window is exited.
		 */
		@Override
		public void windowClosing(WindowEvent e) {
			setDefaultCloseOperation(DISPOSE_ON_CLOSE);
			System.exit(0);
		}

		@Override
		public void windowDeactivated(WindowEvent e) {
		}

		@Override
		public void windowDeiconified(WindowEvent e) {
		}

		@Override
		public void windowIconified(WindowEvent e) {
		}

		@Override
		public void windowOpened(WindowEvent e) {
		}

	}
}
