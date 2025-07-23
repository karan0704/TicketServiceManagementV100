// js/engineer_dashboard.js

document.addEventListener('DOMContentLoaded', () => {
    const username = localStorage.getItem('username');
    const role = localStorage.getItem('role');
    const loggedIn = localStorage.getItem('loggedIn');

    if (!loggedIn || role !== 'ENGINEER') {
        window.location.href = 'login.html';
        return;
    }

    document.getElementById('usernameDisplay').textContent = username;

    document.getElementById('logoutBtn').addEventListener('click', (e) => {
        e.preventDefault();
        localStorage.clear();
        window.location.href = 'login.html';
    });

    // Fetch and render tickets assigned to engineer
    async function fetchEngineerTickets() {
        try {
            const response = await fetch(`http://localhost:9090/api/tickets/engineer/${username}`, {
                headers: {
                    'X-Username': username,
                    'X-User-Role': role
                }
            });

            const tickets = await response.json();
            const container = document.getElementById('engineerTicketsList');
            container.innerHTML = '';

            if (Array.isArray(tickets)) {
                tickets.forEach(ticket => {
                    const card = document.createElement('div');
                    card.className = 'bg-white p-6 rounded-xl shadow hover:shadow-lg transition';
                    card.innerHTML = `
                        <h3 class="text-lg font-semibold text-indigo-700">Ticket ID: #${ticket.id}</h3>
                        <p class="text-sm text-gray-600 mt-2">Description: ${ticket.description}</p>
                        <p class="text-sm text-gray-500 mt-1">Status: <span class="font-medium text-yellow-600">${ticket.status}</span></p>
                        <p class="text-sm text-gray-500 mt-1">Customer: ${ticket.createdBy?.username || 'Unknown'}</p>
                    `;
                    container.appendChild(card);
                });
            }
        } catch (error) {
            console.error('Error fetching engineer tickets:', error);
        }
    }

    fetchEngineerTickets();
});