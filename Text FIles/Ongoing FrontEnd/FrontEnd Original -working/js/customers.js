// Customer functions
        async function handleCreateCustomer(e) {
            e.preventDefault();
            
            const username = document.getElementById('customerUsername').value;
            const password = document.getElementById('customerPassword').value;
            
            try {
                const response = await fetch(`${API_BASE_URL}/customers`, {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                    },
                    body: JSON.stringify({ username, password })
                });
                
                if (response.ok) {
                    showMessage('Customer created successfully!', 'success');
                    document.getElementById('customerForm').reset();
                    loadCustomers();
                } else {
                    showMessage('Error creating customer', 'error');
                }
            } catch (error) {
                showMessage('Error connecting to server', 'error');
            }
        }

        async function loadCustomers() {
            try {
                const response = await fetch(`${API_BASE_URL}/customers`);
                const customers = await response.json();
                
                displayCustomers(customers);
                populateCustomerDropdown(customers);
            } catch (error) {
                showMessage('Error loading customers', 'error');
            }
        }

        function displayCustomers(customers) {
            const container = document.getElementById('customersList');
            
            if (customers.length === 0) {
                container.innerHTML = '<p>No customers found.</p>';
                return;
            }
            
            let html = '<table class="table"><thead><tr><th>ID</th><th>Username</th></tr></thead><tbody>';
            
            customers.forEach(customer => {
                html += `
                    <tr>
                        <td>${customer.id}</td>
                        <td>${customer.username}</td>
                    </tr>
                `;
            });
            
            html += '</tbody></table>';
            container.innerHTML = html;
        }

        function populateCustomerDropdown(customers) {
            const select = document.getElementById('ticketCustomer');
            select.innerHTML = '<option value="">Select Customer</option>';
            
            customers.forEach(customer => {
                const option = document.createElement('option');
                option.value = customer.id;
                option.textContent = customer.username;
                select.appendChild(option);
            });
        }