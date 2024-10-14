package com.order_manager.main.view.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JTextField;

import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.core.matcher.JLabelMatcher;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.fixture.JButtonFixture;
import org.assertj.swing.fixture.JTextComponentFixture;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.order_manager.main.controller.OrderController;
import com.order_manager.main.model.Order;

@RunWith(GUITestRunner.class)
public class OrderManagerViewSwingImplTest extends AssertJSwingJUnitTestCase {

	private FrameFixture window;

	private OrderManagerViewSwingImpl orderManagerView;

	@Mock
	private OrderController orderController;

	private AutoCloseable closeable;

	@Override
	protected void onSetUp() {
		closeable = MockitoAnnotations.openMocks(this);
		GuiActionRunner.execute(() -> {
			orderManagerView = new OrderManagerViewSwingImpl(orderController);
			return orderManagerView;
		});

		window = new FrameFixture(robot(), orderManagerView);
		window.show();
	}

	@Override
	protected void onTearDown() throws Exception {
		closeable.close();
	}

	@Test
	@GUITest
	public void testControlsInitialStates() {
		window.label(JLabelMatcher.withText("ID:"));
		window.textBox("idTextField").requireEnabled();
		window.label(JLabelMatcher.withText("Customer:"));
		window.textBox("customerTextField").requireEnabled();
		window.label(JLabelMatcher.withText("Company:"));
		window.textBox("companyTextField").requireEnabled();
		window.label(JLabelMatcher.withText("Product:"));
		window.textBox("productTextField").requireEnabled();
		window.label(JLabelMatcher.withText("Status:"));
		window.textBox("statusTextField").requireEnabled();
		window.label(JLabelMatcher.withText("Description:"));
		window.textBox("descriptionTextField").requireEnabled();
		window.button(JButtonMatcher.withText("Add Order")).requireDisabled();
		window.list("listAllOrders");
		window.button(JButtonMatcher.withText("Delete Selected")).requireDisabled();
		window.label("lblErrorMessage").requireText(" ");
	}

	@Test
	public void testWhenAllFieldsAreNonEmptyThenAddButtonShouldBeEnabled() {
		window.textBox("idTextField").enterText("123");
		window.textBox("customerTextField").enterText("CustomerName");
		window.textBox("companyTextField").enterText("CompanyName");
		window.textBox("productTextField").enterText("ProductName");
		window.textBox("statusTextField").enterText("Shipped");
		window.textBox("descriptionTextField").enterText("Description");
		window.button(JButtonMatcher.withText("Add Order")).requireEnabled();
	}

	@Test
	public void testWhenAnyFieldIsEmptyThenAddButtonShouldBeDisabled() {
		JTextComponentFixture idTextBox = window.textBox("idTextField");
		JTextComponentFixture customerTextBox = window.textBox("customerTextField");
		JTextComponentFixture companyTextBox = window.textBox("companyTextField");
		JTextComponentFixture productTextBox = window.textBox("productTextField");
		JTextComponentFixture statusTextBox = window.textBox("statusTextField");
		JTextComponentFixture descriptionTextBox = window.textBox("descriptionTextField");

		idTextBox.setText("123");
		customerTextBox.setText("CustomerName");
		companyTextBox.setText("CompanyName");
		productTextBox.setText("");
		window.button(JButtonMatcher.withText("Add Order")).requireDisabled();

		resetTextFields(idTextBox, customerTextBox, companyTextBox, productTextBox, statusTextBox, descriptionTextBox);
		idTextBox.setText("");
		customerTextBox.setText("CustomerName");
		window.button(JButtonMatcher.withText("Add Order")).requireDisabled();
	}

	private void resetTextFields(JTextComponentFixture... textFields) {
		for (JTextComponentFixture textField : textFields) {
			textField.setText("");
		}
	}

	@Test
	public void testDeleteButtonShouldBeEnabledOnlyWhenAnOrderIsSelected() {
		Order order = new Order("123", "CustomerName", "CompanyName", "ProductName", "Shipped", "Description", null,
				null);
		GuiActionRunner.execute(() -> {
			DefaultListModel<Order> listAllOrdersModel = orderManagerView.listAllOrdersModel;
			listAllOrdersModel.addElement(order);
		});

		window.list("listAllOrders").selectItem(0);
		JButtonFixture deleteButton = window.button(JButtonMatcher.withText("Delete Selected"));
		deleteButton.requireEnabled();

		window.list("listAllOrders").clearSelection();
		deleteButton.requireDisabled();
	}

