import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Z1_Producer {

    public static void main(String[] argv) throws Exception {

        // info
        System.out.println("Z1 PRODUCER");

        // connection & channel
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        // queue
        String QUEUE_NAME = "queue1";
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);        

        // producer (publish msg)

//        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
//        String message = "";
//        System.out.print("Enter message: ");
//        message = br.readLine();
//
//        channel.basicPublish("", QUEUE_NAME, null, message.getBytes());
//        System.out.println("Sent: " + message);

        String message = "1";

        for (int i = 0; i < 10; i++) {
            if (message.equals("1")) {
                message = "5";
            } else {
                message = "1";
            }

            channel.basicPublish("", QUEUE_NAME, null, message.getBytes());
            System.out.println("Sent: " + message);
        }

        // close
        channel.close();
        connection.close();
    }
}
