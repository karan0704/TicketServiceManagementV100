// js/customer_dashboard.js

document.addEventListener('DOMContentLoaded', () => {
    const username = localStorage.getItem('username');
    const role = localStorage.getItem('role');
    const loggedIn = localStorage.getItem('loggedIn');

    if (!loggedIn || role !== 'CUSTOMER') {
        window.location.href = 'login.html';
        return;
    }

    document.getElementById('usernameDisplay').textContent = username;

    document.getElementById('logoutBtn').addEventListener('click', (e) => {
        e.preventDefault();
        localStorage.clear();
        window.location.href = 'login.html';
    });

    const ticketContainer = document.getElementById('customerTicketsList');
    const createTicketForm = document.getElementById('createTicketForm');

    // Fetch customer tickets
    async function loadTickets() {
        try {
            const response = await fetch(`http://localhost:9090/api/tickets/customer/${username}`, {
                headers: {
                    'X-Username': username,
                    'X-User-Role': role
                }
            });

            const tickets = await response.json();
            ticketContainer.innerHTML = '';

            tickets.forEach(ticket => {
                const card = document.createElement('div');
                card.className = 'bg-white p-6 rounded-xl shadow hover:shadow-lg transition';
                card.innerHTML = `
          <h3 class="text-lg font-semibold text-indigo-700">Ticket ID: #${ticket.id}</h3>
          <p class="text-sm text-gray-600 mt-2">Description: ${ticket.description}</p>
          <p class="text-sm text-gray-500 mt-1">Status: <span class="font-medium text-yellow-600">${ticket.status}</span></p>
          <p class="text-sm text-gray-500 mt-1">Engineer: ${ticket.acknowledgedBy?.username || 'Unassigned'}</p>
        `;
                ticketContainer.appendChild(card);
            });
        } catch (error) {
            console.error('Error loading tickets:', error);
        }
    }

    loadTickets();

    // Handle ticket creation
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
            console.error('Ticket creation error:', error);
        }
    });
});