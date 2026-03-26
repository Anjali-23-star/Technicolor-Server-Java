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

            // Writer to server
            PrintWriter servWriter = new PrintWriter(clientSocket.getOutputStream(), true);

           // Read from the server.
           BufferedReader serverReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        
           // Step 1: LIST
            String userCommand = userInputReader.readLine();
            servWriter.println(userCommand);

            String serverResponse;
            while ((serverResponse = serverReader.readLine()) != null && !serverResponse.equals("END")) {
                System.out.println(serverResponse);
            }

            // Step 2: OPEN FILE
            String fileRequest = userInputReader.readLine();
            servWriter.println(fileRequest);

            String fileContent;
            while ((fileContent = serverReader.readLine()) != null && !fileContent.equals("END")) {
                System.out.println(fileContent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
