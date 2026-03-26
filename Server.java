import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.File;
import java.io.FileReader;

/**
 * A Server.
 */
public class Server {
    public static void main(String[] args) {

        try {
            ServerSocket serverSocket = new ServerSocket(9806);
            Socket clientSocket = serverSocket.accept(); // wait for client.

            // Create directory
            File serverDirectory = new File("./Server_Files");
            serverDirectory.mkdirs();

            // Create files
            File orangFile = new File("./Server_Files/orange.txt");
            File pinkFile = new File("./Server_Files/pink.txt");
            File blueFile = new File("./Server_Files/blue.txt");

            orangFile.createNewFile();
            pinkFile.createNewFile();
            blueFile.createNewFile();

            /**
             * CUSTOME PROTOCOL:
             * LIST : List files.
             * OPEN file name: Send file content.
             */
            BufferedReader clientReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            String clientRequest = clientReader.readLine();

            OutputStream serverOutput = clientSocket.getOutputStream();

            // Handle LIST command.
            if (clientRequest.equals("LIST")) {
                File[] files = new File("./Server_Files").listFiles();

                // Send the file names to the client.
                for (File file : files) {
                    String fileName = file.getName() + "\n";
                    serverOutput.write(fileName.getBytes());
                }
                serverOutput.write("END\n".getBytes());
                serverOutput.flush();
            }

            // Handles OPEN request.
            BufferedReader commandReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            String command;

            while ((command = commandReader.readLine()) != null) {
                if (command.equals("OPEN orange.txt")) {
                    FileReader fr = new FileReader("./Server_Files/orange.txt");
                    BufferedReader file_buffer = new BufferedReader(fr);

                    String line_from_file;

                    OutputStream fileOutput = clientSocket.getOutputStream();

                    while ((line_from_file = file_buffer.readLine()) != null) {
                        fileOutput.write((line_from_file + "\n").getBytes());
                    }
                    fileOutput.write("END\n".getBytes());
                    clientSocket.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}