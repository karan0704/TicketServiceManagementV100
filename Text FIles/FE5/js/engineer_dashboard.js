// js/engineer_dashboard.js

document.addEventListener('DOMContentLoaded', function() {
    const username = localStorage.getItem('username');
    const role = localStorage.getItem('role');
    const loggedIn = localStorage.getItem('loggedIn');

    // Redirect to login if not logged in or not an engineer
    if (!loggedIn || role !== 'ENGINEER') {
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

    // Placeholder for fetching engineer's assigned tickets
    async function fetchEngineerTickets() {
        console.log('Fetching tickets assigned to engineer:', username);
        // In a real application, you would make an API call here:
        // const response = await fetch('http://localhost:9090/api/tickets/engineer/' + engineerId, {
        //     headers: {
        //         'X-Username': username,
        //         'X-User-Role': role
        //     }
        // });
        // const tickets = await response.json();
        // Render tickets to #engineerTicketsList
    }

    fetchEngineerTickets(); // Initial fetch of tickets
});