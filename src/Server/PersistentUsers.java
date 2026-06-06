package Server;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

// Mimicking database.
public class PersistentUsers{
    Map<String, String> users = new HashMap<String, String>();

    public PersistentUsers() throws Exception {
        loadUsers();
    }

       public void loadUsers() throws Exception {
           BufferedReader br = new BufferedReader(new FileReader("./src/Server_Files/ClientAuthDetails.txt"));
           String line;

           while((line=br.readLine())!=null) {
               String[] creds = line.split(":");

               String userName = creds[0];
               String password = creds[1];

               // Populate the map.
               users.put(userName, password);
           }
       }

    // Checks if the logged-in user is an existing user.
    public boolean isUserExists(String name, String password) {
        if(users.containsKey(name) && users.get(name).equalsIgnoreCase(password)) {
            return true;
        }
        return false;
    }
}
