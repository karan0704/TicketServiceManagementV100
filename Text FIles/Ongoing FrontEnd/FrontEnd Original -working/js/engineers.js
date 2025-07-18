// Engineer functions
        async function handleCreateEngineer(e) {
            e.preventDefault();
            if (currentRole !== 'ENGINEER') {
        showMessage('Only engineers can create other engineers.', 'error');
        return;
    }
            
            const username = document.getElementById('engineerUsername').value;
            const password = document.getElementById('engineerPassword').value;
            
            try {
                const response = await fetch(`${API_BASE_URL}/engineers`, {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                        'X-Username': currentUser,
                        'X-User-Role': currentRole
                    },
                    body: JSON.stringify({ username, password })
                });
                
                if (response.ok) {
                    showMessage('Engineer created successfully!', 'success');
                    document.getElementById('engineerForm').reset();
                    loadEngineers();
                } else {
                    showMessage('Error creating engineer', 'error');
                }
            } catch (error) {
                showMessage('Error connecting to server', 'error');
            }
        }

        async function loadEngineers() {
            try {
                const response = await fetch(`${API_BASE_URL}/engineers`);
                const engineers = await response.json();
                
                displayEngineers(engineers);
                populateEngineerDropdown(engineers);
            } catch (error) {
                showMessage('Error loading engineers', 'error');
            }
        }

        function displayEngineers(engineers) {
            const container = document.getElementById('engineersList');
            
            if (engineers.length === 0) {
                container.innerHTML = '<p>No engineers found.</p>';
                return;
            }
            
            let html = '<table class="table"><thead><tr><th>ID</th><th>Username</th><th>Actions</th></tr></thead><tbody>';
            
            engineers.forEach(engineer => {
                html += `
                    <tr>
                        <td>${engineer.id}</td>
                        <td>${engineer.username}</td>
                        <td>
                            <button class="btn-danger" onclick="deleteEngineer(${engineer.id})">Delete</button>
                        </td>
                    </tr>
                `;
            });
            
            html += '</tbody></table>';
            container.innerHTML = html;
        }

        function populateEngineerDropdown(engineers) {
            const select = document.getElementById('ticketEngineer');
            select.innerHTML = '<option value="">Select Engineer</option>';
            
            engineers.forEach(engineer => {
                const option = document.createElement('option');
                option.value = engineer.id;
                option.textContent = engineer.username;
                select.appendChild(option);
            });
        }

        async function deleteEngineer(id) {
            if (!confirm('Are you sure you want to delete this engineer?')) {
                return;
            }
            
            try {
                const response = await fetch(`${API_BASE_URL}/engineers/${id}`, {
                    method: 'DELETE'
                });
                
                if (response.ok) {
                    showMessage('Engineer deleted successfully!', 'success');
                    loadEngineers();
                } else {
                    showMessage('Error deleting engineer', 'error');
                }
            } catch (error) {
                showMessage('Error connecting to server', 'error');
            }
        }