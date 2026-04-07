import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
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

            File[] files = new File("./Server_Files").listFiles();

            /**
             * CUSTOME PROTOCOL:
             * LIST : List files.
             * OPEN file name: Send file content.
             */
            BufferedReader clientReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            OutputStream serverOutput = clientSocket.getOutputStream();

            // Handles list/OPEN request.
            String command;
            while ((command = clientReader.readLine()) != null) {
                command = command.trim();

                String cmd[] = command.split("\\s+"); // the first argument of the request.
                String action = cmd[0].toUpperCase();

                switch (action) {
                    case "LIST":
                        // Send the file names to the client.
                        for (File file : files) {
                            String fileName = file.getName() + "\n";
                            serverOutput.write(fileName.getBytes());
                        }
                        serverOutput.write("END\n".getBytes());
                        break;

                    case "OPEN":
                        boolean fileNotFound = true;

                        if (cmd.length == 2) {
                            for (File file : files) {
                                // Search for the file requested by the user.
                                if (cmd[1].equalsIgnoreCase(file.getName())) {

                                    // A flag for file existence.
                                    fileNotFound = false;
                                    FileReader fr = new FileReader(file);
                                    BufferedReader file_buffer = new BufferedReader(fr);

                                    String file_content;

                                    while ((file_content = file_buffer.readLine()) != null) {
                                        serverOutput.write((file_content + "\n").getBytes());
                                    }
                                    serverOutput.write("END\n".getBytes());

                                    break;
                                }

                            }
                            if (fileNotFound) {
                                serverOutput.write((cmd[1] + " file not found.\n").getBytes());
                                serverOutput.write("END\n".getBytes());
                            }
                        } else {
                            serverOutput.write("Please enter a valid file name.".getBytes());
                            serverOutput.write("END\n".getBytes());
                        }
                        break;

                    case "EXIT":
                        serverOutput.write("This is server, Signing off for now.\n".getBytes());
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}