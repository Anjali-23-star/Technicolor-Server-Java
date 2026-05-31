package Server;

import java.util.HashMap;
import java.util.Map;

// Mimicking database.
public class PersistentUsers {
    Map<String, String> users = new HashMap<String, String>();

    // Adding users.
    private void addUsers() {
        users.put("ANIKA", "SCOOBYDOO");
        users.put("KANISHKA", "LEMONDEW");
        users.put("GERALD", "MOONBEAM");
        users.put("KARAN", "KAMIKAZE");
        users.put("SWATI", "HAGIMARU");
    }

    public PersistentUsers() {
        addUsers();
    }

    // Checks if the logged-in user is an existing user.
    public boolean isUserExists(String name, String password) {
        if(users.containsKey(name) && users.get(name).equalsIgnoreCase(password)) {
            return true;
        }
        return false;
    }
}
