import java.net.ServerSocket;
import java.net.Socket;
import Server.ClientHandler;
import Server.FileService;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import Server.CommandProcessor;
import Server.PersistentUsers;

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
        private boolean authenticated=false;
        private String loggedInUser;
        private final PersistentUsers users=new PersistentUsers();

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
                while ((command = clientHandler.readUTF()) != null) {
                    String cmd[] = command.split("\\s+");
                    if(cmd[0].equalsIgnoreCase("LOGIN")) {
                        String clientName = null;

                        // check if client entered the user name.
                        if(cmd.length>1) {
                            clientName = cmd[1].toUpperCase();
                        }

                        clientHandler.writeUTF("Please enter password");
                        clientHandler.writeEND();

                        String password = clientHandler.readUTF().toUpperCase();

                        // Checking if user exists.
                        if(clientName!=null && users.isUserExists(clientName, password)) {
                            clientHandler.writeUTF("User Authenticated.");
                            authenticated=true;
                            loggedInUser=clientName;
                            clientHandler.writeEND();
                        }
                        else {
                            clientHandler.writeUTF("Authentication failed.");
                            clientHandler.writeUTF("Invalid User name or password.");
                            clientHandler.writeUTF("Please try again.");
                            clientHandler.writeEND();
                        }

                        continue;
                    }

                    if(!authenticated && !command.toUpperCase().startsWith("LOGIN")) {
                        clientHandler.writeUTF("Please login first.");
                        clientHandler.writeEND();
                    }
                    // Only authenticated user have access to command.
                    else {
                       commandProcessor.setCurrentUser(loggedInUser);
                       commandProcessor.processCommand(command);
                    }

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