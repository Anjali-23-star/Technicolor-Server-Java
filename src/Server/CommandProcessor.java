package Server;

import Main.GeminiService;

import java.io.*;
import java.nio.file.Files;


public class CommandProcessor {

    private final FileService fileService;
    private final ClientHandler clientHandler;
    private String userName;
    private GeminiService geminiService;

    public CommandProcessor(final FileService fileService, final ClientHandler clientHandler) {
        this.fileService = fileService;
        this.clientHandler = clientHandler;
    }

    public void processCommand(final String command) throws IOException, InterruptedException {
        String[] cmd = command.split("\\s+");
        String firstArg = cmd[0].toUpperCase();
        String secondArg = null;
        int tokens = cmd.length;

        if(tokens >1) {
           secondArg  = cmd[1];
        }

        handleCommand(firstArg, secondArg, tokens);
    }

    // Handles CD command.
    private void handleCD(final String secondArg, final int tokens) throws IOException {
        System.out.println("["+ userName+"] CD");
        if(tokens == 2) {
            boolean fileExists = fileService.changeDirectory(secondArg);
            if(fileExists) {
                clientHandler.writeUTF("Changed directory");
                clientHandler.writeEND();
            }
            else{
                clientHandler.writeUTF("No such directory found.");
                clientHandler.writeEND();
            }
        }
        else{
            clientHandler.writeUTF("Please enter a valid directory.");
            clientHandler.writeEND();
        }
    }

    // handle file download.
    private void handleDownload(String secondCmd) throws IOException{
        String fileName = secondCmd;

        System.out.println("["+ userName+"] DOWNLOAD :"+fileName);
        final var file = fileService.getFile(fileName);

        // If file is not found.
        if(file == null) {
            clientHandler.writeUTF("Sorry, no file of this name is found.");
            clientHandler.writeEND();
        }

        else {
            clientHandler.writeUTF("Download");
            long fileLength = file.length();
            clientHandler.writeLong(fileLength);

            // Sending content of the file.
            byte[] buffer = new byte[4096];

            // Reading the file requested by the client.
            FileInputStream fio = new FileInputStream(file);
            int read;

            while ((read = fio.read(buffer)) != -1) {
                clientHandler.getOutputStream().write(buffer, 0, read);
            }

            fio.close();

            clientHandler.writeUTF("Download successful.");
            clientHandler.writeEND();
        }
    }

    // Handles Exit command.
    private void handleExitCmd() throws IOException{
        System.out.println("["+ userName+"] EXIT");
        clientHandler.writeUTF("This is server, Signing off for now.");
        clientHandler.writeEND();
    }

    // Handles invalid command.
    private void handleInvalidCmd() throws IOException {
        System.out.println("["+ userName+"] INVALID COMMAND.");
        clientHandler.writeUTF("Please enter a valid command");
        clientHandler.writeEND();
    }

    // Handles list command.
    private void handleListCmd() throws IOException {
        System.out.println("["+ userName+"] LIST");
        // Send file names to the client.
        for (File file : fileService.listFiles()) {
            String fileName = file.getName();
            clientHandler.writeUTF(fileName);
        }
        clientHandler.writeEND();
    }

    // Handles open command.
    private void handleOpenCmd(final String secondArg, final int tokens) throws IOException {
        System.out.println("["+ userName+"] OPEN: "+secondArg);
        boolean fileNotFound = true;

        if (tokens == 2) {
            for (File file : fileService.listFiles()) {
                // Search for the file requested by the user.
                if (secondArg.equalsIgnoreCase(file.getName())) {

                    // A flag for file existence.
                    fileNotFound = false;

                    try(BufferedReader file_buffer = new BufferedReader(new FileReader(file))) {
                        String file_content;

                        while ((file_content = file_buffer.readLine()) != null) {
                            clientHandler.writeUTF(file_content);
                        }
                    }
                    clientHandler.writeEND();
                    break;
                }
            }
            if (fileNotFound) {
                clientHandler.writeUTF((secondArg + " file not found."));
                clientHandler.writeEND();
            }
        } else {
            clientHandler.writeUTF("Please enter a valid file name.");
            clientHandler.writeEND();
        }
    }

    // Handles PWD command.
    private void handlePWDCmd() throws IOException {
        System.out.println("["+ userName+"] PWD");
        clientHandler.writeUTF(fileService.getPWD());
        clientHandler.writeEND();
    }

    private void handleUpload() throws  IOException {
        // Reading meta-data of the file.
        String fileName = clientHandler.readUTF();
        System.out.println("["+ userName+"] UPLOAD: "+fileName);
        long fileLength = clientHandler.getDataInputStream().readLong();

        handleFileContent(fileLength, fileName);

        clientHandler.writeUTF("Upload successful.");
        clientHandler.writeEND();
    }

    private void handleFileContent(long fileLength, String fileName) throws IOException {
        // Create a file.
        File uploadedFile = fileService.createFile(fileName);

        FileOutputStream fio = new FileOutputStream(uploadedFile);

        byte[] bytes = new byte[4096];

        long remainingBytes = fileLength;
        while(remainingBytes > 0) {
            // Loading data into bytes.
            int read = clientHandler.getInputStream().read(bytes);
            fio.write(bytes, 0, read);

            remainingBytes-=read;
        }
        fio.close();
    }

    public void showAvailableCommands() throws IOException {
            clientHandler.writeUTF("LIST");
            clientHandler.writeUTF("OPEN <file name>");
            clientHandler.writeUTF("cd <directory>");
            clientHandler.writeUTF("PWD");
            clientHandler.writeUTF("UPLOAD <file>");
            clientHandler.writeUTF("DOWNLOAD <file>");
            clientHandler.writeUTF("WHOAMI");
            clientHandler.writeUTF("EXIT");
            clientHandler.writeEND();
    }

    // Sets current user.
    public void setCurrentUser(String userName) {
        this.userName=userName;
   }

   // Gets current user.
    public void showCurrentUser() throws IOException {
        System.out.println("["+ userName+"] LOGGED IN USER");
        clientHandler.writeUTF("You are logged-in as: "+ userName);
        clientHandler.writeEND();
    }

    // Summarize file using AI.
    public void summarizeFile(String fileName) throws IOException, InterruptedException {
        geminiService = new GeminiService();

        final var file = fileService.getFile(fileName);

        String content = Files.readString(file.toPath());

        geminiService.generateRequest(content);

        clientHandler.writeUTF(geminiService.sendResponse());
        clientHandler.writeEND();

    }

    // Handles command.
    private void handleCommand(final String command, final String secondArg, final int tokens) throws IOException, InterruptedException {
        switch(command) {
            case "LIST":
                handleListCmd();
                break;
            case "OPEN":
                handleOpenCmd(secondArg, tokens);
                break;
            case "PWD":
                handlePWDCmd();
                break;
            case "CD":
                handleCD(secondArg, tokens);
                break;
            case "UPLOAD":
                handleUpload();
                break;
            case "DOWNLOAD":
                handleDownload(secondArg);
                break;
            case "WHOAMI":
                showCurrentUser();
                break;
            case "HELP":
                showAvailableCommands();
                break;
            case "SUMMARIZE":
                summarizeFile(secondArg);
                break;
            case "EXIT":
                handleExitCmd();
                break;
            default:
                handleInvalidCmd();
                break;
        }
    }
}