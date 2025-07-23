// js/customer_dashboard.js

document.addEventListener('DOMContentLoaded', function() {
    const username = localStorage.getItem('username');
    const role = localStorage.getItem('role');
    const loggedIn = localStorage.getItem('loggedIn');

    // Redirect to login if not logged in or not a customer
    if (!loggedIn || role !== 'CUSTOMER') {
        window.location.href = 'login.html';
        return;
    }

    // Display username
    document.getElementById('usernameDisplay').textContent = username;

    // Logout functionality
    document.getElementById('logoutBtn').addEventListener('click', function(event) {
        event.preventDefault();
        localStorage.clear(); // Clear all stored session data
        window.location.href = 'login.html'; // Redirect to login page
    });

    // Placeholder for fetching customer tickets
    async function fetchCustomerTickets() {
        console.log('Fetching tickets for customer:', username);
        // In a real application, you would make an API call here:
        // const response = await fetch('http://localhost:9090/api/tickets/customer/' + customerId, {
        //     headers: {
        //         'X-Username': username,
        //         'X-User-Role': role
        //     }
        // });
        // const tickets = await response.json();
        // Render tickets to #customerTicketsList
    }

    // Placeholder for creating a new ticket
    document.getElementById('createTicketForm').addEventListener('submit', async function(event) {
        event.preventDefault();
        const description = document.getElementById('ticketDescription').value;

        if (!description.trim()) {
            alert('Ticket description cannot be empty.'); // Use a custom modal in a real app
            return;
        }

        console.log('Creating new ticket with description:', description);
        // In a real application, you would make an API call here:
        // const response = await fetch('http://localhost:9090/api/tickets', {
        //     method: 'POST',
        //     headers: {
        //         'Content-Type': 'application/json',
        //         'X-Username': username, // Send username for backend to identify customer
        //         'X-User-Role': role
        //     },
        //     body: JSON.stringify({ description: description })
        // });
        // const newTicket = await response.json();
        // console.log('New ticket created:', newTicket);
        // alert('Ticket created successfully!'); // Use a custom modal in a real app
        // document.getElementById('ticketDescription').value = ''; // Clear form
        // fetchCustomerTickets(); // Refresh ticket list
    });

    fetchCustomerTickets(); // Initial fetch of tickets
});