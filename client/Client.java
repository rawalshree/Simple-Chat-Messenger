package client;

import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.ImageProducer;
import javax.swing.*;


public class Client extends JFrame {

	private JTextField userText;
	private JTextArea chatWindow;
	private ObjectOutputStream output;
	private ObjectInputStream input;
	private String serverIP;
	private Socket connection;
	
	public Client(String host) {
		
		super("CLIENT");
		
		serverIP = host;		
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
		add(new JScrollPane(chatWindow), BorderLayout.CENTER);
		setSize(1280, 720);
		setVisible(true);
	}

	public void startRunning() {
		
		try {
			Connection();
			Setup();
			Chatting();
		}
		catch(EOFException e) {
			showMessage("\nConnection Ended by the Client!! ");
		}
		catch(IOException e) {
			e.printStackTrace();
		}
		finally {
			closeAll();
		}				
	}

	private void Connection() throws IOException {
		
		showMessage("Connecting......\n");
		connection = new Socket(InetAddress.getByName(serverIP), 9090);
		showMessage("Connected to " + connection.getInetAddress().getHostAddress() + ".\n");
	}

	private void Setup() throws IOException {
		
		output = new ObjectOutputStream(connection.getOutputStream());
		output.flush();
		input = new ObjectInputStream(connection.getInputStream());
		showMessage("Setup is Done!!!\n");;
		
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
		while(!message.equals("SERVER - END"));
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
			output.writeObject("CLIENT - " + message);
			output.flush();
			showMessage("CLIENT - " + message + "\n");
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
