package com.order_manager.main.repository.impl;

import static com.mongodb.client.model.Filters.eq;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.order_manager.main.model.Order;
import com.order_manager.main.repository.OrderRepository;

public class OrderMongoRepository implements OrderRepository {

    private final MongoCollection<Document> collection;

    public OrderMongoRepository(MongoClient mongoClient, String databaseName, String collectionName) {
        MongoDatabase database = mongoClient.getDatabase(databaseName);
        this.collection = database.getCollection(collectionName);
    }

    @Override
    public List<Order> findAll() {
        List<Order> orders = new ArrayList<>();
        for (Document doc : collection.find()) {
            orders.add(documentToOrder(doc));
        }
        return orders;
    }

    @Override
    public Order findById(String id) {
        Document doc = collection.find(eq("_id", id)).first();
        return doc != null ? documentToOrder(doc) : null;
    }

    @Override
    public void save(Order order) {
        collection.insertOne(orderToDocument(order));
    }

    @Override
    public void update(Order order) {
        collection.replaceOne(eq("_id", order.getId()), orderToDocument(order));
    }

    @Override
    public void deleteById(String id) {
        collection.deleteOne(eq("_id", id));
    }

    private Order documentToOrder(Document doc) {
        return new Order(
            doc.getString("_id"),
            doc.getString("customer"),
            doc.getString("company"),
            doc.getString("product"),
            doc.getString("status"),
            doc.getString("description"),
            doc.getString("createdDate"),
            doc.getString("updatedDate")
        );
    }

    private Document orderToDocument(Order order) {
        return new Document("_id", order.getId())
            .append("customer", order.getCustomer())
            .append("company", order.getCompany())
            .append("product", order.getProduct())
            .append("status", order.getStatus())
            .append("description", order.getDescription())
            .append("createdDate", order.getCreatedDate())
            .append("updatedDate", order.getUpdatedDate());
    }
}
