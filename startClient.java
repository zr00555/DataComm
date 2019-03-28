import javax.swing.*;
/*
 * Creates an instance of the client that connects to the chatroom.
 * Prompts user for a screen name and will pop up with the gui after
 */
public class startClient {
 
    public static void main(String [] args) {
        JFrame frame = new JFrame(""); 
        String input = JOptionPane.showInputDialog(frame, "Input Screen name");
         
        try { //Creates client with its thread and executes it, also displays gui
            System.out.println("Welcome!");                    
            Chat c = new Chat(input);   
            Thread t1 = new Thread(c);
            t1.start();
        } catch(Exception e) { //catch exceptions
            e.printStackTrace();
            System.out.println("Issues initializing client!");
            System.exit(0);
        }
    }
}