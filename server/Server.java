package server;

import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.ImageProducer;
import javax.swing.*;


public class Server extends JFrame {
	
	private JTextField userText;
	private JTextArea chatWindow;
	private ObjectOutputStream output;
	private ObjectInputStream input;
	private ServerSocket server;
	private Socket connection;
	
	public Server() {
		
		super("SERVER");
		userText = new JTextField();
		userText.setEditable(false);
		
		userText.addActionListener(
									new ActionListener() {
								
										@Override
										public void actionPerformed(ActionEvent e) {
											
											sendMessage(e.getActionCommand());
											userText.setText("");
											
										}
									});
		
		add(userText, BorderLayout.SOUTH);
		chatWindow = new JTextArea();
		chatWindow.setEditable(false);
		add(new JScrollPane(chatWindow));
		setSize(1280, 720);
		setVisible(true);
		
	}
	
	public void startRunning() {
		
		try {
			server = new ServerSocket(9090, 20);
			while(true) {
				try {
					Connection();
					Setup();
					Chatting();
				}
				catch(EOFException e) {
					showMessage("\nConnection Ended by the Server!! ");
				}
				finally {
					closeAll();
				}				
			}	
	    }
		catch(IOException e) {
			e.printStackTrace();
		}
	}

	private void Connection() throws IOException {
		
		showMessage("Waiting For a Connection......\n");
		connection = server.accept();
		showMessage(connection.getInetAddress().getHostAddress() + " connected.\n");
	}
	
	private void Setup() throws IOException {
		
		output = new ObjectOutputStream(connection.getOutputStream());
		output.flush();
		input = new ObjectInputStream(connection.getInputStream());
		showMessage("Setup is Done!!!\n");
		
	}
	
	private void Chatting() throws IOException {
		
		String message = "Connected";
		sendMessage(message);
		sendMessage("=====================================");
		canType(true);
		
		do {
			try {
				message = (String) input.readObject();
				showMessage(message + "\n");
			}
			catch(ClassNotFoundException e) {
				showMessage("Command not recognized.\n");
			}
		}
		while(!message.equals("CLIENT - END"));
	}
	
	private void closeAll() {
		
		showMessage("Closing Connection.\n");
		canType(false);
		
		try {
			output.close();
			input.close();
			connection.close();
		}
		catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	private void sendMessage(String message) {
		
		try {
			output.writeObject("SERVER - " + message);
			output.flush();
			showMessage("SERVER - " + message + "\n");
		}
		catch(IOException e) {
			chatWindow.append("ERROR - Message cannpt be send.");
		}
	}
	
	private void showMessage(final String text) {
		
		SwingUtilities.invokeLater(
									new Runnable()
									{
										public void run()
										{
											chatWindow.append(text);
										}
									});
	}

	private void canType(final boolean tof) {
		
		SwingUtilities.invokeLater(
									new Runnable() 
									{
										@Override
										public void run() 
										{
											userText.setEditable(tof);						
										}
									});
	}

	
}
