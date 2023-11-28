package uk.ac.ed.inf;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class createDir {
    public static void main(){

        // Define the name of the directory
        Path resultfiles = Paths.get("resultFiles/");

        // Try and create the directory
        try {
            Files.createDirectory(resultfiles);
            System.out.println("Directory created");
        // If the directory already exist, we can confirm
        } catch (IOException ignored) {
            System.out.println("Directory found");
        }
    }
}
