package Main;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

import Server.ClientHandler;
import Server.FileService;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import Server.CommandProcessor;
import Server.PersistentUsers;

/**
 * A Main.Server.
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
        private boolean authenticated = false;
        private String loggedInUser;
        private ClientHandler clientHandler;
        private CommandProcessor commandProcessor;
        private FileService fileService;

        private final PersistentUsers users = new PersistentUsers();

        public ClientSession(final Socket clientSocket) throws Exception {
            this.clientSocket = clientSocket;
        }

        @Override
        public void run() {
            try {
                // Each client session are independent.
                FileService fileService = new FileService(new File("./src/Server_Files"));
                clientHandler = new ClientHandler(clientSocket);
                commandProcessor = new CommandProcessor(fileService, clientHandler);

                String command;

                /**
                 * Executing command.
                 */
                while ((command = clientHandler.readUTF()) != null) {
                    String cmd[] = command.split("\\s+");

                    if (handleRegister(cmd)) {
                        continue;
                    }

                    if(handleLogin(cmd)) {
                        continue;
                    }

                    if(!authenticated) {
                        clientHandler.writeUTF("Please login first.");
                        clientHandler.writeEND();
                    }else {
                        commandProcessor.setCurrentUser(loggedInUser);
                        commandProcessor.processCommand(command);
                    }

                    if(command.equalsIgnoreCase(Protocol.EXIT.name())) {
                        clientSocket.close();
                        break;
                    }
                }
            }catch (Exception e) {
                e.printStackTrace();
            }
        }

        // Handles user registration.
        boolean handleRegister(String[] cmd) throws IOException {
            if (!cmd[0].equalsIgnoreCase("REGISTER")) {
                return false;
            }

            if (cmd.length <= 1) {
                clientHandler.writeUTF("The command is: REGISTER <username>");
                clientHandler.writeEND();
                return true;
            }

            String newUserName = cmd[1].toUpperCase();

            clientHandler.writeUTF("Please enter password.");
            clientHandler.writeEND();

            String newPassword;

            while(true) {
                newPassword = clientHandler.readUTF().trim();

                if (newPassword.isEmpty()) {
                    clientHandler.writeUTF("Password cannot be empty.");
                    clientHandler.writeUTF("Please enter password.");
                    clientHandler.writeEND();
                    continue;
                }

                if (newPassword.contains(" ")) {
                    clientHandler.writeUTF("Password should not contain spaces.");
                    clientHandler.writeUTF("Please enter password.");
                    clientHandler.writeEND();
                    continue;
                }

                break;
            }

            if (userExists(newUserName)) {
                clientHandler.writeUTF("This user name is already registered.");
                clientHandler.writeEND();
                return true;
            }

            try(FileWriter writer = new FileWriter("./src/Server_Files/ClientAuthDetails.txt", true)) {
                writer.write("\n");
                writer.write(newUserName+":"+newPassword.toUpperCase());
            }

            clientHandler.writeUTF("Registration is successful.");
            clientHandler.writeEND();

            return true;
        }

        // Handles user login.
        private boolean handleLogin(String[] cmd) throws IOException{

            if(!cmd[0].equalsIgnoreCase("LOGIN")) {
                return false;
            }

            String clientName=null;

            if(cmd.length>1) {
                clientName = cmd[1].toUpperCase();
            }

            clientHandler.writeUTF("Please enter password.");
            clientHandler.writeEND();

            String password = clientHandler.readUTF().toUpperCase();

            if(clientName!=null && users.isUserExists(clientName, password)) {
                authenticated = true;
                loggedInUser = clientName;

                clientHandler.writeUTF("User Authenticated.");
                clientHandler.writeEND();
            }
            else {
                clientHandler.writeUTF("Authenticated Failed.");
                clientHandler.writeUTF("Invalid User name or password.");
                clientHandler.writeUTF("Please try again.");
                clientHandler.writeEND();
            }

            return true;
         }

         // Checks if user to be registered already exists.
        private boolean userExists(String username) throws IOException {
            File file = new File("./src/Server_Files/ClientAuthDetails.txt");
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String line;

                while ((line = br.readLine()) != null) {
                    if (line.isBlank()) {
                        continue;
                    }
                    String[] cred = line.split(":");
                    if (cred.length != 2) {
                        continue;
                    }
                    if (cred[0].equalsIgnoreCase(username)) {
                        return true;
                    }
                }
            }

            return false;
        }
    }
}