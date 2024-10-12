Feature: Order Management

  Background:
    Given The database contains a few orders

  Scenario: Displaying orders
    When The Order View is shown
    Then The displayed orders should match the orders in the database

  Scenario: Searching for an existing order
    When The Order View is shown
    Given The user provides a company name in the search field, specifying an existing name
    When The user clicks the "Search by Company" button
    Then The search results should contain the searched order

  Scenario: Searching for a non-existing order
    When The Order View is shown
    Given The user provides a company name in the search field, specifying a non-existing name
    When The user clicks the "Search by Company" button
    Then The displayed orders should match the orders in the database
