import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {
    public static void main(String[] args) {
        try {
            System.out.println("Client started.");

            Socket clientSocket = new Socket("localhost", 9806);

            // Reader for user input.
            BufferedReader userInputReader = new BufferedReader(new InputStreamReader(System.in));

            // Write to the server.
            PrintWriter servWriter = new PrintWriter(clientSocket.getOutputStream(), true);

            // Response from the server
            BufferedReader serverReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            // Printing the response from the server.
            while (true) {

                String userCommand = userInputReader.readLine();
                servWriter.println(userCommand);

                String serverResponse;
                while ((serverResponse = serverReader.readLine()) != null && !serverResponse.equals("END")) {
                    System.out.println(serverResponse);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
