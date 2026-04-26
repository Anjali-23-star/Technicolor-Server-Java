import java.net.ServerSocket;
import java.net.Socket;
import Server.ClientHandler;
import Server.FileService;

import java.io.File;
import Server.CommandProcessor;

/**
 * A Server.
 */
public class Server {
    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(9806);

            while(true) {
                Socket clientSocket = serverSocket.accept(); // Waiting for a client.
                ClientThread clientThread = new ClientThread(clientSocket);

                clientThread.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Managing multiple clients.
    public static class ClientThread extends Thread {
        private final Socket clientSocket;

        public ClientThread(final Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        @Override
        public void run() {
            try{
                // Each client session are independent.
                FileService fileService = new FileService(new File("./src/Server_Files"));
                ClientHandler clientHandler = new ClientHandler(clientSocket);
                CommandProcessor commandProcessor = new CommandProcessor(fileService, clientHandler);

                String command;

                /**
                 * Executing command.
                 */
                while ((command = clientHandler.readCommand()) != null) {
                    commandProcessor.processCommand(command);

                    if(command.equalsIgnoreCase("exit")) {
                        clientSocket.close();
                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}