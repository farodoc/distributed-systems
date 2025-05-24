package src;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeoutException;

public class Main {

    public static void main(String[] args) throws IOException, TimeoutException, InterruptedException {
        Admin admin = new Admin();
        admin.start();

        Supplier supplier1 = new Supplier("S1", List.of("oxygen", "shoes"));
        supplier1.start();

        Supplier supplier2 = new Supplier("S2", List.of("oxygen", "backpack"));
        supplier2.start();

        Team team1 = new Team("T1");
        team1.start();

        Team team2 = new Team("T2");
        team2.start();

        Thread.sleep(1000); // Wait for the teams and suppliers to start

        List<String> orders = List.of("oxygen", "oxygen", "shoes", "shoes", "backpack", "backpack");
//        List<String> orders = List.of("oxygen", "shoes", "backpack", "backpack");
//        List<String> orders = List.of("backpack", "backpack");

        for (String order : orders) {
            team1.order(order);
            Thread.sleep(500);
        }

        Thread.sleep(1000);
        System.out.println("===================================");
        admin.sendMessageToTeams("Hello teams, this is an admin message.");
        Thread.sleep(3000);
        System.out.println("===================================");
        admin.sendMessageToSuppliers("Hello suppliers, this is an admin message.");
        Thread.sleep(3000);
        System.out.println("===================================");
        admin.sendMessageToAll("Hello everyone, this is an admin message.");
        Thread.sleep(1000);
        System.out.println("===================================");

        admin.close();
        supplier1.close();
        supplier2.close();
        team1.close();
        team2.close();
    }
}
