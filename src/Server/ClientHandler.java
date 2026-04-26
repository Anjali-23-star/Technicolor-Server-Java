package Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Manage clients.
 */
public class ClientHandler {
    private Socket clientSocket;
    private BufferedReader inputReader;
    private OutputStream outputWriter;
    private String readLine;

    public ClientHandler(final Socket clientSocket) throws Exception {
        this.clientSocket = clientSocket;

        // reading from socket.
        this.inputReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

        // writing to socket.
        this.outputWriter = clientSocket.getOutputStream();
    }

    public BufferedReader getInputReader() {
        return inputReader;
    }

    public OutputStream getOutputWriter() {
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
}
