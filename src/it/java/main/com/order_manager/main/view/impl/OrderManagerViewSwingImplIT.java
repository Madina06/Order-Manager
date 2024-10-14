package main.com.order_manager.main.view.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.List;

import javax.swing.JList;

import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import com.order_manager.main.controller.OrderController;
import com.order_manager.main.model.Order;
import com.order_manager.main.repository.impl.OrderMongoRepository;
import com.order_manager.main.view.impl.OrderManagerViewSwingImpl;

import de.bwaldvogel.mongo.MongoServer;
import de.bwaldvogel.mongo.backend.memory.MemoryBackend;

@RunWith(GUITestRunner.class)
public class OrderManagerViewSwingImplIT extends AssertJSwingJUnitTestCase {

	private static MongoServer server;
	private static InetSocketAddress serverAddress;

	private MongoClient mongoClient;
	private FrameFixture window;
	private OrderManagerViewSwingImpl orderView;
	private OrderController orderController;
	private OrderMongoRepository orderRepository;

	private static final String MANAGER_DB_NAME = "manager";
	private static final String ORDER_COLLECTION_NAME = "order";

	@BeforeClass
	public static void setupServer() {
		server = new MongoServer(new MemoryBackend());
		serverAddress = server.bind();
	}

	@AfterClass
	public static void shutdownServer() {
		server.shutdown();
	}

	@Override
	protected void onSetUp() {
		mongoClient = new MongoClient(new ServerAddress(serverAddress));
		orderRepository = new OrderMongoRepository(mongoClient, MANAGER_DB_NAME, ORDER_COLLECTION_NAME);

		GuiActionRunner.execute(() -> {
			orderController = new OrderController(orderRepository);
			orderView = new OrderManagerViewSwingImpl(orderController);
			return orderView;
		});
		window = new FrameFixture(robot(), orderView);
		window.show();
	}

	@Override
	protected void onTearDown() {
		mongoClient.close();
	}

	@Test
	@GUITest
	public void testInitialControlsState() {
		window.textBox("searchCompanyTextField").requireEnabled();
		window.list("listAllOrders").requireVisible();
		window.button(JButtonMatcher.withText("Search")).requireDisabled();
		window.button(JButtonMatcher.withText("Add Order")).requireDisabled();
	}

	@Test
	@GUITest
	public void testDisplayOrders() {
		List<Order> orders = Arrays.asList(
				new Order("1", "Customer1", "Company1", "Product1", "Status1", "Description1", null, null),
				new Order("2", "Customer2", "Company2", "Product2", "Status2", "Description2", null, null));

		GuiActionRunner.execute(() -> orderView.displayOrders(orders));

		JList<Order> list = orderView.getAllOrdersList();
		assertThat(window.list("listAllOrders").contents()).hasSize(2);
		assertThat(list.getModel().getElementAt(0).getCustomer()).isEqualTo("Customer1");
		assertThat(list.getModel().getElementAt(1).getCompany()).isEqualTo("Company2");
	}

	@Test
	@GUITest
	public void testAddOrderButtonShouldBeEnabledWhenFieldsAreFilled() {
		window.textBox("idTextField").enterText("123");
		window.textBox("customerTextField").enterText("CustomerName");
		window.textBox("companyTextField").enterText("CompanyName");
		window.textBox("productTextField").enterText("ProductName");
		window.textBox("statusTextField").enterText("Shipped");
		window.textBox("descriptionTextField").enterText("Description");

		// Check if the Add button becomes enabled when all fields are filled
		window.button(JButtonMatcher.withText("Add Order")).requireEnabled();
	}

	@Test
	@GUITest
	public void testAddOrderButtonShouldDelegateToController() {
		window.textBox("idTextField").enterText("123");
		window.textBox("customerTextField").enterText("CustomerName");
		window.textBox("companyTextField").enterText("CompanyName");
		window.textBox("productTextField").enterText("ProductName");
		window.textBox("statusTextField").enterText("Shipped");
		window.textBox("descriptionTextField").enterText("Description");

		window.button(JButtonMatcher.withText("Add Order")).click();
	}

	@Test
	@GUITest
	public void testDeleteSelectedOrder() {
		List<Order> orders = Arrays
				.asList(new Order("1", "Customer1", "Company1", "Product1", "Shipped", "Description", null, null));

		GuiActionRunner.execute(() -> orderView.displayOrders(orders));

		window.list("listAllOrders").selectItem(0);
		window.button(JButtonMatcher.withText("Delete Selected")).click();

		window.optionPane().requireMessage("Are you sure you want to delete Order ID: 1?");
		window.optionPane().yesButton().click();
	}

	@Test
	@GUITest
	public void testSearchOrdersWhenCompanyExists() {
		List<Order> orders = Arrays.asList(
				new Order("1", "Customer1", "Company1", "Product1", "Status1", "Description1", null, null),
				new Order("2", "Customer2", "Company1", "Product2", "Status2", "Description2", null, null));

		GuiActionRunner.execute(() -> orderView.displayOrders(orders));

		window.textBox("searchCompanyTextField").enterText("Company1");
		window.button(JButtonMatcher.withText("Search")).click();
	}

	@Test
	@GUITest
	public void testSearchOrdersWhenCompanyDoesNotExist() {
		List<Order> orders = Arrays
				.asList(new Order("1", "Customer1", "Company1", "Product1", "Status1", "Description1", null, null));

		GuiActionRunner.execute(() -> orderView.displayOrders(orders));

		window.textBox("searchCompanyTextField").enterText("NonExistentCompany");
		window.button(JButtonMatcher.withText("Search")).click();

		assertThat(window.list("listSearchedOrders").contents()).isEmpty();
	}
}
