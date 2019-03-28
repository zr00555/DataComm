import java.io.*;
import java.net.*;
import java.util.*;
 
@SuppressWarnings({"rawtypes", "resource", "unchecked"})
 
 
   
//Server-side program that hosts the chatroom
class Server implements Runnable {
    Socket connectionSocket;  
    public static int numClients = 0;
    public static Vector clients = new Vector();
     
    /*
     * Main method that creates that server socket at port 1234 and configures
     * the server to listen for multiple clients by assigning each a thread.
     */
    public static void main(String[] args) throws Exception {
        ServerSocket mysocket = new ServerSocket(1234, 2, 141.165);
        System.out.println("Chat Server operational");
        while(true) {
            Socket sock = mysocket.accept();
            Server server = new Server(sock);
            Thread serverThread = new Thread(server);
            serverThread.start(); //executes thread and the class's run method
        }
    }
     
    //Constructor for server-client connection
    public Server(Socket s) {
        try {
            numClients++;
            System.out.println("Client " + numClients + " connected.");
            connectionSocket = s;
        } catch(Exception e) {
            e.printStackTrace();
        }
    }     
     
    public void run() {
        try {//Creates the reader/writer objects that communicate to the client
            BufferedReader reader = new BufferedReader(new InputStreamReader
                    (connectionSocket.getInputStream()));
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter
                    (connectionSocket.getOutputStream()));
            clients.add(writer); 
            while(true) {
                String data1 = reader.readLine().trim();
                System.out.println("Client logs: " + data1);      
                 
                /*
                 * Iterates through the vector  object that holds all the clients
                 * Reads the stored writer objects and broadcasts the message to all
                 * clients
                 */
                for (int i = 0; i < clients.size(); i++) {
                    try {
                        BufferedWriter bw = (BufferedWriter)clients.get(i);
                        bw.write(data1);
                        bw.write("\r\n");
                        bw.flush();
                    } catch(Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch(Exception e) {
            System.out.println("Client " + (numClients) + " has terminated its connection");
            numClients--;
        }
    }
}