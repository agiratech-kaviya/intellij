import java.io.File;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class jar {
    public static void main(String[] args) {
        String jarPath = "/home/agira/Downloads/unijdbc.jar"; // Specify the path to your external JAR file

        // Create a command to execute the JAR file
        String command = "java -jar " + jarPath;

        try {
            // Create a process builder and set the command
            ProcessBuilder processBuilder = new ProcessBuilder(command.split(" "));
            processBuilder.directory(new File(System.getProperty("user.dir")));

            // Redirect the error stream
            processBuilder.redirectErrorStream(true);

            // Start the process
            Process process = processBuilder.start();

            // Read the output from the process
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            StringBuilder output = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }

            // Wait for the process to complete
            int exitCode = process.waitFor();

            // Print the output
            if (exitCode == 0) {
                System.out.println("The JAR is executable.");
            } else {
                System.out.println("The JAR is not executable. Error output:");
                System.out.println(output.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