	@Test
	public void testAddButtonShouldDelegateToOrderControllerNewOrder() {
		window.textBox("idTextField").enterText("123");
		window.textBox("customerTextField").enterText("CustomerName");
		window.textBox("companyTextField").enterText("CompanyName");
		window.textBox("productTextField").enterText("ProductName");
		window.textBox("statusTextField").enterText("Shipped");
		window.textBox("descriptionTextField").enterText("Description");

		window.button(JButtonMatcher.withText("Add Order")).click();

		verify(orderController).createOrder(
				new Order("123", "CustomerName", "CompanyName", "ProductName", "Shipped", "Description", null, null));
	}

	@Test
	public void testDeleteButtonShouldDelegateToOrderControllerDeleteOrder() {
		Order order = new Order("123", "CustomerName", "CompanyName", "ProductName", "Shipped", "Description", null,
				null);
		GuiActionRunner.execute(() -> orderManagerView.listAllOrdersModel.addElement(order));
		window.list("listAllOrders").selectItem(0);
		window.button(JButtonMatcher.withText("Delete Selected")).click();
	}

	@Test
	public void testSearchButtonShouldDisplayFilteredOrders() {
		Order order1 = new Order("123", "CustomerName", "CompanyA", "ProductA", "Shipped", "Description", null, null);
		Order order2 = new Order("124", "CustomerName", "CompanyB", "ProductB", "Pending", "Description", null, null);

		GuiActionRunner.execute(() -> {
			orderManagerView.listAllOrdersModel.addElement(order1);
			orderManagerView.listAllOrdersModel.addElement(order2);
		});

		window.textBox("searchCompanyTextField").setText("CompanyA");
		window.button(JButtonMatcher.withText("Search")).click();

		String[] listContents = window.list("listSearchedOrders").contents();
	}

	@Test
	public void testClearFormFieldsAfterOrderAdded() {
		GuiActionRunner.execute(() -> orderManagerView.clearFormFields());

		window.textBox("idTextField").requireText("");
		window.textBox("customerTextField").requireText("");
		window.textBox("companyTextField").requireText("");
		window.textBox("productTextField").requireText("");
		window.textBox("statusTextField").requireText("");
		window.textBox("descriptionTextField").requireText("");
		window.button(JButtonMatcher.withText("Add Order")).requireDisabled();
	}

	@Test
	public void testShowErrorMessageShouldDisplayErrorMessage() {
		GuiActionRunner.execute(() -> orderManagerView.showErrorMessage("Error occurred"));
		window.label("lblErrorMessage").requireText("Error occurred");
	}

	@Test
	public void testOrderAddedShouldAddTheOrderToList() {
		Order order = new Order("123", "CustomerName", "CompanyName", "ProductName", "Shipped", "Description", null,
				null);
		GuiActionRunner.execute(() -> orderManagerView.orderAdded(order));

		String[] listContents = window.list("listAllOrders").contents();
		assertThat(listContents).containsExactly(
				"<html><b>ID:</b> 123 &nbsp;&nbsp; <b>Customer:</b> CustomerName &nbsp;&nbsp; <b>Company:</b> CompanyName &nbsp;&nbsp; <b>Product:</b> ProductName &nbsp;&nbsp; <b>Status:</b> Shipped</html>");
	}

	@Test
	public void testShowAllOrdersShouldDisplayOrders() {
		List<Order> orders = Arrays.asList(
				new Order("123", "Customer1", "Company1", "Product1", "Shipped", "Description1", null, null),
				new Order("124", "Customer2", "Company2", "Product2", "Pending", "Description2", null, null));

		GuiActionRunner.execute(() -> orderManagerView.showAllOrders(orders));

		String[] listContents = window.list("listAllOrders").contents();
		assertThat(listContents).containsExactly(
				"<html><b>ID:</b> 123 &nbsp;&nbsp; <b>Customer:</b> Customer1 &nbsp;&nbsp; <b>Company:</b> Company1 &nbsp;&nbsp; <b>Product:</b> Product1 &nbsp;&nbsp; <b>Status:</b> Shipped</html>",
				"<html><b>ID:</b> 124 &nbsp;&nbsp; <b>Customer:</b> Customer2 &nbsp;&nbsp; <b>Company:</b> Company2 &nbsp;&nbsp; <b>Product:</b> Product2 &nbsp;&nbsp; <b>Status:</b> Pending</html>");
	}

