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
    private static Server server;
    private final Set<ServerThread> clientThreads = ConcurrentHashMap.newKeySet();
    private ServerSocket serverSocket;
    private DatagramSocket udpServerSocket;
    ExecutorService executorService = Executors.newCachedThreadPool();

    public static void main(String[] args) throws IOException {
        server = new Server();
        server.startServer();
    }

    private void startServer() throws IOException {
        try {
            serverSocket = new ServerSocket(PORT_NUMBER);
            udpServerSocket = new DatagramSocket(PORT_NUMBER);

            executorService.submit(this::udpHandler);

            while (true) {
                ServerThread serverThread = new ServerThread(this);
                try {
                    serverThread.setClientSocket(serverSocket.accept());
                    executorService.submit(serverThread);
                } catch (SocketTimeoutException socketTimeoutException) {
                    System.err.println(socketTimeoutException);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            stopServer();
        }
    }

    private void udpHandler() {
        byte[] receiveBuffer = new byte[1024];

        while (true) {
            Arrays.fill(receiveBuffer, (byte) 0);
            DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
            try {
                udpServerSocket.receive(receivePacket);

                int senderId = -1;
                for (ServerThread clientThread : clientThreads) {
                    if (clientThread.getClientSocket().getPort() == receivePacket.getPort()
                            && clientThread.getClientSocket().getInetAddress().equals(receivePacket.getAddress())) {
                        senderId = clientThread.getClientId();
                    }
                }

                String msg = new String(receivePacket.getData(), 0, receivePacket.getLength(), "cp1250");
                System.out.println("[UDP] received msg:" + msg);
                msg = "[" + senderId + "]:" + msg;

                receiveBuffer = msg.getBytes();

                for (ServerThread clientThread : clientThreads) {
                    if (clientThread.getClientId() != senderId) {
                        InetAddress address = clientThread.getClientSocket().getInetAddress();
                        int port = clientThread.getClientSocket().getPort();
                        DatagramPacket sendPacket = new DatagramPacket(receiveBuffer, receiveBuffer.length, address, port);
                        udpServerSocket.send(sendPacket);
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void addClientThread(ServerThread clientThread) {
        clientThreads.add(clientThread);
    }

    public void removeClientThread(ServerThread clientThread) {
        clientThreads.remove(clientThread);
    }

    public Set<ServerThread> getClientThreads() {
        return clientThreads;
    }

    private void stopServer() {
        executorService.shutdown();
        try {
            serverSocket.close();
            udpServerSocket.close();
        } catch (IOException e) {
            System.out.println("Error while shutting down server.");
            e.printStackTrace();
        }
    }

}
