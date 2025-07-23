// js/customer_dashboard.js

document.addEventListener('DOMContentLoaded', function() {
    console.log('Customer Dashboard: DOMContentLoaded fired.');

    const username = localStorage.getItem('username');
    const role = localStorage.getItem('role');
    const loggedIn = localStorage.getItem('loggedIn');

    console.log('Customer Dashboard - localStorage values:');
    console.log('  username:', username);
    console.log('  role:', role);
    console.log('  loggedIn:', loggedIn);

    // Redirect to login if not logged in or not a customer
    if (!loggedIn || loggedIn !== 'true' || role !== 'CUSTOMER') {
        console.warn('Customer Dashboard: Redirecting to login. Not logged in or not a CUSTOMER.');
        window.location.href = 'login.html';
        return; // Stop further execution
    }

    // If we reach here, the user is authenticated as a customer
    console.log('Customer Dashboard: User authenticated as CUSTOMER.');
    document.getElementById('usernameDisplay').textContent = username;

    // Logout functionality
    document.getElementById('logoutBtn').addEventListener('click', function(event) {
        event.preventDefault();
        console.log('Customer Dashboard: Logging out...');
        localStorage.clear(); // Clear all stored session data
        window.location.href = 'login.html'; // Redirect to login page
    });

    // Placeholder for fetching customer tickets
    async function fetchCustomerTickets() {
        console.log('Customer Dashboard: Fetching tickets for customer:', username);
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
            // In a real app, use a custom modal instead of alert()
            console.error('Ticket description cannot be empty.');
            alert('Ticket description cannot be empty.');
            return;
        }

        console.log('Customer Dashboard: Creating new ticket with description:', description);
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
        // alert('Ticket created successfully!'); // In a real app, use a custom modal
        // document.getElementById('ticketDescription').value = ''; // Clear form
        // fetchCustomerTickets(); // Refresh ticket list
    });

    fetchCustomerTickets(); // Initial fetch of tickets
});