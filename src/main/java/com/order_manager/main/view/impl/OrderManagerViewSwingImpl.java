package com.order_manager.main.view.impl;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import com.order_manager.main.controller.OrderController;
import com.order_manager.main.model.Order;
import com.order_manager.main.view.OrderManagerView;

public class OrderManagerViewSwingImpl extends JFrame implements OrderManagerView {

	private static final long serialVersionUID = 1L;

	private JPanel contentPane;
	private JTextField idTextField;
	private JTextField customerTextField;
	private JTextField companyTextField;
	private JTextField productTextField;
	private JTextField statusTextField;
	private JTextField descriptionTextField;
	private JTextField searchCompanyTextField;

	private JButton btnAdd;
	private JButton btnSearch;
	private JButton btnDelete;

	private JLabel lblErrorMessage;

	private JList<Order> listAllOrders;
	private JList<Order> listSearchedOrders;

	DefaultListModel<Order> listAllOrdersModel;
	DefaultListModel<Order> listSearchedOrdersModel;

	private transient OrderController orderController;

	public OrderManagerViewSwingImpl(OrderController orderController) {
		this.orderController = orderController;
		setTitle("Order Manager");
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setSize(900, 700);
		setLocationRelativeTo(null);

		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(15, 15, 15, 15));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(15, 15));

		JPanel inputPanel = createInputPanel();
		contentPane.add(inputPanel, BorderLayout.NORTH);

		JPanel listsPanel = createListsPanel();
		contentPane.add(listsPanel, BorderLayout.CENTER);

		JPanel controlPanel = createControlPanel();
		contentPane.add(controlPanel, BorderLayout.SOUTH);
		
		displayOrders(orderController.getAllOrders());
	}

	private JPanel createInputPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		panel.setBorder(new TitledBorder(new LineBorder(Color.GRAY, 1, true), "Order Details", TitledBorder.LEADING,
				TitledBorder.TOP, new Font("Arial", Font.BOLD, 14), Color.DARK_GRAY));

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(5, 5, 5, 5);
		gbc.fill = GridBagConstraints.HORIZONTAL;

		JLabel lblId = new JLabel("ID:");
		lblId.setFont(new Font("Arial", Font.PLAIN, 12));
		gbc.gridx = 0;
		gbc.gridy = 0;
		panel.add(lblId, gbc);

		idTextField = new JTextField();
		idTextField.setName("idTextField");
		idTextField.setFont(new Font("Arial", Font.PLAIN, 12));
		idTextField.addKeyListener(new FormKeyAdapter());
		gbc.gridx = 1;
		gbc.gridy = 0;
		panel.add(idTextField, gbc);

		JLabel lblCustomer = new JLabel("Customer:");
		lblCustomer.setFont(new Font("Arial", Font.PLAIN, 12));
		gbc.gridx = 0;
		gbc.gridy = 1;
		panel.add(lblCustomer, gbc);

		customerTextField = new JTextField();
		customerTextField.setName("customerTextField");
		customerTextField.setFont(new Font("Arial", Font.PLAIN, 12));
		customerTextField.addKeyListener(new FormKeyAdapter());
		gbc.gridx = 1;
		gbc.gridy = 1;
		panel.add(customerTextField, gbc);

		JLabel lblCompany = new JLabel("Company:");
		lblCompany.setFont(new Font("Arial", Font.PLAIN, 12));
		gbc.gridx = 0;
		gbc.gridy = 2;
		panel.add(lblCompany, gbc);

		companyTextField = new JTextField();
		companyTextField.setName("companyTextField");
		companyTextField.setFont(new Font("Arial", Font.PLAIN, 12));
		companyTextField.addKeyListener(new FormKeyAdapter());
		gbc.gridx = 1;
		gbc.gridy = 2;
		panel.add(companyTextField, gbc);

		JLabel lblProduct = new JLabel("Product:");
		lblProduct.setFont(new Font("Arial", Font.PLAIN, 12));
		gbc.gridx = 0;
		gbc.gridy = 3;
		panel.add(lblProduct, gbc);

		productTextField = new JTextField();
		productTextField.setName("productTextField");
		productTextField.setFont(new Font("Arial", Font.PLAIN, 12));
		productTextField.addKeyListener(new FormKeyAdapter());
		gbc.gridx = 1;
		gbc.gridy = 3;
		panel.add(productTextField, gbc);

		JLabel lblStatus = new JLabel("Status:");
		lblStatus.setFont(new Font("Arial", Font.PLAIN, 12));
		gbc.gridx = 0;
		gbc.gridy = 4;
		panel.add(lblStatus, gbc);

		statusTextField = new JTextField();
		statusTextField.setName("statusTextField");
		statusTextField.setFont(new Font("Arial", Font.PLAIN, 12));
		statusTextField.addKeyListener(new FormKeyAdapter());
		gbc.gridx = 1;
		gbc.gridy = 4;
		panel.add(statusTextField, gbc);

		JLabel lblDescription = new JLabel("Description:");
		lblDescription.setFont(new Font("Arial", Font.PLAIN, 12));
		gbc.gridx = 0;
		gbc.gridy = 5;
		panel.add(lblDescription, gbc);

		descriptionTextField = new JTextField();
		descriptionTextField.setName("descriptionTextField");
		descriptionTextField.setFont(new Font("Arial", Font.PLAIN, 12));
		descriptionTextField.addKeyListener(new FormKeyAdapter());
		gbc.gridx = 1;
		gbc.gridy = 5;
		panel.add(descriptionTextField, gbc);

		btnAdd = new JButton("Add Order");
		btnAdd.setName("btnAdd");
		btnAdd.setFont(new Font("Arial", Font.BOLD, 12));
		btnAdd.setEnabled(false);
		btnAdd.setBackground(new Color(76, 175, 80));
		btnAdd.setForeground(Color.WHITE);
		btnAdd.setFocusPainted(false);
		btnAdd.addActionListener(e -> addOrder());
		gbc.gridx = 1;
		gbc.gridy = 6;
		gbc.anchor = GridBagConstraints.CENTER;
		panel.add(btnAdd, gbc);

		return panel;
	}

	private JPanel createListsPanel() {
		JPanel panel = new JPanel(new GridLayout(1, 2, 15, 0));

		JPanel allOrdersPanel = new JPanel(new BorderLayout(5, 5));
		allOrdersPanel.setBorder(new TitledBorder(new LineBorder(Color.GRAY, 1, true), "All Orders",
				TitledBorder.LEADING, TitledBorder.TOP, new Font("Arial", Font.BOLD, 14), Color.DARK_GRAY));

		listAllOrdersModel = new DefaultListModel<>();
		listAllOrders = new JList<>(listAllOrdersModel);
		listAllOrders.setName("listAllOrders");
		listAllOrders.setFont(new Font("Arial", Font.PLAIN, 12));
		listAllOrders.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		listAllOrders.setCellRenderer(new OrderListCellRenderer());
		listAllOrders.addListSelectionListener(e -> handleOrderSelection(listAllOrders.getSelectedValue()));
		JScrollPane scrollAll = new JScrollPane(listAllOrders);
		allOrdersPanel.add(scrollAll, BorderLayout.CENTER);

		JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
		searchPanel.setBorder(new EmptyBorder(5, 5, 5, 5));

		JLabel lblSearchCompany = new JLabel("Search by Company:");
		lblSearchCompany.setFont(new Font("Arial", Font.PLAIN, 12));
		searchPanel.add(lblSearchCompany);

		searchCompanyTextField = new JTextField();
		searchCompanyTextField.setName("searchCompanyTextField");
		searchCompanyTextField.setFont(new Font("Arial", Font.PLAIN, 12));
		searchCompanyTextField.addKeyListener(new SearchKeyAdapter());
		searchCompanyTextField.setColumns(15);
		searchPanel.add(searchCompanyTextField);

		btnSearch = new JButton("Search");
		btnSearch.setName("btnSearch");
		btnSearch.setFont(new Font("Arial", Font.BOLD, 12));
		btnSearch.setEnabled(false);
		btnSearch.setBackground(new Color(33, 150, 243));
		btnSearch.setForeground(Color.WHITE);
		btnSearch.setFocusPainted(false);
		btnSearch.addActionListener(e -> searchOrders());
		searchPanel.add(btnSearch);

		allOrdersPanel.add(searchPanel, BorderLayout.NORTH);

		panel.add(allOrdersPanel);

		JPanel searchedOrdersPanel = new JPanel(new BorderLayout(5, 5));
		searchedOrdersPanel.setBorder(new TitledBorder(new LineBorder(Color.GRAY, 1, true), "Searched Orders",
				TitledBorder.LEADING, TitledBorder.TOP, new Font("Arial", Font.BOLD, 14), Color.DARK_GRAY));

		listSearchedOrdersModel = new DefaultListModel<>();
		listSearchedOrders = new JList<>(listSearchedOrdersModel);
		listSearchedOrders.setName("listSearchedOrders");
		listSearchedOrders.setFont(new Font("Arial", Font.PLAIN, 12));
		listSearchedOrders.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		listSearchedOrders.setCellRenderer(new OrderListCellRenderer());
		JScrollPane scrollSearched = new JScrollPane(listSearchedOrders);
		searchedOrdersPanel.add(scrollSearched, BorderLayout.CENTER);

		panel.add(searchedOrdersPanel);

		return panel;
	}

	private JPanel createControlPanel() {
		JPanel panel = new JPanel(new BorderLayout(5, 5));

		lblErrorMessage = new JLabel(" ");
		lblErrorMessage.setName("lblErrorMessage");
		lblErrorMessage.setFont(new Font("Arial", Font.BOLD, 12));
		lblErrorMessage.setForeground(Color.RED);
		lblErrorMessage.setHorizontalAlignment(SwingConstants.CENTER);
		panel.add(lblErrorMessage, BorderLayout.NORTH);

		btnDelete = new JButton("Delete Selected");
		btnDelete.setName("btnDelete");
		btnDelete.setFont(new Font("Arial", Font.BOLD, 12));
		btnDelete.setEnabled(false);
		btnDelete.setBackground(new Color(244, 67, 54));
		btnDelete.setForeground(Color.WHITE);
		btnDelete.setFocusPainted(false);
		btnDelete.addActionListener(e -> deleteSelectedOrder());
		panel.add(btnDelete, BorderLayout.SOUTH);

		return panel;
	}

	private void addOrder() {
		String id = idTextField.getText().trim();

		String customer = customerTextField.getText().trim();
		String company = companyTextField.getText().trim();
		String product = productTextField.getText().trim();
		String status = statusTextField.getText().trim();
		String description = descriptionTextField.getText().trim();

		Order newOrder = new Order(id, customer, company, product, status, description, null, null);
		orderController.createOrder(newOrder);
		displayOrders(orderController.getAllOrders());
		clearFormFields();
	}

	private void searchOrders() {
		String company = searchCompanyTextField.getText().trim();
		List<Order> filteredOrders = orderController.getAllOrders().stream()
				.filter(order -> order.getCompany().equalsIgnoreCase(company)).collect(Collectors.toList());
		showSearchedOrdersList(filteredOrders);
	}

	void deleteSelectedOrder() {
		Order selectedOrder = listSearchedOrders.getSelectedValue();
		selectedOrder = listAllOrders.getSelectedValue();
		int confirm = JOptionPane.showConfirmDialog(this,
				"Are you sure you want to delete Order ID: " + selectedOrder.getId() + "?", "Confirm Deletion",
				JOptionPane.YES_NO_OPTION);
		if (confirm == JOptionPane.YES_OPTION) {
			orderController.deleteOrder(selectedOrder.getId());
			displayOrders(orderController.getAllOrders());
			showSearchedOrdersList(orderController.getAllOrders());
		}
	}

	private void handleOrderSelection(Order order) {
		if (order != null) {
			btnDelete.setEnabled(true);
		} else {
			btnDelete.setEnabled(false);
		}
	}

	private class FormKeyAdapter extends KeyAdapter {
		@Override
		public void keyReleased(KeyEvent e) {
			if (idTextField.getText().trim().isEmpty()) {
			    btnAdd.setEnabled(false);
			} else if (customerTextField.getText().trim().isEmpty()) {
			    btnAdd.setEnabled(false);
			} else if (companyTextField.getText().trim().isEmpty()) {
			    btnAdd.setEnabled(false);
			} else if (productTextField.getText().trim().isEmpty()) {
			    btnAdd.setEnabled(false);
			} else if (statusTextField.getText().trim().isEmpty()) {
			    btnAdd.setEnabled(false);
			} else if (descriptionTextField.getText().trim().isEmpty()) {
			    btnAdd.setEnabled(false);
			} else {
			    btnAdd.setEnabled(true);
			}
		}
	}

	private class SearchKeyAdapter extends KeyAdapter {
		@Override
		public void keyReleased(KeyEvent e) {
			btnSearch.setEnabled(true);
		}
	}

	private class OrderListCellRenderer extends DefaultListCellRenderer {
		private static final long serialVersionUID = 1L;

		@Override
		public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
				boolean cellHasFocus) {
			Order order = (Order) value;
			String display = String.format(
					"<html><b>ID:</b> %s &nbsp;&nbsp; <b>Customer:</b> %s &nbsp;&nbsp; <b>Company:</b> %s &nbsp;&nbsp; <b>Product:</b> %s &nbsp;&nbsp; <b>Status:</b> %s</html>",
					order.getId(), order.getCustomer(), order.getCompany(), order.getProduct(), order.getStatus());
			JLabel label = (JLabel) super.getListCellRendererComponent(list, display, index, isSelected, cellHasFocus);
			label.setBorder(new EmptyBorder(5, 5, 5, 5));
			return label;
		}
	}

	@Override
	public void displayOrders(List<Order> orders) {
		listAllOrdersModel.clear();
		orders.forEach(listAllOrdersModel::addElement);
	}

	@Override
	public void showAllOrders(List<Order> orders) {
		displayOrders(orders);
	}

	@Override
	public void showSearchedOrdersList(List<Order> orders) {
		listSearchedOrdersModel.clear();
		orders.forEach(listSearchedOrdersModel::addElement);
	}

	@Override
	public void showErrorMessage(String message) {
		lblErrorMessage.setText(message);
	}

	@Override
	public void orderAdded(Order order) {
		listAllOrdersModel.addElement(order);
		lblErrorMessage.setForeground(new Color(76, 175, 80));
		lblErrorMessage.setText("Order added successfully.");
	}

	@Override
	public void orderRemoved(Order order) {
		listAllOrdersModel.removeElement(order);
		listSearchedOrdersModel.removeElement(order);
		lblErrorMessage.setForeground(new Color(244, 67, 54));
		lblErrorMessage.setText("Order removed successfully.");
	}

	@Override
	public JList<Order> getAllOrdersList() {
		return listAllOrders;
	}

	@Override
	public JList<Order> getSearchedOrdersList() {
		return listSearchedOrders;
	}

	@Override
	public JTextField getSearchCompanyTextField() {
		return searchCompanyTextField;
	}

	@Override
	public JTextField getIdTextField() {
		return idTextField;
	}

	@Override
	public JTextField getCustomerTextField() {
		return customerTextField;
	}

	@Override
	public JTextField getCompanyTextField() {
		return companyTextField;
	}

	@Override
	public JTextField getProductTextField() {
		return productTextField;
	}

	@Override
	public JTextField getStatusTextField() {
		return statusTextField;
	}

	@Override
	public JTextField getDescriptionTextField() {
		return descriptionTextField;
	}

	@Override
	public void clearFormFields() {
		idTextField.setText("");
		customerTextField.setText("");
		companyTextField.setText("");
		productTextField.setText("");
		statusTextField.setText("");
		descriptionTextField.setText("");
		btnAdd.setEnabled(false);
	}
}
