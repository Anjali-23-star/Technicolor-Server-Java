package Server;

import java.io.*;

public class CommandProcessor {

    private final FileService fileService;
    private final ClientHandler clientHandler;

    public CommandProcessor(final FileService fileService, final ClientHandler clientHandler) {
        this.fileService = fileService;
        this.clientHandler = clientHandler;
    }

    public void processCommand(final String command) throws IOException {
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
        if(tokens == 2) {
            boolean fileExists = fileService.changeDirectory(secondArg);
            if(fileExists) {
                clientHandler.sendMessage("Changed directory");
                clientHandler.sendEND();
            }
            else{
                clientHandler.sendMessage("No such directory found.");
                clientHandler.sendEND();
            }
        }
        else{
            clientHandler.sendMessage("Please enter a valid directory.");
            clientHandler.sendEND();
        }
    }

    // handle file download.
    public void handleDownload(String secondCmd) throws IOException{
        String fileName = secondCmd;

        final var file = fileService.getFile(fileName);

        // If file is not found.
        if(file == null) {
            clientHandler.sendMessage("Sorry, no file of this name is found.");
            clientHandler.sendEND();
        }

        else {
            clientHandler.sendMessage("Download");
            long fileLength = file.length();
            clientHandler.sendMessage(Long.toString(fileLength));

            // Sending content of the file.
            byte[] buffer = new byte[4096];

            // Reading the file requested by the client.
            FileInputStream fio = new FileInputStream(file);
            int read;

            while ((read = fio.read(buffer)) != -1) {
                clientHandler.getOutputStream().write(buffer, 0, read);
            }

            fio.close();

            clientHandler.sendMessage("Download successful.");
            clientHandler.sendEND();
        }
    }

    // Handles Exit command.
    private void handleExitCmd() throws IOException{
        clientHandler.sendMessage("This is server, Signing off for now.");
        clientHandler.sendEND();
    }

    // Handles invalid command.
    private void handleInvalidCmd() throws IOException {
        clientHandler.sendMessage("Please enter a valid command");
        clientHandler.sendEND();
    }

    // Handles list command.
    private void handleListCmd() throws IOException {
        // Send file names to the client.
        for (File file : fileService.listFiles()) {
            String fileName = file.getName();
            clientHandler.sendMessage(fileName);
        }
        clientHandler.sendEND();
    }

    // Handles open command.
    private void handleOpenCmd(final String secondArg, final int tokens) throws IOException {
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
                            clientHandler.sendMessage(file_content);
                        }
                    }
                    clientHandler.sendEND();
                    break;
                }
            }
            if (fileNotFound) {
                clientHandler.sendMessage((secondArg + " file not found."));
                clientHandler.sendEND();
            }
        } else {
            clientHandler.sendMessage("Please enter a valid file name.");
            clientHandler.sendEND();
        }
    }

    // Handles PWD command.
    private void handlePWDCmd() throws IOException {
        clientHandler.sendMessage(fileService.getPWD());
        clientHandler.sendEND();
    }

    public void handleUpload() throws  IOException {
        // Reading meta-data of the file.
        String fileName = clientHandler.readCommand();
        long fileLength = Long.parseLong(clientHandler.readCommand());

        handleFileContent(fileLength, fileName);

        clientHandler.sendMessage("Upload successful.");
        clientHandler.sendEND();
    }

    public void handleFileContent(long fileLength, String fileName) throws IOException {
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

    // Handles command.
    private void handleCommand(final String command, final String secondArg, final int tokens) throws IOException {
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
            case "EXIT":
                handleExitCmd();
                break;
            default:
                handleInvalidCmd();
                break;
        }
    }
}