package homework;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;

public class Client {

    private static final String SERVER_ADDRESS = "localhost";
    private static final InetAddress SERVER_IP;
    private static final int SERVER_PORT = 12345;
    private static final String MULTICAST_ADDRESS = "230.0.0.1";
    private static final InetAddress MULTICAST_GROUP;
    private static final int MULTICAST_PORT = 12346;
    private static int id;

    static {
        try {
            SERVER_IP = InetAddress.getByName(SERVER_ADDRESS);
            MULTICAST_GROUP = InetAddress.getByName(MULTICAST_ADDRESS);
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }


    public static void main(String[] args) throws IOException {

        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
             DatagramSocket udpSocket = new DatagramSocket(socket.getLocalPort());
             MulticastSocket multicastSocket = new MulticastSocket(MULTICAST_PORT)) {

            multicastSocket.joinGroup(MULTICAST_GROUP);

            System.out.println("Connection Successful!");

            // in & out streams
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));

            // initializing client id
            String initResponse = in.readLine();
            id = Integer.parseInt(initResponse);
            System.out.println("ID: " + id);

            // TCP read thread
            Thread tcpReadThread = new Thread(() -> {
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
            tcpReadThread.start();

            // UDP read thread
            Thread udpReadThread = new Thread(() -> {
                byte[] receiveBuffer = new byte[1024];
                DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);

                while (true) {
                    try {
                        udpSocket.receive(receivePacket);
                        String msg = new String(receivePacket.getData(), 0, receivePacket.getLength(), "cp1250");
                        System.out.println("[UDP] " + msg);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            udpReadThread.start();

            // Multicast read thread
            Thread multicastReadThread = new Thread(() -> {
                byte[] receiveBuffer = new byte[1024];
                DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);

                while (true) {
                    try {
                        multicastSocket.receive(receivePacket);
                        String msg = new String(receivePacket.getData(), 0, receivePacket.getLength(), "cp1250");
                        System.out.println("[Multicast] " + msg);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            multicastReadThread.start();

            // write thread
            String userInput;
            while ((userInput = stdIn.readLine()) != null && !socket.isClosed()) {
                if (userInput.startsWith("[U]")) {
                    sendUdpMessage(udpSocket, userInput.substring(3));
                } else if (userInput.startsWith("[M]")) {
                    sendMulticastMessage(udpSocket, userInput.substring(3));
                } else {
                    out.println(userInput);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.out.println("Connection closed.");
        }
    }

    private static void sendUdpMessage(DatagramSocket udpSocket, String message) throws IOException {
        byte[] sendBuffer = message.getBytes();

        DatagramPacket sendPacket = new DatagramPacket(sendBuffer, sendBuffer.length, SERVER_IP, SERVER_PORT);
        udpSocket.send(sendPacket);
    }

    private static void sendMulticastMessage(DatagramSocket udpSocket, String message) throws IOException {
        byte[] sendBuffer = message.getBytes();

        DatagramPacket sendPacket = new DatagramPacket(sendBuffer, sendBuffer.length, MULTICAST_GROUP, MULTICAST_PORT);
        udpSocket.send(sendPacket);
    }

}
