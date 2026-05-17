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

            // Write to the server.
            PrintWriter servWriter = new PrintWriter(clientSocket.getOutputStream(), true);

            // Write to the server for binary files.
            DataOutputStream outputStream = new DataOutputStream(clientSocket.getOutputStream());

            // Read from the server in bytes(binary file).
            DataInputStream inputStream = new DataInputStream(clientSocket.getInputStream());

            // Response from the server
            BufferedReader serverReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            // Printing the response from the server.
            while (true) {

                String userCommand = userInputReader.readLine();

                // Writing to socket.
                servWriter.println(userCommand);

                // Uploading the file: Extracting meta data.
                String [] arg = userCommand.split("\\s+");
                /**
                 * UPLOAD:
                 *       - Send file name.
                 *       - Send file size.
                 *       - Send file content in bytes.
                 */
                if(arg.length>1 && arg[0].equalsIgnoreCase(Protocol.UPLOAD.name())) {
                    String filePath = arg[1];

                    // Creating the file.
                    File file = new File(filePath);

                    // meta data.
                    String fileName = file.getName();
                    Long length = file.length();

                    // Writing meta-data to the socket.
                    servWriter.println(fileName);
                    servWriter.println(length);

                    // Reading data of the file.
                    FileInputStream fis= new FileInputStream(filePath);
                    int read;
                    byte[] buffer = new byte[4096];

                    // Sending data over server.
                    while((read = fis.read(buffer))!=-1) {
                       outputStream.write(buffer, 0, read);
                    }
                }

                String serverResponse;
                // Response from the server.
                while ((serverResponse = serverReader.readLine()) != null && !serverResponse.equals(String.valueOf(Protocol.END))) {
                    //Binary file transfer.
                    if(serverResponse.equalsIgnoreCase(Protocol.DOWNLOAD.name())) {

                        downloadFile(serverReader, arg[1], inputStream);
                    }
                    // Simple text transfer.
                    else {
                        System.out.println(serverResponse);
                    }

                }

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
    private static void downloadFile(BufferedReader serverReader, String fileName, DataInputStream inputStream) throws IOException {
        long fileLength = Long.parseLong(serverReader.readLine());

        // The dedicated path where downloaded files will be placed.
        File file = new File("C:\\Users\\HP\\Desktop\\"+fileName);

        FileOutputStream fio = new FileOutputStream(file);

        byte[] buffer = new byte[4026];
        var remainingBytes = fileLength;

        // Writing into the file.
        while(remainingBytes>0) {
            int read = inputStream.read(buffer);
            fio.write(buffer, 0, read);

            remainingBytes-=read;
        }
        fio.close();

        System.out.println(serverReader.readLine());

    }
}
