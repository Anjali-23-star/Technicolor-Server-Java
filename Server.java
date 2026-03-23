import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.channels.SocketChannel;

/**
 * A Server.
 */
public class Server {
    public static void main(String [] args) {
        /**
         * In low level, we make a socket and then bind. Here, Java already has build in lib
         * so, we instead use them.
         * 
         * We first define an address to which our server is on: IP+TCP.
         * We then bind the socket to this address.
         */
        try{
        ServerSocket sckfd = new ServerSocket(9806);
        System.out.println("Socket reference.");
        Socket nSocket = sckfd.accept();  // Blocking call: Server wait for client connection and create a new socket.
        System.out.println("connection established.");

        // So, the communication between client and server is happening through new socket nSocket.
        InputStreamReader in = new InputStreamReader(nSocket.getInputStream());
        BufferedReader reader = new BufferedReader(in);

        String message = reader.readLine();
        System.out.println(message);

        }catch(Exception e){
            e.printStackTrace();
         }
    }
}