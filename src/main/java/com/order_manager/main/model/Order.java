package com.order_manager.main.model;

import java.util.Objects;

public class Order {
	private String id;
	private String customer;
	private String company;
	private String product;
	private String status;
	private String description;
	private String createdDate;
	private String updatedDate;

	public Order(String id, String customer, String company, String product, String status, String description, String createdDate, String updatedDate) {
		this.id = id;
		this.customer=customer;
		this.company=company;
		this.product=product;
		this.status=status;
		this.description=description;
		this.createdDate=createdDate;
		this.updatedDate=updatedDate;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCustomer() {
		return customer;
	}

	public void setCustomer(String customer) {
		this.customer = customer;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}

	public String getUpdatedDate() {
		return updatedDate;
	}

	public void setUpdatedDate(String updatedDate) {
		this.updatedDate = updatedDate;
	}

	public String getProduct() {
		return product;
	}

	public void setProduct(String product) {
		this.product = product;
	}

	@Override
	public int hashCode() {
		return Objects.hash(company, createdDate, customer, description, id, product, status, updatedDate);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Order other = (Order) obj;
		return Objects.equals(company, other.company) && Objects.equals(createdDate, other.createdDate)
				&& Objects.equals(customer, other.customer) && Objects.equals(description, other.description)
				&& Objects.equals(id, other.id) && Objects.equals(product, other.product)
				&& Objects.equals(status, other.status) && Objects.equals(updatedDate, other.updatedDate);
	}

	@Override
	public String toString() {
		return "Order [id=" + id + ", customer=" + customer + ", company=" + company + ", product=" + product
				+ ", status=" + status + ", description=" + description + ", createdDate=" + createdDate
				+ ", updatedDate=" + updatedDate + "]";
	}

	
}
