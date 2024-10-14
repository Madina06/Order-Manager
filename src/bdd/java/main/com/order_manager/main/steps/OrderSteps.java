package main.com.order_manager.main.steps;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.swing.launcher.ApplicationLauncher.application;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JFrame;

import org.assertj.swing.core.BasicRobot;
import org.assertj.swing.core.GenericTypeMatcher;
import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.finder.WindowFinder;
import org.assertj.swing.fixture.FrameFixture;

import io.cucumber.java.After;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class OrderSteps {

	private FrameFixture window;

	@After
	public void tearDown() {
		if (window != null) {
			window.cleanUp();
		}
	}

	@When("The Order View is shown")
	public void the_Order_View_is_shown() {
		application("com.order_manager.main.App").withArgs("--mongo-port=27017", "--db-name=" + MongoSteps.DB_NAME,
				"--db-collection=" + MongoSteps.COLLECTION_NAME).start();
		window = WindowFinder.findFrame(new GenericTypeMatcher<JFrame>(JFrame.class) {
			@Override
			protected boolean isMatching(JFrame frame) {
				return "Order Manager".equals(frame.getTitle()) && frame.isShowing();
			}
		}).using(BasicRobot.robotWithCurrentAwtHierarchy());
	}

	@Then("The displayed orders should match the orders in the database")
	public void the_displayed_orders_should_match_the_orders_in_the_database() {
		List<String> expectedOrders = Arrays.asList(
				"ID: 1    Customer: Customer A    Company: Company A    Product: Product A    Status: Pending",
				"ID: 2    Customer: Customer B    Company: Company B    Product: Product A    Status: Pending");

		List<String> displayedOrders = new ArrayList<>();

		for (int i = 0; i < window.list("listAllOrders").contents().length; i++) {
			String orderHtml = window.list("listAllOrders").contents()[i];
			String cleanedOrder = orderHtml.replaceAll("<.*?>", "").replaceAll("\\s+", " ").replace("&nbsp;", " ");

			displayedOrders.add(cleanedOrder.trim());
		}

		assertThat(displayedOrders).containsExactlyElementsOf(expectedOrders);
	}

	@Given("The user provides order data in the text fields, specifying an existing ID")
	public void the_user_provides_order_data_in_the_text_fields_specifying_an_existing_ID() {
		window.textBox("idTextField").enterText(MongoSteps.ORDER_FIXTURE_1_ID);
		window.textBox("customerTextField").enterText("Customer Name");
		window.textBox("companyTextField").enterText("Company Name");
		window.textBox("productTextField").enterText("Product Name");
		window.textBox("statusTextField").enterText("Pending");
		window.textBox("descriptionTextField").enterText("Description");
		window.button(JButtonMatcher.withText("OK")).click();
	}

	@When("The user clicks the {string} button")
	public void the_user_clicks_the_button(String buttonText) {
		window.button(JButtonMatcher.withText(buttonText)).click();
	}

	@Then("An error message is shown indicating that the order with the existing ID already exists")
	public void an_error_message_is_shown_indicating_that_the_order_with_the_existing_ID_already_exists() {
		assertThat(window.label("lblErrorMessage").text())
				.contains("Order with ID " + MongoSteps.ORDER_FIXTURE_1_ID + " already exists!");
	}

	@Given("The user provides a company name in the search field, specifying an existing name")
	public void the_user_provides_a_company_name_in_the_search_field_specifying_an_existing_name() {
		window.textBox("searchCompanyTextField").enterText(MongoSteps.ORDER_FIXTURE_1_COMPANY);
	}

	@Then("The search results should contain the searched order")
	public void the_search_results_should_contain_the_searched_order() {
		boolean found = false;
		String[] displayedOrders = window.list("listSearchedOrders").contents();

		for (String order : displayedOrders) {
			if (order.contains(MongoSteps.ORDER_FIXTURE_1_ID) && order.contains(MongoSteps.ORDER_FIXTURE_1_CUSTOMER)
					&& order.contains(MongoSteps.ORDER_FIXTURE_1_COMPANY)) {
				found = true;
				break;
			}
		}
		assertThat(found).isTrue();
	}

	@Given("The user provides a company name in the search field, specifying a non-existing name")
	public void the_user_provides_a_company_name_in_the_search_field_specifying_a_non_existing_name() {
		window.textBox("searchCompanyTextField").enterText("NonExistingCompany");
	}

	@Then("An error message is shown indicating that no orders were found for the specified company")
	public void an_error_message_is_shown_indicating_that_no_orders_were_found_for_the_specified_company() {
		assertThat(window.label("lblErrorMessage").text()).contains("No orders found for company: NonExistingCompany");
	}

	@Given("The user selects an order from the displayed list")
	public void the_user_selects_an_order_from_the_displayed_list() {
		window.list("listAllOrders").selectItem(0);
	}

	@When("The user clicks the {string} button for the selected order")
	public void the_user_clicks_the_button_for_the_selected_order(String buttonText) {
		window.button(JButtonMatcher.withText(buttonText)).click();
	}

	@Then("The selected order should be removed from the list")
	public void the_selected_order_should_be_removed_from_the_list() {
		boolean orderExists = false;
		String[] contents = window.list("listAllOrders").contents();
		for (String row : contents) {
			if (row.contains(MongoSteps.ORDER_FIXTURE_1_ID)) {
				orderExists = true;
				break;
			}
		}
		assertThat(orderExists).isFalse();
	}
}
