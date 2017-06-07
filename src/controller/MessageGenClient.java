package controller;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import model.MessageGenerator;
import view.GraphicalView;

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
		
		this.add(new GraphicalView(msgGen, this, width, height));
		this.addWindowListener(new windowListener());
	}
	
	private class windowListener extends WindowAdapter implements WindowListener {

		@Override
		public void windowActivated(WindowEvent e) {

		}

		@Override
		public void windowClosed(WindowEvent e) {
			// TODO Auto-generated method stub

		}

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
			// TODO Auto-generated method stub
		}

		@Override
		public void windowDeiconified(WindowEvent e) {
			// TODO Auto-generated method stub
		}

		@Override
		public void windowIconified(WindowEvent e) {
			// TODO Auto-generated method stub
		}

		@Override
		public void windowOpened(WindowEvent e) {
			// TODO Auto-generated method stub
		}

	}
}
