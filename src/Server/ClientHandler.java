package Server;

import java.io.*;
import java.net.Socket;

/**
 * Manage clients.
 */
public class ClientHandler {
    private Socket clientSocket;
    private OutputStream outputWriter;
    private InputStream inputStream;
    private DataOutputStream dataOutputStream;
    private DataInputStream dataInputStream;


    public ClientHandler(final Socket clientSocket) throws Exception {
        this.clientSocket = clientSocket;

        // writing to socket.
        this.outputWriter = clientSocket.getOutputStream();

        this.inputStream = clientSocket.getInputStream();

        this.dataInputStream = new DataInputStream(clientSocket.getInputStream());

        this.dataOutputStream = new DataOutputStream(clientSocket.getOutputStream());

    }

    public OutputStream getOutputStream() {
        return outputWriter;
    }

    public String readUTF() throws IOException {
        return dataInputStream.readUTF();
    }

    public void writeUTF(String message) throws IOException {
        dataOutputStream.writeUTF(message);
    }

    public void writeEND() throws IOException {
        dataOutputStream.writeUTF("END");
    }

    public void writeLong(long num) throws IOException {
        dataOutputStream.writeLong(num);
    }

    public DataInputStream getDataInputStream() {
        return dataInputStream;
    }

    public InputStream getInputStream() { return inputStream;}
}
