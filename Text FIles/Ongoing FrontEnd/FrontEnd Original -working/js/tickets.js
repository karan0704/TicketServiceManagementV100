// Ticket functions
        async function handleCreateTicket(e) {
    e.preventDefault();

    if (currentRole !== 'CUSTOMER') {
        showMessage('Only customers can create tickets.', 'error');
        return;
    }

    const description = document.getElementById('ticketDescription').value;
    const engineerId = document.getElementById('ticketEngineer').value || null;

    try {
        const response = await fetch(`${API_BASE_URL}/tickets`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'X-Username': currentUser,
                'X-User-Role': currentRole
            },
            body: JSON.stringify({
                description,
                engineerId: engineerId ? parseInt(engineerId) : null
                // No customerId sent; server should infer from auth
            })
        });

        if (response.ok) {
            showMessage('Ticket created successfully!', 'success');
            document.getElementById('ticketForm').reset();
            loadTickets();
        } else {
            showMessage('Error creating ticket', 'error');
        }
    } catch (error) {
        showMessage('Error connecting to server', 'error');
    }
}


        async function loadTickets() {
            try {
                const response = await fetch(`${API_BASE_URL}/tickets`);
                const tickets = await response.json();
                
                displayTickets(tickets);
            } catch (error) {
                showMessage('Error loading tickets', 'error');
            }
        }

        function displayTickets(tickets) {
            const container = document.getElementById('ticketsList');
            
            if (tickets.length === 0) {
                container.innerHTML = '<p>No tickets found.</p>';
                return;
            }
            
            let html = '<table class="table"><thead><tr><th>ID</th><th>Description</th><th>Status</th><th>Customer</th><th>Engineer</th><th>Actions</th></tr></thead><tbody>';
            
            tickets.forEach(ticket => {
                html += `
                    <tr>
                        <td>${ticket.id}</td>
                        <td>${ticket.description}</td>
                        <td><span class="status-badge status-${ticket.status.toLowerCase()}">${ticket.status}</span></td>
                        <td>${ticket.createdBy ? ticket.createdBy.username : 'N/A'}</td>
                        <td>${ticket.acknowledgedBy ? ticket.acknowledgedBy.username : 'Unassigned'}</td>
                        <td>
                            ${ticket.status === 'CREATED' ? `<button class="btn-success" onclick="acknowledgeTicket(${ticket.id})">Acknowledge</button>` : ''}
                            <button class="btn-danger" onclick="deleteTicket(${ticket.id})">Delete</button>
                        </td>
                    </tr>
                `;
            });
            
            html += '</tbody></table>';
            container.innerHTML = html;
        }

        async function acknowledgeTicket(ticketId) {
            // Get current user's engineer ID (this is a simplified approach)
            try {
                const engineers = await fetch(`${API_BASE_URL}/engineers`).then(r => r.json());
                const currentEngineer = engineers.find(e => e.username === currentUser);
                
                if (!currentEngineer) {
                    showMessage('Only engineers can acknowledge tickets', 'error');
                    return;
                }
                
                const response = await fetch(`${API_BASE_URL}/tickets/${ticketId}/acknowledge/${currentEngineer.id}`, {
                    method: 'PUT'
                });
                
                if (response.ok) {
                    showMessage('Ticket acknowledged successfully!', 'success');
                    loadTickets();
                } else {
                    showMessage('Error acknowledging ticket', 'error');
                }
            } catch (error) {
                showMessage('Error connecting to server', 'error');
            }
        }

        async function deleteTicket(id) {
            if (!confirm('Are you sure you want to delete this ticket?')) {
                return;
            }
            
            try {
                const response = await fetch(`${API_BASE_URL}/tickets/${id}`, {
                    method: 'DELETE'
                });
                
                if (response.ok) {
                    showMessage('Ticket deleted successfully!', 'success');
                    loadTickets();
                } else {
                    showMessage('Error deleting ticket', 'error');
                }
            } catch (error) {
                showMessage('Error connecting to server', 'error');
            }
        }