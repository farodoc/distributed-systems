package homework;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Client {

    private static final String SERVER_ADDRESS = "localhost";
    private static final InetAddress SERVER_IP;
    private static final int SERVER_PORT = 12345;
    private static final String MULTICAST_ADDRESS = "230.0.0.1";
    private static final InetAddress MULTICAST_GROUP;
    private static final int MULTICAST_PORT = 12346;
    private static final String ASCII_ART = """
            
                                    \\_/
                                  --(_)--
                                    / \\
                   ,-,  .----.
                   |-| /______\\    __ __
                   | | |  __  |   (())())
                   | | | |><| |    )( )(
                 .-''''''''''''-.-'''''''-.
            """;

    static {
        try {
            SERVER_IP = InetAddress.getByName(SERVER_ADDRESS);
            MULTICAST_GROUP = InetAddress.getByName(MULTICAST_ADDRESS);
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    private static Client client;
    private int id;
    private Socket socket;
    private DatagramSocket udpSocket;
    private MulticastSocket multicastSocket;
    private final ExecutorService executorService = Executors.newCachedThreadPool();


    public static void main(String[] args) throws IOException {
        client = new Client();
        client.runClient();
    }

    private void runClient() {
        try {
            socket = new Socket(SERVER_IP, SERVER_PORT);
            udpSocket = new DatagramSocket(socket.getLocalPort());
            multicastSocket = new MulticastSocket(MULTICAST_PORT);

            multicastSocket.joinGroup(new InetSocketAddress(MULTICAST_GROUP, MULTICAST_PORT), null);

            // in & out streams
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));

            // initializing client id
            String initResponse = in.readLine();
            id = Integer.parseInt(initResponse);

            System.out.println("Connection Successful!\nID: " + id);

            // TCP read thread
            executorService.submit(() -> tcpHandler(in));

            // UDP read thread
            executorService.submit(this::udpHandler);

            // Multicast read thread
            executorService.submit(this::multicastHandler);

            // write thread
            String userInput;
            while ((userInput = stdIn.readLine()) != null && !socket.isClosed()) {
                String command = userInput.split(" ")[0];

                switch (command) {
                    case "[U]":
                        sendUdpMessage(udpSocket, userInput.substring(command.length()));
                        break;
                    case "[M]":
                        sendMulticastMessage(udpSocket, userInput.substring(command.length()));
                        break;
                    case "[T]":
                        sendUdpMessage(udpSocket, ASCII_ART);
                        break;
                    default:
                        out.println(userInput);
                        break;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            stopClient(socket, udpSocket, multicastSocket, executorService);
        }
    }

    private void tcpHandler(BufferedReader in) {
        try {
            String response;
            while ((response = in.readLine()) != null) {
                System.out.println(response);
            }
        } catch (IOException e) {
            if (socket.isClosed()) {
                return;
            }
            System.out.println("Server closed the connection.");
        } finally {
            stopClient(socket, udpSocket, multicastSocket, executorService);
        }
    }

    private void udpHandler() {
        byte[] receiveBuffer = new byte[1024];
        DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);

        while (true) {
            try {
                udpSocket.receive(receivePacket);
                String msg = new String(receivePacket.getData(), 0, receivePacket.getLength(), "cp1250");
                System.out.println("[UDP] " + msg);
            } catch (IOException e) {
                if (udpSocket.isClosed()) {
                    break;
                }
                e.printStackTrace();
            }
        }
    }

    private void multicastHandler() {
        byte[] receiveBuffer = new byte[1024];
        DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);

        while (true) {
            try {
                multicastSocket.receive(receivePacket);
                String msg = new String(receivePacket.getData(), 0, receivePacket.getLength(), "cp1250");
                int senderId = Integer.parseInt(msg.substring(1, msg.indexOf("]")));
                if (senderId == id) {
                    continue;
                }
                System.out.println("[Multicast] " + msg);
            } catch (IOException e) {
                if (multicastSocket.isClosed()) {
                    break;
                }
                e.printStackTrace();
            }
        }
    }

    private void sendUdpMessage(DatagramSocket udpSocket, String message) throws IOException {
        byte[] sendBuffer = message.getBytes();

        DatagramPacket sendPacket = new DatagramPacket(sendBuffer, sendBuffer.length, SERVER_IP, SERVER_PORT);
        udpSocket.send(sendPacket);
    }

    private void sendMulticastMessage(DatagramSocket udpSocket, String message) throws IOException {
        String messageWithId = "[" + id + "]:" + message;
        byte[] sendBuffer = messageWithId.getBytes();

        DatagramPacket sendPacket = new DatagramPacket(sendBuffer, sendBuffer.length, MULTICAST_GROUP, MULTICAST_PORT);
        udpSocket.send(sendPacket);
    }

    private void stopClient(Socket socket, DatagramSocket udpSocket, MulticastSocket multicastSocket, ExecutorService executorService) {
        try {
            System.out.println("Exiting...");
            executorService.shutdownNow();
            if (!multicastSocket.isClosed()) {
                multicastSocket.leaveGroup(new InetSocketAddress(MULTICAST_GROUP, MULTICAST_PORT), null);
                multicastSocket.close();
            }
            if (!socket.isClosed()) {
                socket.close();
            }
            if (!udpSocket.isClosed()) {
                udpSocket.close();
            }
            System.exit(0);
        } catch (IOException e) {
            System.out.println("Error while closing the client.");
            e.printStackTrace();
        }
    }

}
