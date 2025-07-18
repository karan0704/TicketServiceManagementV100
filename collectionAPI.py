import json
import os

def generate_postman_collection(base_url="http://localhost:9090"):
    """
    Generates a Postman Collection JSON object for the Ticket Service Management API.

    Args:
        base_url (str): The base URL of your Spring Boot application (e.g., http://localhost:9090).

    Returns:
        dict: A dictionary representing the Postman Collection.
    """

    collection = {
        "info": {
            "_postman_id": "YOUR_UNIQUE_COLLECTION_ID", # Postman will generate a new one on import
            "name": "Ticket Service Management API",
            "schema": "https://schema.getpostman.com/json/collection/v2.1.0/collection.json"
        },
        "item": [
            {
                "name": "Authentication",
                "item": [
                    {
                        "name": "Login User",
                        "request": {
                            "method": "POST",
                            "header": [
                                { "key": "Content-Type", "value": "application/json" }
                            ],
                            "body": {
                                "mode": "raw",
                                "raw": json.dumps({
                                    "username": "default_engineer", # Or "test_customer"
                                    "password": "password"
                                }, indent=2)
                            },
                            "url": {
                                "raw": f"{base_url}/login",
                                "host": [base_url.replace("http://", "").replace("https://", "").split("/")[0]],
                                "path": ["login"]
                            },
                            "description": "Authenticates a user (customer or engineer)."
                        },
                        "response": []
                    }
                ]
            },
            {
                "name": "Customers",
                "item": [
                    {
                        "name": "Create Customer",
                        "request": {
                            "method": "POST",
                            "header": [
                                { "key": "Content-Type", "value": "application/json" }
                            ],
                            "body": {
                                "mode": "raw",
                                "raw": json.dumps({
                                    "username": "test_customer",
                                    "password": "customer_pass"
                                }, indent=2)
                            },
                            "url": {
                                "raw": f"{base_url}/customers",
                                "host": [base_url.replace("http://", "").replace("https://", "").split("/")[0]],
                                "path": ["customers"]
                            },
                            "description": "Creates a new customer account."
                        },
                        "response": []
                    },
                    {
                        "name": "Get All Customers",
                        "request": {
                            "method": "GET",
                            "header": [],
                            "url": {
                                "raw": f"{base_url}/customers",
                                "host": [base_url.replace("http://", "").replace("https://", "").split("/")[0]],
                                "path": ["customers"]
                            },
                            "description": "Retrieves a list of all customers."
                        },
                        "response": []
                    },
                    {
                        "name": "Get Customer by ID",
                        "request": {
                            "method": "GET",
                            "header": [],
                            "url": {
                                "raw": f"{base_url}/customers/:id",
                                "host": [base_url.replace("http://", "").replace("https://", "").split("/")[0]],
                                "path": ["customers", ":id"],
                                "variable": [
                                    {
                                        "key": "id",
                                        "value": "1", # Example customer ID
                                        "description": "ID of the customer to retrieve"
                                    }
                                ]
                            },
                            "description": "Retrieves a single customer by their ID."
                        },
                        "response": []
                    },
                    {
                        "name": "Update Customer",
                        "request": {
                            "method": "PUT",
                            "header": [
                                { "key": "Content-Type", "value": "application/json" }
                            ],
                            "body": {
                                "mode": "raw",
                                "raw": json.dumps({
                                    "username": "updated_customer",
                                    "password": "new_password"
                                }, indent=2)
                            },
                            "url": {
                                "raw": f"{base_url}/customers/:id",
                                "host": [base_url.replace("http://", "").replace("https://", "").split("/")[0]],
                                "path": ["customers", ":id"],
                                "variable": [
                                    {
                                        "key": "id",
                                        "value": "1", # Example customer ID
                                        "description": "ID of the customer to update"
                                    }
                                ]
                            },
                            "description": "Updates an existing customer's details."
                        },
                        "response": []
                    }
                ]
            },
            {
                "name": "Engineers",
                "item": [
                    {
                        "name": "Create Engineer",
                        "request": {
                            "method": "POST",
                            "header": [
                                { "key": "Content-Type", "value": "application/json" },
                                { "key": "X-User-Role", "value": "ENGINEER" } # Required by controller
                            ],
                            "body": {
                                "mode": "raw",
                                "raw": json.dumps({
                                    "username": "new_engineer",
                                    "password": "engineer_pass",
                                    "role": "ENGINEER" # Ensure role is set
                                }, indent=2)
                            },
                            "url": {
                                "raw": f"{base_url}/engineers",
                                "host": [base_url.replace("http://", "").replace("https://", "").split("/")[0]],
                                "path": ["engineers"]
                            },
                            "description": "Creates a new engineer account. Requires X-User-Role header."
                        },
                        "response": []
                    },
                    {
                        "name": "Get All Engineers",
                        "request": {
                            "method": "GET",
                            "header": [],
                            "url": {
                                "raw": f"{base_url}/engineers",
                                "host": [base_url.replace("http://", "").replace("https://", "").split("/")[0]],
                                "path": ["engineers"]
                            },
                            "description": "Retrieves a list of all engineers."
                        },
                        "response": []
                    },
                    {
                        "name": "Get Engineer by ID",
                        "request": {
                            "method": "GET",
                            "header": [],
                            "url": {
                                "raw": f"{base_url}/engineers/:id",
                                "host": [base_url.replace("http://", "").replace("https://", "").split("/")[0]],
                                "path": ["engineers", ":id"],
                                "variable": [
                                    {
                                        "key": "id",
                                        "value": "1", # Example engineer ID
                                        "description": "ID of the engineer to retrieve"
                                    }
                                ]
                            },
                            "description": "Retrieves a single engineer by their ID."
                        },
                        "response": []
                    },
                    {
                        "name": "Update Engineer",
                        "request": {
                            "method": "PUT",
                            "header": [
                                { "key": "Content-Type", "value": "application/json" }
                            ],
                            "body": {
                                "mode": "raw",
                                "raw": json.dumps({
                                    "username": "updated_engineer",
                                    "password": "new_engineer_pass",
                                    "role": "ENGINEER"
                                }, indent=2)
                            },
                            "url": {
                                "raw": f"{base_url}/engineers/:id",
                                "host": [base_url.replace("http://", "").replace("https://", "").split("/")[0]],
                                "path": ["engineers", ":id"],
                                "variable": [
                                    {
                                        "key": "id",
                                        "value": "1", # Example engineer ID
                                        "description": "ID of the engineer to update"
                                    }
                                ]
                            },
                            "description": "Updates an existing engineer's details."
                        },
                        "response": []
                    },
                    {
                        "name": "Delete Engineer",
                        "request": {
                            "method": "DELETE",
                            "header": [],
                            "url": {
                                "raw": f"{base_url}/engineers/:id",
                                "host": [base_url.replace("http://", "").replace("https://", "").split("/")[0]],
                                "path": ["engineers", ":id"],
                                "variable": [
                                    {
                                        "key": "id",
                                        "value": "1", # Example engineer ID
                                        "description": "ID of the engineer to delete"
                                    }
                                ]
                            },
                            "description": "Deletes an engineer by their ID."
                        },
                        "response": []
                    }
                ]
            },
            {
                "name": "Tickets",
                "item": [
                    {
                        "name": "Create Ticket",
                        "request": {
                            "method": "POST",
                            "header": [
                                { "key": "Content-Type", "value": "application/json" },
                                { "key": "X-Username", "value": "test_customer" }, # Example customer username
                                { "key": "X-User-Role", "value": "CUSTOMER" } # Required by controller
                            ],
                            "body": {
                                "mode": "raw",
                                "raw": json.dumps({
                                    "description": "My internet is not working.",
                                    "engineerId": None # Optional: Assign an engineer immediately
                                }, indent=2)
                            },
                            "url": {
                                "raw": f"{base_url}/tickets",
                                "host": [base_url.replace("http://", "").replace("https://", "").split("/")[0]],
                                "path": ["tickets"]
                            },
                            "description": "Creates a new ticket. Requires X-Username and X-User-Role headers."
                        },
                        "response": []
                    },
                    {
                        "name": "Get All Tickets",
                        "request": {
                            "method": "GET",
                            "header": [],
                            "url": {
                                "raw": f"{base_url}/tickets",
                                "host": [base_url.replace("http://", "").replace("https://", "").split("/")[0]],
                                "path": ["tickets"]
                            },
                            "description": "Retrieves a list of all tickets."
                        },
                        "response": []
                    },
                    {
                        "name": "Get Ticket by ID",
                        "request": {
                            "method": "GET",
                            "header": [],
                            "url": {
                                "raw": f"{base_url}/tickets/:id",
                                "host": [base_url.replace("http://", "").replace("https://", "").split("/")[0]],
                                "path": ["tickets", ":id"],
                                "variable": [
                                    {
                                        "key": "id",
                                        "value": "1", # Example ticket ID
                                        "description": "ID of the ticket to retrieve"
                                    }
                                ]
                            },
                            "description": "Retrieves a single ticket by its ID."
                        },
                        "response": []
                    },
                    {
                        "name": "Update Ticket",
                        "request": {
                            "method": "PUT",
                            "header": [
                                { "key": "Content-Type", "value": "application/json" }
                            ],
                            "body": {
                                "mode": "raw",
                                "raw": json.dumps({
                                    "description": "Internet issue resolved.",
                                    "status": "CLOSED" # Example status update
                                }, indent=2)
                            },
                            "url": {
                                "raw": f"{base_url}/tickets/:id",
                                "host": [base_url.replace("http://", "").replace("https://", "").split("/")[0]],
                                "path": ["tickets", ":id"],
                                "variable": [
                                    {
                                        "key": "id",
                                        "value": "1", # Example ticket ID
                                        "description": "ID of the ticket to update"
                                    }
                                ]
                            },
                            "description": "Updates an existing ticket's details (description, status)."
                        },
                        "response": []
                    },
                    {
                        "name": "Acknowledge Ticket",
                        "request": {
                            "method": "PUT",
                            "header": [],
                            "url": {
                                "raw": f"{base_url}/tickets/:ticketId/acknowledge/:engineerId",
                                "host": [base_url.replace("http://", "").replace("https://", "").split("/")[0]],
                                "path": ["tickets", ":ticketId", "acknowledge", ":engineerId"],
                                "variable": [
                                    {
                                        "key": "ticketId",
                                        "value": "1", # Example ticket ID
                                        "description": "ID of the ticket to acknowledge"
                                    },
                                    {
                                        "key": "engineerId",
                                        "value": "1", # Example engineer ID
                                        "description": "ID of the engineer acknowledging the ticket"
                                    }
                                ]
                            },
                            "description": "Acknowledges a ticket by assigning an engineer and updating its status."
                        },
                        "response": []
                    },
                    {
                        "name": "Delete Ticket",
                        "request": {
                            "method": "DELETE",
                            "header": [],
                            "url": {
                                "raw": f"{base_url}/tickets/:id",
                                "host": [base_url.replace("http://", "").replace("https://", "").split("/")[0]],
                                "path": ["tickets", ":id"],
                                "variable": [
                                    {
                                        "key": "id",
                                        "value": "1", # Example ticket ID
                                        "description": "ID of the ticket to delete"
                                    }
                                ]
                            },
                            "description": "Deletes a ticket by its ID."
                        },
                        "response": []
                    }
                ]
            },
            {
                "name": "Home",
                "request": {
                    "method": "GET",
                    "header": [],
                    "url": {
                        "raw": f"{base_url}/",
                        "host": [base_url.replace("http://", "").replace("https://", "").split("/")[0]],
                        "path": [""]
                    },
                    "description": "Accesses the home page."
                },
                "response": []
            }
        ],
        "variable": [
            {
                "key": "baseUrl",
                "value": base_url,
                "type": "string"
            }
        ]
    }
    return collection

if __name__ == "__main__":
    # You can change the base URL here if your Spring Boot app runs on a different port or host
    # The default is based on the application.properties file (server.port=9090)
    api_base_url = "http://localhost:9090"
    postman_collection_data = generate_postman_collection(api_base_url)

    output_filename = "TicketServiceManagement_Postman_Collection.json"
    with open(output_filename, "w") as f:
        json.dump(postman_collection_data, f, indent=4)

    print(f"Postman collection '{output_filename}' generated successfully!")
    print("\nTo import this into Postman:")
    print("1. Open Postman.")
    print("2. Click on 'Import' in the top-left corner.")
    print(f"3. Select the file '{output_filename}' from your local machine.")
    print("4. Click 'Import' and the collection will appear in your sidebar.")
    print("\nRemember to start your Spring Boot application before running these requests.")