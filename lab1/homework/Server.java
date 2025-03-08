package homework;

import java.io.IOException;
import java.net.*;
import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    private static final int PORT_NUMBER = 12345;
    private static final Set<ServerThread> clientThreads = ConcurrentHashMap.newKeySet();

    public static void main(String[] args) throws IOException {
        startServer();
    }

    private static void startServer() throws IOException {

        try (ServerSocket serverSocket = new ServerSocket(PORT_NUMBER);
            DatagramSocket udpServerSocket = new DatagramSocket(PORT_NUMBER)) {

            ExecutorService executorService = Executors.newCachedThreadPool();
            executorService.submit(() -> listenToUdp(udpServerSocket));

            while (true) {
                ServerThread serverThread = new ServerThread();
                try {
                    serverThread.setClientSocket(serverSocket.accept());
                    executorService.submit(serverThread);
                } catch (SocketTimeoutException socketTimeoutException) {
                    System.err.println(socketTimeoutException);
                }
            }
        }
    }

    private static void listenToUdp(DatagramSocket udpSocket) {
        byte[] receiveBuffer = new byte[1024];

        while (true) {
            Arrays.fill(receiveBuffer, (byte) 0);
            DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
            try {
                udpSocket.receive(receivePacket);

                int senderId = -1;
                for (ServerThread clientThread : clientThreads) {
                    if (clientThread.getClientSocket().getPort() == receivePacket.getPort()
                            && clientThread.getClientSocket().getInetAddress().equals(receivePacket.getAddress())) {
                        senderId = clientThread.getClientId();
                    }
                }

                String msg = new String(receivePacket.getData(), 0, receivePacket.getLength(), "cp1250");
                System.out.println("[UDP] received msg: " + msg);
                msg = "[" + senderId + "]: " + msg;

                receiveBuffer = msg.getBytes();

                for (ServerThread clientThread : clientThreads) {
                    if (clientThread.getClientId() != senderId) {
                        InetAddress address = clientThread.getClientSocket().getInetAddress();
                        int port = clientThread.getClientSocket().getPort();
                        DatagramPacket sendPacket = new DatagramPacket(receiveBuffer, receiveBuffer.length, address, port);
                        udpSocket.send(sendPacket);
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void addClientThread(ServerThread clientThread) {
        clientThreads.add(clientThread);
    }

    public static void removeClientThread(ServerThread clientThread) {
        clientThreads.remove(clientThread);
    }

    public static Set<ServerThread> getClientThreads() {
        return clientThreads;
    }
}
