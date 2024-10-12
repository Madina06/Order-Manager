package com.order_manager.main;

import java.awt.EventQueue;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import com.order_manager.main.controller.OrderController;
import com.order_manager.main.repository.impl.OrderMongoRepository;
import com.order_manager.main.view.impl.OrderManagerViewSwingImpl;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;


@Command(mixinStandardHelpOptions = true)
public class App implements Callable<Void> {

    @Option(names = { "--mongo-host" }, description = "MongoDB host address")
    private String mongoHost = "localhost";

    @Option(names = { "--mongo-port" }, description = "MongoDB host port")
    private int mongoPort = 27017;

    @Option(names = { "--db-name" }, description = "Database name")
    private String databaseName = "manager";

    @Option(names = { "--db-collection" }, description = "Collection name")
    private String collectionName = "order";

    public static void main(String[] args) {
        new CommandLine(new App()).execute(args);
    }

    @Override
    public Void call() throws Exception {
        EventQueue.invokeLater(() -> {
            try {
                OrderMongoRepository orderRepository = new OrderMongoRepository(
                    new MongoClient(new ServerAddress(mongoHost, mongoPort)), databaseName, collectionName);
                OrderController orderController = new OrderController(orderRepository);
                OrderManagerViewSwingImpl orderView = new OrderManagerViewSwingImpl(orderController);
                orderView.setVisible(true);
            } catch (Exception e) {
                Logger.getLogger(getClass().getName())
                    .log(Level.SEVERE, "Exception", e);
            }
        });
        return null;
    }
}
