package Server;

import java.io.File;

/**
 * A class for interacting with the files.
 */
public class FileService {

    private File currentDirectory;

    /**
     * Intializing with the current directory.
     *
     * @param currentDirectory The current directory.
     */
    public FileService(final File currentDirectory) {
        this.currentDirectory = currentDirectory;
    }

    /**
     * Change the directory.
     *
     * @param otherDirectory The directory to navigate to.
     */
    public void changeDirectory(final File otherDirectory) {
        this.currentDirectory = otherDirectory;
    }

    public boolean changeDirectory(final String path) {

        // If user wants to exit from the current directory.
        if (path.equals("..")) {
            File parent = currentDirectory.getParentFile();
            if (parent != null) {
                currentDirectory = parent;
                return true;
            }
            return false;
        }

        // If user wants to navigate to other directory (inside current directory).
        File newDir = new File(currentDirectory, path);

        if (newDir.exists() && newDir.isDirectory()) {
            currentDirectory = newDir;

            return true;
        }

        return false;
    }

    /**
     * Gets the current directory.
     *
     * @return The current directory.
     */
    public File getCurrentDirectory() {
        return currentDirectory;
    }

    // Gets Present Working Directory.
    public String getPWD() {
        return currentDirectory.getAbsolutePath();
    }

    /**
     * Listing files from the choosen directory.
     *
     * @return List of files in that directory.
     */
    public File[] listFiles() {
        final var fileList = currentDirectory.listFiles();

        return fileList;
    }
}
