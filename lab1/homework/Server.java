package homework;

import java.io.IOException;
import java.net.*;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class Server {

    private final static int PORT_NUMBER = 12345;
    private static Set<ServerThread> clientThreads = ConcurrentHashMap.newKeySet();

    public static void main(String[] args) throws IOException {
        startServer();
    }

    private static void startServer() throws IOException {

        try (ServerSocket serverSocket = new ServerSocket(PORT_NUMBER)) {
            while(true){
                ServerThread serverThread = new ServerThread();
                try {
                    serverThread.setClientSocket(serverSocket.accept());
                    Thread thread = new Thread(serverThread);
                    thread.start();
                } catch (SocketTimeoutException socketTimeoutException) {
                    System.err.println(socketTimeoutException);
                }
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
