document.addEventListener('DOMContentLoaded', () => {
    // Fetch login info from localStorage
    const username = localStorage.getItem('username');
    const role = localStorage.getItem('role');
    const loggedIn = localStorage.getItem('loggedIn');

    // Redirect to login if not logged in or role mismatch
    if (!loggedIn || role !== 'CUSTOMER') {
        window.location.href = 'login.html';
        return;
    }

    // Display logged in username
    document.getElementById('usernameDisplay').textContent = username;

    // Handle logout button click
    document.getElementById('logoutBtn').addEventListener('click', (e) => {
        e.preventDefault();
        localStorage.clear();
        window.location.href = 'login.html';
    });

    const ticketContainer = document.getElementById('customerTicketsList');
    const createTicketForm = document.getElementById('createTicketForm');

    // Load tickets created by this customer
    async function loadTickets() {
        try {
            const response = await fetch(`http://localhost:9090/api/tickets/customer/${username}`, {
                method: 'GET',
                headers: {
                    'X-Username': username,
                    'X-User-Role': role
                }
            });

            if (!response.ok) {
                ticketContainer.innerHTML = '<p class="text-red-600">Failed to load tickets.</p>';
                return;
            }

            const tickets = await response.json();
            ticketContainer.innerHTML = '';

            if (tickets.length === 0) {
                ticketContainer.innerHTML = '<p class="text-gray-600">No tickets created yet.</p>';
                return;
            }

            tickets.forEach(ticket => {
                const card = document.createElement('div');
                card.className = 'bg-white p-6 rounded-xl shadow hover:shadow-lg transition mb-4';
                card.innerHTML = `
                    <h3 class="text-lg font-semibold text-indigo-700">Ticket ID: #${ticket.id}</h3>
                    <p class="text-sm text-gray-600 mt-2">Description: ${ticket.description}</p>
                    <p class="text-sm text-gray-500 mt-1">Status: <span class="font-medium text-yellow-600">${ticket.status}</span></p>
                    <p class="text-sm text-gray-500 mt-1">Engineer: ${ticket.acknowledgedBy?.username || 'Unassigned'}</p>
                `;
                ticketContainer.appendChild(card);
            });
        } catch (error) {
            ticketContainer.innerHTML = `<p class="text-red-600">Error loading tickets: ${error.message}</p>`;
        }
    }

    // Submit handler to create new ticket
    createTicketForm.addEventListener('submit', async (e) => {
        e.preventDefault();

        const description = document.getElementById('ticketDescription').value.trim();
        if (!description) return;

        try {
            const response = await fetch('http://localhost:9090/api/tickets', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'X-Username': username,
                    'X-User-Role': role
                },
                body: JSON.stringify({ description })
            });

            if (response.ok) {
                document.getElementById('ticketDescription').value = '';
                loadTickets();
            } else {
                alert('Failed to create ticket.');
            }
        } catch (error) {
            alert('Error creating ticket: ' + error.message);
        }
    });

    // Initial tickets load
    loadTickets();
});