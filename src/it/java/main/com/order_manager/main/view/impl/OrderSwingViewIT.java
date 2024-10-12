package main.com.order_manager.main.view.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.List;

import javax.swing.JTable;

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
public class OrderSwingViewIT extends AssertJSwingJUnitTestCase {

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
		window.textBox("searchField").requireEnabled();
		window.table("orderTable").requireVisible();
		window.button(JButtonMatcher.withText("Search by Company")).requireEnabled();
		window.button(JButtonMatcher.withText("Create Order")).requireEnabled();
	}

	@Test
	@GUITest
	public void testDisplayOrders() {
		List<Order> orders = Arrays.asList(
				new Order("1", "Customer1", "Company1", "Product1", "Status1", "Description1", null, null),
				new Order("2", "Customer2", "Company2", "Product2", "Status2", "Description2", null, null));

		GuiActionRunner.execute(() -> orderView.displayOrders(orders));

		JTable table = orderView.getOrderTable();
		assertThat(window.table("orderTable").rowCount()).isEqualTo(2);
		assertThat(table.getValueAt(0, 1)).isEqualTo("Customer1");
		assertThat(table.getValueAt(1, 2)).isEqualTo("Company2");
	}

	@Test
	@GUITest
	public void testCreateOrderButtonShouldDelegateToController() {
		window.button(JButtonMatcher.withText("Create Order")).click();
		window.dialog().textBox("idField").enterText("3");
		window.dialog().textBox("customerField").enterText("Customer3");
		window.dialog().textBox("companyField").enterText("Company3");
		window.dialog().textBox("productField").enterText("Product3");
		window.dialog().textBox("statusField").enterText("Status3");
		window.dialog().textBox("descriptionField").enterText("Description3");
		window.dialog().button(JButtonMatcher.withText("OK")).click();
	}

	@Test
	@GUITest
	public void testSearchButtonWhenCompanyNotFoundShouldShowDialog() {
		List<Order> orders = Arrays
				.asList(new Order("1", "Customer1", "Company1", "Product1", "Status1", "Description1", null, null));

		window.textBox("searchField").enterText("NonExistentCompany");
		window.button(JButtonMatcher.withText("Search by Company")).click();

		window.optionPane().requireMessage("No orders found for company: NonExistentCompany");
	}
}
