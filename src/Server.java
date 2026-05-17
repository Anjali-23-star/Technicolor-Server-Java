import java.net.ServerSocket;
import java.net.Socket;
import Server.ClientHandler;
import Server.FileService;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import Server.CommandProcessor;

/**
 * A Server.
 */
public class Server {
    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(9806);

            // Managing client session through Executor Service.
            ExecutorService es = Executors.newFixedThreadPool(2);

            while (true) {
                Socket clientSocket = serverSocket.accept();

                ClientSession clientSession = new ClientSession(clientSocket);
                es.execute(clientSession);
            }
        }
        catch (Exception e) {
                e.printStackTrace();
        }
    }

    // Managing multiple clients.
    public static class ClientSession implements Runnable {
        private final Socket clientSocket;

        public ClientSession(final Socket clientSocket) {
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

                    if(command.equalsIgnoreCase(Protocol.EXIT.name())) {
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