import java.io.*;
import java.net.Socket;
import java.util.Arrays;

public class Client {
    public static void main(String[] args) {
        try {
            System.out.println("Client started.");

            Socket clientSocket = new Socket("localhost", 9806);

            // Reader for user input.
            BufferedReader userInputReader = new BufferedReader(new InputStreamReader(System.in));

            // Write to the server for binary files.
            DataOutputStream outputStream = new DataOutputStream(clientSocket.getOutputStream());

            // Read from the server in bytes(binary file).
            DataInputStream inputStream = new DataInputStream(clientSocket.getInputStream());

            // Printing the response from the server.
            while (true) {

                String userCommand = userInputReader.readLine().toUpperCase();

                // Writing to socket.
                outputStream.writeUTF(userCommand);

                // Uploading file.
                if(userCommand.startsWith(Protocol.UPLOAD.name())) {
                    uploadFile(userCommand, outputStream);
                }

                String serverResponse;
                // Response from the server.
                while ((serverResponse = inputStream.readUTF()) != null && !serverResponse.equals(String.valueOf(Protocol.END))) {
                    //Binary file transfer.
                    if(serverResponse.equalsIgnoreCase(Protocol.DOWNLOAD.name())) {

                        downloadFile(userCommand, inputStream);
                    }
                    // Simple text transfer.
                    else {
                        System.out.println(serverResponse);
                    }
                }

                // Client exits.
                if(userCommand.equalsIgnoreCase(Protocol.EXIT.name())) {
                    clientSocket.close();
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * DOWNLOAD:
     *          - Receive file name.
     *          - Receive file size.
     *          - Receive file content in bytes.
     */
    private static void downloadFile(String userCommand, DataInputStream inputStream) throws IOException {
        String[] args = userCommand.split("\\s+");

        if(args.length>1) {
            long fileLength = inputStream.readLong();

            // The dedicated path where downloaded files will be placed.
            File file = new File("C:\\Users\\HP\\Desktop\\" + args[1]);

            FileOutputStream fio = new FileOutputStream(file);

            byte[] buffer = new byte[4026];
            var remainingBytes = fileLength;

            // Writing into the file.
            while (remainingBytes > 0) {
                int read = inputStream.read(buffer, 0, (int)Math.min(buffer.length, remainingBytes));
                fio.write(buffer, 0, read);

                remainingBytes -= read;
            }
            fio.close();

            System.out.println(inputStream.readUTF());
        }
        else{
            System.out.println("Please enter a correct file name.");
        }
    }

    /**
     * UPLOAD:
     *       - Send file name.
     *       - Send file size.
     *       - Send file content in bytes.
     */
    private static void uploadFile(String userCommand, DataOutputStream outputStream) throws IOException {

        // Uploading the file: Extracting meta data.
        String [] arg = userCommand.split("\\s+");

        if(arg.length>1 && arg[0].equalsIgnoreCase(Protocol.UPLOAD.name())) {
            String filePath = arg[1];

            // Creating the file.
            File file = new File(filePath);

            // meta data.
            String fileName = file.getName();
            Long length = file.length();

            // Writing meta-data to the socket.
            outputStream.writeUTF(fileName);
            outputStream.writeLong(length);

            // Reading data of the file.
            FileInputStream fis= new FileInputStream(filePath);
            int read;
            byte[] buffer = new byte[4096];

            // Sending data over server.
            while((read = fis.read(buffer))!=-1) {
                outputStream.write(buffer, 0, read);
            }
        }
    }
}
