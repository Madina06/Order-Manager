package com.order_manager.main.view.impl;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;

import com.order_manager.main.button.ButtonEditor;
import com.order_manager.main.button.ButtonRenderer;
import com.order_manager.main.controller.OrderController;
import com.order_manager.main.model.Order;
import com.order_manager.main.view.OrderManagerView;

public class OrderManagerViewSwingImpl extends JFrame implements OrderManagerView {

	private OrderController orderController;
	private JTable orderTable;
	private JTextField searchField;

	public OrderManagerViewSwingImpl(OrderController orderController) {
		this.orderController = orderController;
		setTitle("Order Manager");
		setSize(800, 600);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		setLayout(new BorderLayout());

		JPanel searchPanel = new JPanel();
		searchField = new JTextField(20);
		searchField.setName("searchField");
		JButton searchButton = new JButton("Search by Company");
		searchButton.setName("searchButton");
		searchPanel.add(new JLabel("Company: "));
		searchPanel.add(searchField);
		searchPanel.add(searchButton);
		add(searchPanel, BorderLayout.NORTH);

		orderTable = new JTable();
		orderTable.setName("orderTable");
		displayOrders(orderController.getAllOrders());
		add(new JScrollPane(orderTable), BorderLayout.CENTER);

		JPanel controlPanel = new JPanel();
		JButton createButton = new JButton("Create Order");
		createButton.setName("createButton");
		controlPanel.add(createButton);
		add(controlPanel, BorderLayout.SOUTH);

		searchButton.addActionListener(e -> {
			String company = searchField.getText().trim();
			if (company.isEmpty()) {
				displayOrders(orderController.getAllOrders());
			} else {
				List<Order> orders = orderController.getAllOrders();
				List<Order> filteredOrders = orders.stream()
						.filter(order -> order.getCompany().equalsIgnoreCase(company)).collect(Collectors.toList());
				if (filteredOrders.isEmpty()) {
					JOptionPane.showMessageDialog(this, "No orders found for company: " + company, "Search Result",
							JOptionPane.INFORMATION_MESSAGE);
				} else {
					displayOrders(filteredOrders);
				}
			}
		});

		createButton.addActionListener(e -> showCreateOrderForm());
	}

	@Override
	public void displayOrders(List<Order> orders) {
		String[] columnNames = { "ID", "Customer", "Company", "Product", "Status", "Description", "Update", "Delete" };
		Object[][] data = new Object[orders.size()][8];

		for (int i = 0; i < orders.size(); i++) {
			Order order = orders.get(i);
			data[i][0] = order.getId();
			data[i][1] = order.getCustomer();
			data[i][2] = order.getCompany();
			data[i][3] = order.getProduct();
			data[i][4] = order.getStatus();
			data[i][5] = order.getDescription();
			data[i][6] = "Update";
			data[i][7] = "Delete";
		}

		orderTable.setModel(new javax.swing.table.DefaultTableModel(data, columnNames) {
			public boolean isCellEditable(int row, int column) {
				return (column == 6 || column == 7);
			}
		});

		orderTable.getColumn("Update").setCellRenderer(new ButtonRenderer());
		orderTable.getColumn("Update").setCellEditor(new ButtonEditor(new JCheckBox(), "Update", this));
		orderTable.getColumn("Delete").setCellRenderer(new ButtonRenderer());
		orderTable.getColumn("Delete").setCellEditor(new ButtonEditor(new JCheckBox(), "Delete", this));
	}

	@Override
	public void showCreateOrderForm() {
		JTextField idField = new JTextField(10);
		idField.setName("idField");
		JTextField customerField = new JTextField(10);
		customerField.setName("customerField");
		JTextField companyField = new JTextField(10);
		companyField.setName("companyField");
		JTextField productField = new JTextField(10);
		productField.setName("productField");
		JTextField statusField = new JTextField(10);
		statusField.setName("statusField");
		JTextField descriptionField = new JTextField(10);
		descriptionField.setName("descriptionField");

		JPanel panel = new JPanel(new GridLayout(6, 2));
		panel.add(new JLabel("ID:"));
		panel.add(idField);
		panel.add(new JLabel("Customer:"));
		panel.add(customerField);
		panel.add(new JLabel("Company:"));
		panel.add(companyField);
		panel.add(new JLabel("Product:"));
		panel.add(productField);
		panel.add(new JLabel("Status:"));
		panel.add(statusField);
		panel.add(new JLabel("Description:"));
		panel.add(descriptionField);

		int result = JOptionPane.showConfirmDialog(this, panel, "Create Order", JOptionPane.OK_CANCEL_OPTION);
		String id = idField.getText().trim();

		if (orderController.getOrderById(id) != null) {
			JOptionPane.showMessageDialog(this, "Order with ID " + id + " already exists!", "Error",
					JOptionPane.ERROR_MESSAGE);
			return;
		}

		Order newOrder = new Order(id, customerField.getText().trim(), companyField.getText().trim(),
				productField.getText().trim(), statusField.getText().trim(), descriptionField.getText().trim(), null,
				null);
		orderController.createOrder(newOrder);
		displayOrders(orderController.getAllOrders());

	}

	@Override
	public JTable getOrderTable() {
		return orderTable;
	}

	@Override
	public void handleButtonAction(String action, int row) {
		String orderId = (String) orderTable.getValueAt(row, 0);
		if (action.equals("Delete")) {
			orderController.deleteOrder(orderId);
			displayOrders(orderController.getAllOrders());
		} else {
			openUpdateOrderDialog(orderId);
		}
	}

	private void openUpdateOrderDialog(String orderId) {
		Order order = orderController.getOrderById(orderId);
		JTextField customerField = new JTextField(order.getCustomer(), 10);
		customerField.setName("customerField");
		JTextField companyField = new JTextField(order.getCompany(), 10);
		companyField.setName("companyField");
		JTextField productField = new JTextField(order.getProduct(), 10);
		productField.setName("productField");
		JTextField statusField = new JTextField(order.getStatus(), 10);
		statusField.setName("statusField");
		JTextField descriptionField = new JTextField(order.getDescription(), 10);
		descriptionField.setName("descriptionField");

		JPanel panel = new JPanel(new GridLayout(5, 2));
		panel.add(new JLabel("Customer:"));
		panel.add(customerField);
		panel.add(new JLabel("Company:"));
		panel.add(companyField);
		panel.add(new JLabel("Product:"));
		panel.add(productField);
		panel.add(new JLabel("Status:"));
		panel.add(statusField);
		panel.add(new JLabel("Description:"));
		panel.add(descriptionField);

		int result = JOptionPane.showConfirmDialog(this, panel, "Update Order", JOptionPane.OK_CANCEL_OPTION);
		order.setCustomer(customerField.getText().trim());
		order.setCompany(companyField.getText().trim());
		order.setProduct(productField.getText().trim());
		order.setStatus(statusField.getText().trim());
		order.setDescription(descriptionField.getText().trim());
		orderController.updateOrder(order);
		displayOrders(orderController.getAllOrders());
	}
}
