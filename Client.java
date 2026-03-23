import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {
    public static void main(String [] args) {
        try {
             System.out.println("Client started.");
             Socket soc = new Socket("localhost", 9806);

             /**
              * Read data from the user and send it to the server.
              */

             System.out.println("What's your name?");

             // The data is read/write through File IO stream. Buffered reader/writer accepts
             // Input/Output Stream reader.
            InputStreamReader in = new InputStreamReader(System.in);
            BufferedReader reader = new BufferedReader(in);

            // Writing it to the socket.
            PrintWriter out = new PrintWriter(soc.getOutputStream(), true);

            String message = reader.readLine();
            out.println(message);


        }catch(Exception e) {
           e.printStackTrace();
        }
    }
}
