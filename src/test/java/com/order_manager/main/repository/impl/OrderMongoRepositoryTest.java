package com.order_manager.main.repository.impl;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.bson.Document;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import com.order_manager.main.model.Order;

import de.bwaldvogel.mongo.MongoServer;
import de.bwaldvogel.mongo.backend.memory.MemoryBackend;

public class OrderMongoRepositoryTest {

    private static MongoServer server;
    private static InetSocketAddress serverAddress;
    private MongoClient client;
    private OrderMongoRepository orderRepository;
    private MongoCollection<Document> orderCollection;

    private static final String DATABASE_NAME = "order_manager_db";
    private static final String COLLECTION_NAME = "orders";

    @BeforeClass
    public static void setupServer() {
        server = new MongoServer(new MemoryBackend());
        serverAddress = server.bind();
    }

    @AfterClass
    public static void shutdownServer() {
        server.shutdown();
    }

    @Before
    public void setup() {
        client = new MongoClient(new ServerAddress(serverAddress));
        orderRepository = new OrderMongoRepository(client, DATABASE_NAME, COLLECTION_NAME);
        MongoDatabase database = client.getDatabase(DATABASE_NAME);
        database.drop();
        orderCollection = database.getCollection(COLLECTION_NAME);
    }

    @After
    public void tearDown() {
        client.close();
    }

    @Test
    public void testFindAllWhenDatabaseIsEmpty() {
        assertThat(orderRepository.findAll()).isEmpty();
    }

    @Test
    public void testFindAllWhenDatabaseIsNotEmpty() {
        addTestOrderToDatabase("1", "customer1", "company1", "product1", "status1", "description1");
        addTestOrderToDatabase("2", "customer2", "company2", "product2", "status2", "description2");

        assertThat(orderRepository.findAll())
            .containsExactly(
                new Order("1", "customer1", "company1", "product1", "status1", "description1", null, null),
                new Order("2", "customer2", "company2", "product2", "status2", "description2", null, null));
    }

    @Test
    public void testFindByIdNotFound() {
        assertThat(orderRepository.findById("1")).isNull();
    }

    @Test
    public void testFindByIdFound() {
        addTestOrderToDatabase("1", "customer1", "company1", "product1", "status1", "description1");
        assertThat(orderRepository.findById("1"))
            .isEqualTo(new Order("1", "customer1", "company1", "product1", "status1", "description1", null, null));
    }

    @Test
    public void testSave() {
        Order order = new Order("1", "customer1", "company1", "product1", "status1", "description1", "2024-10-10", "2024-10-11");
        orderRepository.save(order);
        assertThat(readAllOrdersFromDatabase()).containsExactly(order);
    }

    @Test
    public void testDelete() {
        addTestOrderToDatabase("1", "customer1", "company1", "product1", "status1", "description1");
        orderRepository.deleteById("1");
        assertThat(readAllOrdersFromDatabase()).isEmpty();
    }
    
    @Test
    public void testUpdate() {
        addTestOrderToDatabase("1", "customer1", "company1", "product1", "status1", "description1");
        Order updatedOrder = new Order("1", "customer1-updated", "company1-updated", "product1-updated", "status1-updated", "description1-updated", "2024-10-12", "2024-10-13");
        orderRepository.update(updatedOrder);
        assertThat(readAllOrdersFromDatabase())
            .containsExactly(updatedOrder);
    }


    private void addTestOrderToDatabase(String id, String customer, String company, String product, String status, String description) {
        orderCollection.insertOne(
            new Document()
                .append("_id", id)
                .append("customer", customer)
                .append("company", company)
                .append("product", product)
                .append("status", status)
                .append("description", description));
    }

    private List<Order> readAllOrdersFromDatabase() {
        return StreamSupport.stream(orderCollection.find().spliterator(), false)
            .map(d -> new Order(
                ""+d.get("_id"), 
                ""+d.get("customer"), 
                ""+d.get("company"), 
                ""+d.get("product"), 
                ""+d.get("status"), 
                ""+d.get("description"),
                ""+d.get("createdDate"), 
                ""+d.get("updatedDate")))
            .collect(Collectors.toList());
    }
}
