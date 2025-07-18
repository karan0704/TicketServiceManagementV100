// Authentication functions
        async function handleLogin(e) {
            e.preventDefault();
            
            const username = document.getElementById('loginUsername').value;
            const password = document.getElementById('loginPassword').value;
            
            try {
                const response = await fetch(`${API_BASE_URL}/login`, {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                    },
                    body: JSON.stringify({ username, password })
                });
                
                const data = await response.json();
                
                if (response.ok) {
                    currentUser = username;
                    currentRole = data.message.includes('ENGINEER') ? 'ENGINEER' : 'CUSTOMER';
                    
                    showMainApp();
                    showMessage('Login successful!', 'success');
                    
                    // Load initial data
                    loadEngineers();
                    loadCustomers();
                    loadTickets();
                } else {
                    showMessage(data.error || 'Login failed', 'error');
                }
            } catch (error) {
                showMessage('Error connecting to server', 'error');
            }
        }

        function logout() {
            currentUser = null;
            currentRole = null;
            
            document.getElementById('loginSection').classList.remove('hidden');
            document.getElementById('mainApp').classList.add('hidden');
            document.getElementById('userInfo').classList.add('hidden');
            
            // Reset forms
            document.getElementById('loginForm').reset();
            
            showMessage('Logged out successfully', 'success');
        }

        function showMainApp() {
            document.getElementById('loginSection').classList.add('hidden');
            document.getElementById('mainApp').classList.remove('hidden');
            document.getElementById('userInfo').classList.remove('hidden');
            
            document.getElementById('currentUser').textContent = currentUser;
            document.getElementById('currentRole').textContent = currentRole;

            // Role-based visibility
    if (currentRole === 'ENGINEER') {
        document.querySelector('[onclick="showTab(\'engineers\')"]').style.display = 'block';
        document.querySelector('[onclick="showTab(\'customers\')"]').style.display = 'block';
    } else {
        document.querySelector('[onclick="showTab(\'engineers\')"]').style.display = 'none';
        document.querySelector('[onclick="showTab(\'customers\')"]').style.display = 'none';
    }
        }