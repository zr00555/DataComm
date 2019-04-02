import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.Date;

//Authors: Zachary Reese, Gregory Disser, Jeffrey Neal

@SuppressWarnings("resource")

//Client-side program that connects to the chatroom
public class Chat implements Runnable {

	public JTextField tField;
	public JTextArea tArea;
	public String login = "placeholder";
	
	BufferedWriter writer; //Server writer
	BufferedReader reader; //Server reader
	
	SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss"); //24hr date format for timestamps
	Date date;
	String desktop = new String(System.getProperty("user.home") + "/Desktop"); //Users desktop directory location
	File chatHistory = new File(desktop, "ChatHistory.txt"); //Create new file on desktop
	BufferedWriter out; //Local text file writer
	

	public Chat(String str) {
		// GUI using Swing
		
		//Create frame
		login = str;
		JFrame f = new JFrame("Chatroom");
		f.setSize(500, 500);
		
		//Set individual panels
		JPanel p1 = new JPanel();
		p1.setLayout(new BorderLayout());
		JPanel p2 = new JPanel();
		p2.setLayout(new BorderLayout());
		tField = new JTextField();
		p1.add(tField, BorderLayout.CENTER);
		
		//Add buttons
		JButton b1 = new JButton("Send");
		JButton b2 = new JButton("Disconnect");
		
		//Arrange panels and buttons
		p1.add(b1, BorderLayout.EAST);
		p1.add(b2, BorderLayout.SOUTH);
		
		//Add message and history chat boxes
		tArea = new JTextArea();
		p2.add(tArea, BorderLayout.CENTER);
		p2.add(p1, BorderLayout.SOUTH);
		f.setContentPane(p2);
		tArea.setEditable(false); //Chat history box not editable
		

		// Configures the writer and reader object that communicates with the server
		try {
			out = new BufferedWriter(new FileWriter(chatHistory, true)); //Initialize writer for chat history txt file
			
			String IPAddress = new String(InetAddress.getLocalHost().getHostAddress()); //Get local machine IP address, change to target IP if not hosted on your machine
			System.out.println("Connected to " + IPAddress);
			Socket socketClient = new Socket(IPAddress, 1234); // Enter custom IP for connecting to a different host, initialized socket to specified IP and port
			
			//Reader & writer for writing data to the server
			writer = new BufferedWriter(new OutputStreamWriter(socketClient.getOutputStream())); //Connect writer to server socket
			reader = new BufferedReader(new InputStreamReader(socketClient.getInputStream())); //Connect reader to server socket
			
			//Writing data to the local ChatHistory.txt (out) and to the server (writer)
			date = new Date();
			writer.write("[" + formatter.format(date) + "] " + "" + login + "" + " has connected."); //Write when a new user has connected to server
			out.write("[" + formatter.format(date) + "] " + "" + login + "" + " has connected." + "\n"); //Write when new user has connected to text file
			writer.write("\r\n");
			writer.flush();
			out.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}

		// Action listener for the send button, sends username + message to server
		b1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				String message = tField.getText(); //Grab message string from textbox
				date = new Date();
				String str = ("[" + formatter.format(date) + "] " +  login + ": " + message); //Formatted message to add timestamp and username
				if (message.length() != 0) { // Can't send a blank message
					tField.setText(""); //Reset textbox
					try {
						//Send message to server and write to text file
						writer.write(str); //Send message to server
						out.write(str + "\n"); //Write message to text file
						writer.write("\r\n");
						writer.flush();
						out.flush();
					} catch (Exception e) {
						System.out.println("Cannot send messages without a running chatserver!"); //Catch if server is not running and exit client
						System.out.println(e.getStackTrace());
						System.exit(0);
					}
				}
			}
		});

		// Add key listener for enter key to send message
		tField.addKeyListener(new KeyListener() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					String message = tField.getText(); //Grab message string from textbox
					date = new Date();
					String str = ("[" + formatter.format(date) + "] " +  login + ": " + message); //Formatted message to add timestamp and username
					if (message.length() != 0) { // Can't send a blank message
						tField.setText(""); //Reset textbox
						try {
							//Send message to server and write to text file
							writer.write(str); //Send message to server
							out.write(str + "\n"); //Write message to text file
							writer.write("\r\n");
							writer.flush();
							out.flush();
						} catch (Exception ex) {
							System.out.println("Cannot send messages without a running chatserver!"); //Catch if server is not running and exit client
							System.out.println(ex.getStackTrace());
							System.exit(0);
						}
					}
				}

			}

			@Override
			public void keyReleased(KeyEvent arg0) {
				
			}

			@Override
			public void keyTyped(KeyEvent arg0) {
				
			}

		});

		// Disconnect button event
		b2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev2) {
				try {
					writer.write("[" + formatter.format(date) + "] " + login + " has disconnected."); //Write to server whenever user disconnects
					out.write("[" + formatter.format(date) + "] " + login + " has disconnected." + "\n"); //Write to text file whenever user disconnects
					writer.write("\r\n");
					writer.flush();
					out.flush();
					System.exit(0);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		
		f.setVisible(true);
	}

	// Chatlog of messages broadcasted by the server
	public void run() {
		try {
			String serverMsg = "";
			while ((serverMsg = reader.readLine()) != null) { //Retrieves message broadcasted by the server using reader
				System.out.println("Chatserver log: " + serverMsg);
				tArea.append(serverMsg + "\n"); //Appends message from server in textbox
			}
		} catch (Exception e) {
			System.out.println("Chat Server not running!");
		}
	}
}
