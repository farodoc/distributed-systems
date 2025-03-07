import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;

public class JavaUdpClient {

    public static void main(String args[]) throws Exception
    {
        System.out.println("JAVA UDP CLIENT");
        DatagramSocket socket = null;
//        int portNumber = 9008;
        int portNumber = 9009;

        try {
            socket = new DatagramSocket();
            InetAddress address = InetAddress.getByName("localhost");
//            byte[] sendBuffer = "Ping Java Udp".getBytes();
            byte[] sendBuffer = "Jtext from java client".getBytes();
            byte[] receiveBuffer = new byte[1024];

            DatagramPacket sendPacket = new DatagramPacket(sendBuffer, sendBuffer.length, address, portNumber);
            socket.send(sendPacket);

            Arrays.fill(receiveBuffer, (byte)0);

            DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
            socket.receive(receivePacket);

            String msg = new String(receivePacket.getData(), 0, receivePacket.getLength());
            System.out.println("received msg: " + msg);
        }
        catch(Exception e){
            e.printStackTrace();
        }
        finally {
            if (socket != null) {
                socket.close();
            }
        }
    }
}
