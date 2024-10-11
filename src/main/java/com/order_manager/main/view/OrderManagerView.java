package com.order_manager.main.view;

import com.order_manager.main.model.Order;

import java.util.List;

import javax.swing.JTable;

public interface OrderManagerView {
    void displayOrders(List<Order> orders);
    void handleButtonAction(String action, int row);
    void showCreateOrderForm();
    public JTable getOrderTable();
}
