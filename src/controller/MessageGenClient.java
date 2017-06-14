package controller;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;

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
	private int width, height;
	
	public MessageGenClient() {
		this.msgGen = new MessageGenerator();
		
		this.width = 500;
		this.height = 300;
		this.setSize(width, height);
		this.setResizable(false);
		this.setTitle("Markov Chain Message Generator");

		// Starts the instance in the middle of the screen; Works for multi-monitor
		GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		this.setLocation((int) (gd.getDisplayMode().getWidth() / 3), (int) (gd.getDisplayMode().getHeight() / 3));
		
		this.add(new TwitterView(msgGen, this, width, height));
		//this.add(new GraphicalView(msgGen, this, width, height));
		this.addWindowListener(new windowListener());
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
