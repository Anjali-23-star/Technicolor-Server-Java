package Server;

import java.io.*;
import java.net.Socket;

/**
 * Manage clients.
 */
public class ClientHandler {
    private Socket clientSocket;
    private BufferedReader inputReader;
    private OutputStream outputWriter;
    private InputStream inputStream;
    private String readLine;

    public ClientHandler(final Socket clientSocket) throws Exception {
        this.clientSocket = clientSocket;

        // reading from socket.
        this.inputReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

        // writing to socket.
        this.outputWriter = clientSocket.getOutputStream();

        this.inputStream = clientSocket.getInputStream();
    }

    public BufferedReader getInputReader() {
        return inputReader;
    }

    public OutputStream getOutputStream() {
        return outputWriter;
    }

    public String readCommand() throws IOException {
        return getInputReader().readLine().trim();
    }

    public void sendMessage(final String message) throws IOException {
        outputWriter.write((message+"\n").getBytes());
    }

    public void sendEND() throws IOException {
        outputWriter.write("END\n".getBytes());
    }

    public InputStream getInputStream() { return inputStream;}
}
