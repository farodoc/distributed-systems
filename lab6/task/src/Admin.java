package src;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

public class Admin {
    private Connection connection;
    private Channel channel;
    private String logsQueueName;

    public void start() throws IOException, TimeoutException {
        System.out.println("Admin started.");

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");

        connection = factory.newConnection();
        channel = connection.createChannel();

        channel.exchangeDeclare("admin_exchange", BuiltinExchangeType.TOPIC);
        channel.exchangeDeclare("equipment_orders_logs_exchange", BuiltinExchangeType.FANOUT);

        logsQueueName = "admin_logs_queue";
        channel.queueDeclare(logsQueueName, false, false, false, null);
        channel.queueBind(logsQueueName, "equipment_orders_logs_exchange", "");

        channel.basicConsume(logsQueueName, true, (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
            System.out.println("[ Admin received log ]: " + message);
        }, consumerTag -> {});
    }

    public void sendMessageToTeams(String message) throws IOException {
        System.out.println("Admin sends message to all teams: " + message);
        channel.basicPublish(
                "admin_exchange",
                "teams",
                null,
                message.getBytes(StandardCharsets.UTF_8)
        );
    }

    public void sendMessageToSuppliers(String message) throws IOException {
        System.out.println("Admin sends message to all suppliers: " + message);
        channel.basicPublish(
                "admin_exchange",
                "suppliers",
                null,
                message.getBytes(StandardCharsets.UTF_8)
        );
    }

    public void sendMessageToAll(String message) throws IOException {
        System.out.println("Admin sends message to all: " + message);
        channel.basicPublish(
                "admin_exchange",
                "all",
                null,
                message.getBytes(StandardCharsets.UTF_8)
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
            System.out.println("Admin closed.");
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }
    }
}
