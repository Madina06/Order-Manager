package com.order_manager.main.view;

import java.util.List;
import javax.swing.JList;
import javax.swing.JTextField;
import com.order_manager.main.model.Order;

public interface OrderManagerView {

    void displayOrders(List<Order> orders);

    void showAllOrders(List<Order> orders);

    void showSearchedOrdersList(List<Order> orders);

    void showErrorMessage(String message);

    void orderAdded(Order order);

    void orderRemoved(Order order);

    JList<Order> getAllOrdersList();

    JList<Order> getSearchedOrdersList();

    JTextField getSearchCompanyTextField();

    JTextField getIdTextField();

    JTextField getCustomerTextField();

    JTextField getCompanyTextField();

    JTextField getProductTextField();

    JTextField getStatusTextField();

    JTextField getDescriptionTextField();

    void clearFormFields();
}
