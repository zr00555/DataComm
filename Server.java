import java.io.*;
import java.net.*;
import java.util.*;

//Authors: Zachary Reese, Gregory Disser, Jeffrey Neal

@SuppressWarnings({"rawtypes", "resource", "unchecked"})
   
//Server-side program that hosts the chatroom
class Server implements Runnable {
    Socket connectionSocket;  
    public static int numClients = 0;
    public static ArrayList clients = new ArrayList();
    
     
    //Main method that creates that server socket at port 1234 and configures the server to listen for multiple clients by assigning each a thread.
    public static void main(String[] args) throws Exception {
    	String IPAddress = new String(InetAddress.getLocalHost().getHostAddress()); //Get local LAN IP
		System.out.println("Connected to " + IPAddress);
        InetAddress addr = InetAddress.getByName(IPAddress); //PUT SAME SERVER IN CHAT.JAVA, initialize IP address
        ServerSocket mysocket = new ServerSocket(1234, 10, addr); //Initialize Server Socket with port 1234, 10 possible connection, and using the declared IP address
        System.out.println("Chat Server operational");
        while(true) {
            Socket sock = mysocket.accept(); //Opens socket for biderectional communication
            Server server = new Server(sock); //Create new instance of socket for each thread to talk to server
            Thread serverThread = new Thread(server); //Create a new thread for each client
            serverThread.start(); //executes thread and the class's run method
        }
    }
     
    //Constructor for server-client connection
    //When a new client connects to server, increment number of clients and assign them a socket
    public Server(Socket s) {
        try {
            numClients++;
            System.out.println("Client " + numClients + " connected.");
            connectionSocket = s; //Assign new connection a socket
        } catch(Exception e) {
            e.printStackTrace();
        }
    }     
     
    public void run() {
        try {
        	//Creates the reader/writer objects that communicate to the client
            BufferedReader reader = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream())); //Reader that reads incoming messages from clients
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(connectionSocket.getOutputStream())); //Writer that broadcasts message from reader to all clients
            clients.add(writer); 
            while(true) {
                String broadcast = reader.readLine().trim(); //Gets message from reader queue
                System.out.println("Client logs - " + broadcast);      
                 
                 //Iterates through the arraylist that holds all the clients
                 //Reads the stored writer objects and broadcasts the message to all clients
                for (int i = 0; i < clients.size(); i++) {
                    try {
                        BufferedWriter bw = (BufferedWriter)clients.get(i); //Writes to current client in the loop
                        bw.write(broadcast); //Broadcasts message to all clients
                        bw.write("\r\n");
                        bw.flush();
                    } catch(Exception e) {
                    	//If server broadcasts to a disconnected client, remove disconnected client from client arraylist
                    	System.out.println("Client removed from arraylist");
                        clients.remove(i);
                    }
                }
                
            }
        } catch(Exception e) {
            numClients--; //If a client disconnects, decrement number of clients
            
            //If all users have disconnected from server, terminate server
            if(numClients == 0) {
            	System.out.println("All users diconnected, shutting down server");
            	System.exit(0);
            }
        }
    }
}
