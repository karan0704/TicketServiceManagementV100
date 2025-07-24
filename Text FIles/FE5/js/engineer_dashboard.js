document.addEventListener('DOMContentLoaded', () => {
    // Load login details
    const username = localStorage.getItem('username');
    const role = localStorage.getItem('role');
    const loggedIn = localStorage.getItem('loggedIn');

    // Redirect if not logged in or not engineer role
    if (!loggedIn || role !== 'ENGINEER') {
        window.location.href = 'login.html';
        return;
    }

    // Display username in header
    document.getElementById('usernameDisplay').textContent = username;

    // Logout button
    document.getElementById('logoutBtn').addEventListener('click', (e) => {
        e.preventDefault();
        localStorage.clear();
        window.location.href = 'login.html';
    });

    // Container for tickets
    const container = document.getElementById('engineerTicketsList');

    // Fetch tickets assigned to engineer
    async function fetchEngineerTickets() {
        try {
            const response = await fetch(`http://localhost:9090/api/tickets/engineer/${username}`, {
                method: 'GET',
                headers: {
                    'X-Username': username,
                    'X-User-Role': role
                }
            });

            if (!response.ok) {
                container.innerHTML = '<p class="text-red-600">Failed to load assigned tickets.</p>';
                return;
            }

            const tickets = await response.json();
            container.innerHTML = '';

            if (tickets.length === 0) {
                container.innerHTML = '<p class="text-gray-600">No tickets assigned to you currently.</p>';
                return;
            }

            tickets.forEach(ticket => {
                const card = document.createElement('div');
                card.className = 'bg-white p-6 rounded-xl shadow hover:shadow-lg transition mb-4';
                card.innerHTML = `
                    <h3 class="text-lg font-semibold text-indigo-700">Ticket ID: #${ticket.id}</h3>
                    <p class="text-sm text-gray-600 mt-2">Description: ${ticket.description}</p>
                    <p class="text-sm text-gray-500 mt-1">Status: <span class="font-medium text-yellow-600">${ticket.status}</span></p>
                    <p class="text-sm text-gray-500 mt-1">Customer: ${ticket.createdBy?.username || 'Unknown'}</p>
                `;
                container.appendChild(card);
            });
        } catch (error) {
            container.innerHTML = `<p class="text-red-600">Error loading tickets: ${error.message}</p>`;
        }
    }

    // Load tickets on page load
    fetchEngineerTickets();
});