package com.order_manager.main.repository;

import java.util.List;
import com.order_manager.main.model.Order;

public interface OrderRepository {

    List<Order> findAll();

    Order findById(String id);

    void save(Order order);

    void update(Order order);

    void deleteById(String id);
}
