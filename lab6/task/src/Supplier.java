package src;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

public class Supplier {
    private final String name;
    private final List<String> equipmentList;
    private Channel channel;
    private Connection connection;

    public Supplier(String name, List<String> equipmentList) {
        this.name = name;
        this.equipmentList = equipmentList;
    }

    public void start() throws IOException, TimeoutException {
        System.out.println("Supplier " + name + " started.");

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");

        connection = factory.newConnection();
        channel = connection.createChannel();

        channel.exchangeDeclare("equipment_orders_exchange", BuiltinExchangeType.DIRECT);
        channel.exchangeDeclare("equipment_orders_logs_exchange", BuiltinExchangeType.FANOUT);
        channel.exchangeDeclare("admin_exchange", BuiltinExchangeType.TOPIC);

        String queueName = "equipment_orders_queue";
        channel.queueDeclare(queueName, false, false, false, null);

        for (String equipment : equipmentList) {
            String equipmentQueueName = equipment + "_queue";
            channel.queueDeclare(equipmentQueueName, false, false, false, null);
            channel.queueBind(equipmentQueueName, "equipment_orders_exchange", equipment);

            channel.basicConsume(equipmentQueueName, false, (consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), "UTF-8");

                String[] parts = message.split(":");

                String teamName = parts[0];
                String equipmentOrdered = parts[1];

                String orderId = UUID.randomUUID().toString().substring(0, 4);

                System.out.println("Supplier " + name + " processing order from team " +
                        teamName + ": " + equipmentOrdered + " (Order ID: " + orderId + ")");

                String logMessage = "Supplier " + name + " received order from " + teamName + " for " + equipmentOrdered;
                channel.basicPublish("equipment_orders_logs_exchange", "", null, logMessage.getBytes("UTF-8"));

                String confirmationMessage = "Order " + orderId + " for " + equipmentOrdered + " completed by supplier " + name;
                channel.basicPublish("", delivery.getProperties().getReplyTo(), null,
                        confirmationMessage.getBytes("UTF-8"));

                channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
            }, consumerTag -> {});
        }

        String adminQueueName = "admin_to_supplier_" + name;
        channel.queueDeclare(adminQueueName, false, false, false, null);
        channel.queueBind(adminQueueName, "admin_exchange", "suppliers");
        channel.queueBind(adminQueueName, "admin_exchange", "all");

        channel.basicConsume(adminQueueName, true, (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            System.out.println("[ Supplier " + name + " received admin message ]: " + message);
        }, consumerTag -> {});

        channel.basicQos(1);
    }

    public void close() {
        try {
            if (channel != null) {
                channel.close();
            }
            if (connection != null) {
                connection.close();
            }
            System.out.println("Supplier " + name + " closed.");
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }
    }
}
