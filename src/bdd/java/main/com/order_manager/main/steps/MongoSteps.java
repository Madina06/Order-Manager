package main.com.order_manager.main.steps;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.client.model.Filters;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import main.com.order_manager.main.OrderAppBDD;

public class MongoSteps {

    static final String DB_NAME = "manager";
    static final String COLLECTION_NAME = "order";

    static final String ORDER_FIXTURE_1_ID = "1";
    static final String ORDER_FIXTURE_1_CUSTOMER = "Customer A";
    static final String ORDER_FIXTURE_1_COMPANY = "Company A";
    static final String ORDER_FIXTURE_2_ID = "2";
    static final String ORDER_FIXTURE_2_CUSTOMER = "Customer B";
    static final String ORDER_FIXTURE_2_COMPANY = "Company B";

    private MongoClient mongoClient;

    @Before
    public void setUp() {
        mongoClient = new MongoClient("localhost", OrderAppBDD.mongoPort);
        mongoClient.getDatabase(DB_NAME).drop();
    }

    @After
    public void tearDown() {
        mongoClient.close();
    }

    @Given("The database contains a few orders")
    public void the_database_contains_a_few_orders() {
        addTestOrderToDatabase(ORDER_FIXTURE_1_ID, ORDER_FIXTURE_1_CUSTOMER, ORDER_FIXTURE_1_COMPANY);
        addTestOrderToDatabase(ORDER_FIXTURE_2_ID, ORDER_FIXTURE_2_CUSTOMER, ORDER_FIXTURE_2_COMPANY);
    }

    @Given("The order is in the meantime removed from the database")
    public void the_order_is_in_the_meantime_removed_from_the_database() {
        mongoClient
            .getDatabase(DB_NAME)
            .getCollection(COLLECTION_NAME)
            .deleteOne(Filters.eq("id", ORDER_FIXTURE_1_ID));
    }

    private void addTestOrderToDatabase(String id, String customer, String company) {
        mongoClient
            .getDatabase(DB_NAME)
            .getCollection(COLLECTION_NAME)
            .insertOne(
                new Document()
                    .append("_id", id)
                    .append("customer", customer)
                    .append("company", company)
                    .append("product", "Product A")
                    .append("status", "Pending")
                    .append("description", "Description A")
                    .append("createdDate", "10-02-2023")
                    .append("updatedDate", "10-02-2023")
            );
    }
}
