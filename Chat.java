import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

@SuppressWarnings("resource")

//Client-side program that connects to the chatroom
public class Chat implements Runnable {

	public JTextField tField;
	public JTextArea tArea;
	public String login = "placeholder";
	BufferedWriter writer;
	BufferedReader reader;

	/*
	 * Bulk of the client-side program that creates a use interface and connects the
	 * client to the server at 127.0.0.1:1234
	 */
	public Chat(String str) {
		// GUI
		login = str;
		JFrame f = new JFrame("Chatroom");
		f.setSize(500, 500);
		JPanel p1 = new JPanel();
		p1.setLayout(new BorderLayout());
		JPanel p2 = new JPanel();
		p2.setLayout(new BorderLayout());
		tField = new JTextField();
		p1.add(tField, BorderLayout.CENTER);
		JButton b1 = new JButton("Send");
		JButton b2 = new JButton("Disconnect");
		p1.add(b1, BorderLayout.EAST);
		p1.add(b2, BorderLayout.SOUTH);
		tArea = new JTextArea();
		p2.add(tArea, BorderLayout.CENTER);
		p2.add(p1, BorderLayout.SOUTH);
		f.setContentPane(p2);

		// Configures the writer and reader object that communicates with the server
		try {
			String IPAddress = new String(InetAddress.getLocalHost().getHostAddress());
			System.out.println(IPAddress);
			Socket socketClient = new Socket(IPAddress, 1234); // Enter custom IP for connecting to a different host
			writer = new BufferedWriter(new OutputStreamWriter(socketClient.getOutputStream()));
			reader = new BufferedReader(new InputStreamReader(socketClient.getInputStream()));
			writer.write("(" + login + ")" + " has connected.");
			writer.write("\r\n");
			writer.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}

		// Action listener for the send button, sends username + message to server
		b1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				String message = tField.getText();
				String str = login + ": " + message;
				if (message.length() != 0) { // Can't send a blank message
					tField.setText("");
					try {
						writer.write(str);
						writer.write("\r\n");
						writer.flush();
					} catch (Exception e) {
						System.out.println("Cannot send messages without a running chatserver!");
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
					String message = tField.getText();
					String str = login + ": " + message;
					if (message.length() != 0) {
						tField.setText("");
						try {
							writer.write(str);
							writer.write("\r\n");
							writer.flush();
						} catch (Exception ex) {
							System.out.println("Cannot send messages without a running chatserver!");
							System.exit(0);
						}
					}
				}

			}

			@Override
			public void keyReleased(KeyEvent arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void keyTyped(KeyEvent arg0) {
				// TODO Auto-generated method stub

			}

		});

		// End button event
		b2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev2) {
				try {
					writer.write("(" + login + ")" + " has disconnected.");
					writer.write("\r\n");
					writer.flush();
					System.exit(0);
				} catch (IOException e) {
					// TODO Auto-generated catch block
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
			while ((serverMsg = reader.readLine()) != null) {
				System.out.println("Chatserver log: " + serverMsg);
				tArea.append(serverMsg + "\n");
			}
		} catch (Exception e) {
			System.out.println("Chat Server not running!");
		}
	}
}