	@Test
	public void testShowSearchedOrdersListShouldDisplaySearchedOrders() {
		List<Order> orders = Arrays
				.asList(new Order("125", "Customer3", "Company3", "Product3", "Delivered", "Description3", null, null));

		GuiActionRunner.execute(() -> orderManagerView.showSearchedOrdersList(orders));

		String[] listContents = window.list("listSearchedOrders").contents();
		assertThat(listContents).containsExactly(
				"<html><b>ID:</b> 125 &nbsp;&nbsp; <b>Customer:</b> Customer3 &nbsp;&nbsp; <b>Company:</b> Company3 &nbsp;&nbsp; <b>Product:</b> Product3 &nbsp;&nbsp; <b>Status:</b> Delivered</html>");
	}

	@Test
	public void testOrderRemovedShouldRemoveOrderFromLists() {
		Order order = new Order("123", "CustomerName", "CompanyName", "ProductName", "Shipped", "Description", null,
				null);

		GuiActionRunner.execute(() -> {
			orderManagerView.listAllOrdersModel.addElement(order);
			orderManagerView.listSearchedOrdersModel.addElement(order);
		});

		GuiActionRunner.execute(() -> orderManagerView.orderRemoved(order));

		String[] allOrdersList = window.list("listAllOrders").contents();
		String[] searchedOrdersList = window.list("listSearchedOrders").contents();

		assertThat(allOrdersList).isEmpty();
		assertThat(searchedOrdersList).isEmpty();
		window.label("lblErrorMessage").requireText("Order removed successfully.");
	}

	@Test
	public void testGetAllOrdersListShouldReturnListAllOrders() {
		JList<Order> allOrdersList = orderManagerView.getAllOrdersList();
		assertThat(allOrdersList).isNotNull();
	}

	@Test
	public void testGetSearchedOrdersListShouldReturnListSearchedOrders() {
		JList<Order> searchedOrdersList = orderManagerView.getSearchedOrdersList();
		assertThat(searchedOrdersList).isNotNull();
	}

	@Test
	public void testGetSearchCompanyTextFieldShouldReturnSearchCompanyTextField() {
		JTextField searchCompanyField = orderManagerView.getSearchCompanyTextField();
		assertThat(searchCompanyField).isNotNull();
	}

	@Test
	public void testGetIdTextFieldShouldReturnIdTextField() {
		JTextField idField = orderManagerView.getIdTextField();
		assertThat(idField).isNotNull();
	}

	@Test
	public void testGetCustomerTextFieldShouldReturnCustomerTextField() {
		JTextField customerField = orderManagerView.getCustomerTextField();
		assertThat(customerField).isNotNull();
	}

	@Test
	public void testGetCompanyTextFieldShouldReturnCompanyTextField() {
		JTextField companyField = orderManagerView.getCompanyTextField();
		assertThat(companyField).isNotNull();
	}

	@Test
	public void testGetProductTextFieldShouldReturnProductTextField() {
		JTextField productField = orderManagerView.getProductTextField();
		assertThat(productField).isNotNull();
	}

	@Test
	public void testGetStatusTextFieldShouldReturnStatusTextField() {
		JTextField statusField = orderManagerView.getStatusTextField();
		assertThat(statusField).isNotNull();
	}

	@Test
	public void testGetDescriptionTextFieldShouldReturnDescriptionTextField() {
		JTextField descriptionField = orderManagerView.getDescriptionTextField();
		assertThat(descriptionField).isNotNull();
	}

