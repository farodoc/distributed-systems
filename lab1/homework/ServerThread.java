package homework;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;

public class ServerThread implements Runnable {

    private final Server server;
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private int clientId;
    private boolean isConnected = false;

    public ServerThread(Server server) {
        this.server = server;
    }

    public void setClientSocket(Socket clientSocket) {
        this.clientSocket = clientSocket;
        clientId = clientSocket.hashCode();
        isConnected = true;
        server.addClientThread(this);
    }

    @Override
    public void run(){
        System.out.println("####### New client session started. " + clientSocket.hashCode()
                + " | clientSocket.getLocalPort(): " + clientSocket.getLocalPort()
                + " | clientSocket.getPort(): " + clientSocket.getPort());
        try {
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out.println(clientId);
            listenToSocket();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void listenToSocket() throws IOException {
        try {
            while (isConnected) {
                String msg = in.readLine();

                if (msg == null) {
                    System.out.println("Client " + clientId + " disconnected.");
                    server.removeClientThread(this);
                    isConnected = false;
                    break;
                }

                System.out.println("received msg:" + msg);

                String command = msg.split(" ")[0];
                msg = "[" + clientId + "]:" + msg.substring(command.length());

                switch (command) {
                    case "[b]":
                        broadcast(msg);
                        break;

                    case "[q]":
                        server.removeClientThread(this);
                        clientSocket.close();
                        isConnected = false;
                        System.out.println("Client " + clientId + " disconnected.");
                        break;

                    default:
                        System.out.println("Invalid command.");
                        sendMessage("Invalid command.");
                        break;
                }
            }
        } catch (SocketException e) {
            System.out.println("Client " + clientId + " disconnected.");
            server.removeClientThread(this);
        }
    }

    public void sendMessage(String msg) {
        out.println(msg);
    }

    public void broadcast(String msg) {
        server.getClientThreads().forEach(clientThread -> {
            if (clientThread.getClientId() != clientId) {
                clientThread.sendMessage(msg);
            }
        });
    }

    public int getClientId() {
        return clientId;
    }

    public Socket getClientSocket() {
        return clientSocket;
    }

}
