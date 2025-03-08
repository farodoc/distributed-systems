package homework;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {

    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 12345;
    private static int id;

    public static void main(String[] args) throws IOException {

        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT)) {
            System.out.println("Connection Successful!");

            // in & out streams
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));

            // initializing client id
            String initResponse = in.readLine();
            id = Integer.parseInt(initResponse);
            System.out.println("ID: " + initResponse);

            // read thread
            Thread readThread = new Thread(() -> {
                try {
                    String response;
                    while ((response = in.readLine()) != null) {
                        System.out.println(response);
                    }
                } catch (IOException e) {
                    System.out.println("Server closed the connection.");
                } finally {
                    System.out.println("Exiting...");
                    System.exit(0);
                }
            });
            readThread.start();

            // write thread
            String userInput;
            while ((userInput = stdIn.readLine()) != null && !socket.isClosed()) {
                out.println(userInput);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.out.println("Connection closed.");
        }
    }

}
