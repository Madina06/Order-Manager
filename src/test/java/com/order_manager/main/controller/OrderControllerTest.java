package com.order_manager.main.controller;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.order_manager.main.model.Order;
import com.order_manager.main.repository.OrderRepository;

public class OrderControllerTest {

    private OrderController orderController;
    private OrderRepository orderRepository;

    @Before
    public void setUp() {
        orderRepository = Mockito.mock(OrderRepository.class);
        orderController = new OrderController(orderRepository);
    }

    @Test
    public void testGetAllOrders() {
        Order order1 = new Order("1", "customer1", "company1", "product1", "status1", "description1", "2024-10-10", "2024-10-11");
        Order order2 = new Order("2", "customer2", "company2", "product2", "status2", "description2", "2024-10-12", "2024-10-13");

        when(orderRepository.findAll()).thenReturn(Arrays.asList(order1, order2));

        List<Order> orders = orderController.getAllOrders();

        assertThat(orders).containsExactly(order1, order2);
    }

    @Test
    public void testGetOrderById() {
        Order order = new Order("1", "customer1", "company1", "product1", "status1", "description1", "2024-10-10", "2024-10-11");

        when(orderRepository.findById("1")).thenReturn(order);

        Order foundOrder = orderController.getOrderById("1");

        assertThat(foundOrder).isEqualTo(order);
    }

    @Test
    public void testCreateOrder() {
        Order order = new Order("1", "customer1", "company1", "product1", "status1", "description1", "2024-10-10", "2024-10-11");

        orderController.createOrder(order);

        verify(orderRepository).save(order);
    }

    @Test
    public void testUpdateOrder() {
        Order order = new Order("1", "customer1", "company1", "product1", "status1", "description1", "2024-10-10", "2024-10-11");

        orderController.updateOrder(order);

        verify(orderRepository).update(order);
    }

    @Test
    public void testDeleteOrder() {
        orderController.deleteOrder("1");

        verify(orderRepository).deleteById("1");
    }
}
