import javax.swing.*;

//Authors: Zachary Reese, Gregory Disser, Jeffrey Neal

 //Creates an instance of the client that connects to the chatroom.
 //Prompts user for a screen name and will pop up with the gui after
public class startClient {

	public static void main(String[] args) {
		//Dialog box for user to input desired screen name
		JFrame frame = new JFrame("");
		String screenName = JOptionPane.showInputDialog(frame, "Input Screen name");

		//Creates new chat client thread
		if (screenName != null && screenName.length() > 0) { //Catch empty usernames
			try { 
				System.out.println("Welcome!");
				Chat c = new Chat(screenName); //Create new chat instance with given screen name
				Thread t1 = new Thread(c); //Creates new thread for user, allows for multiple chat windows on same machine
				t1.start(); //Execute thread
				
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("Issues initializing client!");
				System.exit(0);
			}
		} else {
			System.exit(0);
		}
	}
}