	@Test
	public void testSearchOrdersShouldFilterOrdersByCompany() {
		List<Order> orders = Arrays.asList(
				new Order("123", "CustomerName1", "CompanyName1", "Product1", "Shipped", "Description1", null, null),
				new Order("124", "CustomerName2", "CompanyName2", "Product2", "Shipped", "Description2", null, null));

		when(orderController.getAllOrders()).thenReturn(orders);

		window.textBox("searchCompanyTextField").enterText("CompanyName1");
		window.button(JButtonMatcher.withText("Search")).click();

		String[] listContents = window.list("listSearchedOrders").contents();
		assertThat(listContents).containsExactly(
				"<html><b>ID:</b> 123 &nbsp;&nbsp; <b>Customer:</b> CustomerName1 &nbsp;&nbsp; <b>Company:</b> CompanyName1 &nbsp;&nbsp; <b>Product:</b> Product1 &nbsp;&nbsp; <b>Status:</b> Shipped</html>");
	}

	@Test
	public void testDeleteSelectedOrderShouldDeleteOrderAfterConfirmation() {
		Order order = new Order("123", "CustomerName", "CompanyName", "ProductName", "Shipped", "Description", null,
				null);

		GuiActionRunner.execute(() -> {
			orderManagerView.listAllOrdersModel.addElement(order);
			orderManagerView.listSearchedOrdersModel.addElement(order);
		});

		window.list("listAllOrders").selectItem(0);

		window.button("btnDelete").click();
	}

	@Test
	public void testDeleteSelectedOrderShouldNotDeleteOrderAfterNoConfirmation() {
		Order order = new Order("123", "CustomerName", "CompanyName", "ProductName", "Shipped", "Description", null,
				null);

		GuiActionRunner.execute(() -> {
			orderManagerView.listAllOrdersModel.addElement(order);
			orderManagerView.listSearchedOrdersModel.addElement(order);
		});

		window.list("listAllOrders").selectItem(0);

		window.button("btnDelete").click();

		window.dialog().optionPane().noButton().click();
	}
	
	
	@Test
	public void testSearchButtonShouldBeEnabledWhenCompanyFieldIsNotEmpty() {
	    window.textBox("searchCompanyTextField").enterText("SomeCompany");
	    window.button(JButtonMatcher.withText("Search")).requireEnabled();
	}
	
	@Test
	public void testWhenOrderIsSelectedThenDeleteButtonShouldBeEnabled() {
	    GuiActionRunner.execute(() -> {
	    	orderManagerView.listAllOrdersModel.addElement(new Order("123", "CustomerName", "CompanyName", "ProductName", "Shipped", "Description", null, null));
	    });

	    window.list("listAllOrders").selectItem(0);
	    window.button(JButtonMatcher.withText("Delete Selected")).requireEnabled();
	}

	@Test
	public void testDeleteButtonShouldCallDeleteOrderOnControllerWhenOrderIsSelected() {
	    Order order = new Order("123", "CustomerName", "CompanyName", "ProductName", "Shipped", "Description", null, null);
	    GuiActionRunner.execute(() -> orderManagerView.listAllOrdersModel.addElement(order));

	    window.list("listAllOrders").selectItem(0);
	    window.button(JButtonMatcher.withText("Delete Selected")).click();

	    window.dialog().optionPane().yesButton().click();
	    verify(orderController).deleteOrder("123");
	}
	
	@Test
	public void testAddButtonEnabledWhenAllFieldsAreFilled() {
	    window.button(JButtonMatcher.withText("Add Order")).requireDisabled();

	    window.textBox("idTextField").enterText("123");
	    window.button(JButtonMatcher.withText("Add Order")).requireDisabled();

	    window.textBox("customerTextField").enterText("CustomerName");
	    window.button(JButtonMatcher.withText("Add Order")).requireDisabled();

	    window.textBox("companyTextField").enterText("SomeCompany");
	    window.button(JButtonMatcher.withText("Add Order")).requireDisabled();

	    window.textBox("productTextField").enterText("ProductName");
	    window.button(JButtonMatcher.withText("Add Order")).requireDisabled();

	    window.textBox("statusTextField").enterText("Shipped");
	    window.button(JButtonMatcher.withText("Add Order")).requireDisabled();
	    
	    window.textBox("idTextField").setText("");
	    window.textBox("descriptionTextField").enterText("Order description");
	    window.button(JButtonMatcher.withText("Add Order")).requireDisabled();

	    window.textBox("idTextField").enterText("123");
	    window.button(JButtonMatcher.withText("Add Order")).requireEnabled();
	}

}
