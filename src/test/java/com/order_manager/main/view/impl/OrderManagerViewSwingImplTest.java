package com.order_manager.main.view.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import javax.swing.JTable;

import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.order_manager.main.controller.OrderController;
import com.order_manager.main.model.Order;

@RunWith(GUITestRunner.class)
public class OrderManagerViewSwingImplTest extends AssertJSwingJUnitTestCase {

    private FrameFixture window;
    private OrderManagerViewSwingImpl orderView;
    private AutoCloseable closeable;

    @Mock
    private OrderController orderController;

    @Before
    public void onSetUp() {
        closeable = MockitoAnnotations.openMocks(this);
        GuiActionRunner.execute(() -> {
            orderView = new OrderManagerViewSwingImpl(orderController);
            return orderView;
        });
        window = new FrameFixture(robot(), orderView);
        window.show();
    }

    @After
    public void onTearDown() throws Exception {
        closeable.close();
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

        when(orderController.getAllOrders()).thenReturn(orders);
        GuiActionRunner.execute(() -> orderView.displayOrders(orders));

        JTable table = orderView.getOrderTable();
        assertThat(table.getRowCount()).isEqualTo(2);
        assertThat(table.getValueAt(0, 1)).isEqualTo("Customer1");
        assertThat(table.getValueAt(1, 2)).isEqualTo("Company2");
    }

    @Test
    @GUITest
    public void testSearchByCompany() {
        List<Order> orders = Arrays.asList(
                new Order("1", "Customer1", "Company1", "Product1", "Status1", "Description1", null, null),
                new Order("2", "Customer2", "Company2", "Product2", "Status2", "Description2", null, null));

        when(orderController.getAllOrders()).thenReturn(orders);
        window.textBox("searchField").enterText("Company1");
        window.button(JButtonMatcher.withText("Search by Company")).click();

        JTable table = orderView.getOrderTable();
        assertThat(table.getRowCount()).isEqualTo(1);
        assertThat(table.getValueAt(0, 2)).isEqualTo("Company1");
    }

    @Test
    @GUITest
    public void testCreateOrderButtonShouldDelegateToController() {
        window.button(JButtonMatcher.withName("createButton")).click();

        window.dialog().textBox("idField").setText("3");
        window.dialog().textBox("customerField").setText("Customer3");
        window.dialog().textBox("companyField").setText("Company3");
        window.dialog().textBox("productField").setText("Product3");
        window.dialog().textBox("statusField").setText("Status3");
        window.dialog().textBox("descriptionField").setText("Description3");

        window.optionPane().button(JButtonMatcher.withText("OK")).click();

        assertThat(orderView.getOrderTable().getRowCount()).isEqualTo(0);
    }

    @Test
    @GUITest
    public void testDeleteOrderButton() {
        List<Order> orders = Arrays.asList(new Order("1", "Customer1", "Company1", "Product1", "Status1", "Description1", null, null));
        when(orderController.getAllOrders()).thenReturn(orders);
        GuiActionRunner.execute(() -> orderView.displayOrders(orders));

        window.table("orderTable").cell("Delete").click();
    }

    @Test
    @GUITest
    public void testSearchButtonWithoutCompanyInputShouldDisplayAllOrders() {
        List<Order> orders = Arrays.asList(
                new Order("1", "Customer1", "Company1", "Product1", "Status1", "Description1", null, null),
                new Order("2", "Customer2", "Company2", "Product2", "Status2", "Description2", null, null));

        when(orderController.getAllOrders()).thenReturn(orders);
        window.textBox("searchField").setText("");
        window.button(JButtonMatcher.withText("Search by Company")).click();

        JTable table = orderView.getOrderTable();
        assertThat(table.getRowCount()).isEqualTo(2);
        assertThat(table.getValueAt(0, 1)).isEqualTo("Customer1");
        assertThat(table.getValueAt(1, 1)).isEqualTo("Customer2");
    }

    @Test
    @GUITest
    public void testSearchButtonWhenCompanyNotFoundShouldShowDialog() {
        List<Order> orders = Arrays.asList(new Order("1", "Customer1", "Company1", "Product1", "Status1", "Description1", null, null));
        when(orderController.getAllOrders()).thenReturn(orders);

        window.textBox("searchField").enterText("NonExistentCompany");
        window.button(JButtonMatcher.withText("Search by Company")).click();

        window.optionPane().requireMessage("No orders found for company: NonExistentCompany");
    }

    @Test
    @GUITest
    public void testCreateOrderWithExistingIdShowsErrorMessage() {
        when(orderController.getOrderById("1")).thenReturn(new Order("1", "Existing Customer", "Existing Company", "Existing Product", "Existing Status", "Existing Description", null, null));

        window.button(JButtonMatcher.withName("createButton")).click();

        window.dialog().textBox("idField").setText("1");
        window.dialog().textBox("customerField").setText("New Customer");
        window.dialog().textBox("companyField").setText("New Company");
        window.dialog().textBox("productField").setText("New Product");
        window.dialog().textBox("statusField").setText("New Status");
        window.dialog().textBox("descriptionField").setText("New Description");

        window.optionPane().button(JButtonMatcher.withText("OK")).click();
        window.optionPane().requireMessage("Order with ID 1 already exists!");
    }

    @Test
    @GUITest
    public void testUpdateAndDeleteButtons() {
        List<Order> orders = Arrays.asList(new Order("1", "Customer1", "Company1", "Product1", "Status1", "Description1", "Test", "Test"));
        when(orderController.getAllOrders()).thenReturn(orders);
        when(orderController.getOrderById("1")).thenReturn(orders.get(0));

        GuiActionRunner.execute(() -> orderView.displayOrders(orders));
        window.table("orderTable").cell("Update").click();

        window.dialog().textBox("customerField").setText("Updated Customer");
        window.dialog().button(JButtonMatcher.withText("OK")).click();

        verify(orderController).updateOrder(new Order("1", "Updated Customer", "Company1", "Product1", "Status1", "Description1", "Test", "Test"));
    }

    @Test
    @GUITest
    public void testIsCellEditable() {
        assertTrue(orderView.getOrderTable().isCellEditable(0, 6));
        assertTrue(orderView.getOrderTable().isCellEditable(0, 7));
        assertFalse(orderView.getOrderTable().isCellEditable(0, 0));
        assertFalse(orderView.getOrderTable().isCellEditable(0, 1));
    }
}
