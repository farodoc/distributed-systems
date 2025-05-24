package src;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Team {
    private final String name;
    private Channel channel;
    private Connection connection;
    private String queueName;

    public Team(String name) {
        this.name = name;
        this.queueName = name + "_queue";
    }

    public void start() throws IOException, TimeoutException {
        System.out.println("Team " + name + " started.");

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");

        connection = factory.newConnection();
        channel = connection.createChannel();

        channel.exchangeDeclare("equipment_orders_exchange", BuiltinExchangeType.DIRECT);
        channel.exchangeDeclare("admin_exchange", BuiltinExchangeType.TOPIC);

        channel.queueDeclare(queueName, false, false, false, null);

        channel.basicConsume(queueName, true, (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            System.out.println("[ Team " + name + " received ]: " + message);
        }, consumerTag -> {});

        String adminQueueName = "admin_to_team_" + name;
        channel.queueDeclare(adminQueueName, false, false, false, null);
        channel.queueBind(adminQueueName, "admin_exchange", "teams");
        channel.queueBind(adminQueueName, "admin_exchange", "all");

        channel.basicConsume(adminQueueName, true, (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            System.out.println("[ Team " + name + " received admin message ]: " + message);
        }, consumerTag -> {});
    }

    public void order(String equipment) throws IOException {
        String message = name + ":" + equipment;
        System.out.println("Team " + name + " orders [ " + equipment + " ]");

        channel.basicPublish(
                "equipment_orders_exchange",
                equipment,
                new AMQP.BasicProperties.Builder()
                        .replyTo(queueName)
                        .build(),
                message.getBytes("UTF-8")
        );
    }

    public void close() {
        try {
            if (channel != null) {
                channel.close();
            }
            if (connection != null) {
                connection.close();
            }
            System.out.println("Team " + name + " closed.");
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }
    }
}
