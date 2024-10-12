package main.com.order_manager.main.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static java.util.Arrays.asList;

import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.testcontainers.containers.MongoDBContainer;

import com.order_manager.main.model.Order;
import com.order_manager.main.repository.OrderRepository;
import com.order_manager.main.repository.impl.OrderMongoRepository;
import com.order_manager.main.controller.OrderController;
import com.order_manager.main.view.OrderManagerView;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;

@RunWith(MockitoJUnitRunner.class)
public class OrderControllerIT {

    @Mock
    private OrderManagerView orderView;

    private OrderRepository orderRepository;

    private OrderController orderController;

    private MongoClient client;

    private static final String MANAGER_DB_NAME = "manager";
    private static final String ORDER_COLLECTION_NAME = "order";

    @ClassRule
    public static final MongoDBContainer mongo = new MongoDBContainer("mongo:4.4.3");

    @Before
    public void setup() {
        client = new MongoClient(new ServerAddress(mongo.getHost(), mongo.getMappedPort(27017)));
        orderRepository = new OrderMongoRepository(client, MANAGER_DB_NAME, ORDER_COLLECTION_NAME);
        MongoDatabase database = client.getDatabase(MANAGER_DB_NAME);
        database.drop();
        orderController = new OrderController(orderRepository);
    }

    @After
    public void tearDown() {
        client.close();
    }

    @Test
    public void testDisplayOrders() {
        Order order = new Order("1", "Customer A", "Company A", "Product A", "Pending", "Description A", "10-02-2023", "10-02-2023");
        orderRepository.save(order);
        orderController.getAllOrders(); 
        
        assertThat(orderRepository.findAll()).containsExactly(order); 
    }

    @Test
    public void testCreateOrder() {
        Order order = new Order("2", "Customer B", "Company B", "Product B", "New", "Description B", "10-02-2023", "10-02-2023");
        orderController.createOrder(order);
        
        assertThat(orderRepository.findById(order.getId())).isNotEqualTo(null);
    }

    @Test
    public void testDeleteOrder() {
        Order orderToDelete = new Order("3", "Customer C", "Company C", "Product C", "Completed", "Description C", "10-02-2023", "10-02-2023");
        orderRepository.save(orderToDelete);
        orderController.deleteOrder(orderToDelete.getId());
        
        assertThat(orderRepository.findById(orderToDelete.getId())).isEqualTo(null);
    }

    @Test
    public void testGetOrderById() {
        Order order = new Order("4", "Customer D", "Company D", "Product D", "In Progress", "Description D", "10-02-2023", "10-02-2023");
        orderRepository.save(order);
        
        Order foundOrder = orderController.getOrderById("4");
        
        assertThat(foundOrder).isEqualTo(order);
    }
}
