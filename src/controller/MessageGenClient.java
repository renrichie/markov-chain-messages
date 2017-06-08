package controller;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;

import model.MessageGenerator;
import view.GraphicalView;

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

		// Starts the instance in the middle of the screen; Works for multi-monitor
		GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		this.setLocation((int) (gd.getDisplayMode().getWidth() / 3), (int) (gd.getDisplayMode().getHeight() / 3));
		
		this.add(new GraphicalView(msgGen, this, width, height));
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
			// Example code for making data persistent
			// TODO: Still need to implement Serializable on relevant classes
//			int i = JOptionPane.showConfirmDialog(null, "Save data?");
//			FileOutputStream bytesToDisk = null;
//			if (i == 0) {
//
//				try {
//					bytesToDisk = new FileOutputStream("Jukebox.ser");
//					ObjectOutputStream outFile = new ObjectOutputStream(bytesToDisk);
//					outFile.writeObject(jukebox);
//					outFile.close();
//				} catch (FileNotFoundException fnfe) {
//					fnfe.printStackTrace();
//				} catch (IOException ioe) {
//					ioe.printStackTrace();
//				}
//				setDefaultCloseOperation(EXIT_ON_CLOSE);
//			} else if (i == 1) {
//				setDefaultCloseOperation(DISPOSE_ON_CLOSE);
//				System.exit(0);
//			} else {
//				setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
//			}
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
