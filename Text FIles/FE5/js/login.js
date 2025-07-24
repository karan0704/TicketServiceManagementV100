document.addEventListener('DOMContentLoaded', () => {
    // Get references to form and message box
    const form = document.getElementById('loginForm');
    const messageBox = document.getElementById('messageBox');

    form.addEventListener('submit', async (e) => {
        e.preventDefault();

        // Get user inputs
        const username = document.getElementById('username').value.trim();
        const password = document.getElementById('password').value.trim();

        // Clear previous messages
        messageBox.classList.add('hidden');
        messageBox.textContent = '';

        try {
            // Call backend /login endpoint
            const response = await fetch('http://localhost:9090/login', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ username, password })
            });

            const data = await response.json();

            // On success, store session and redirect based on role
            if (response.ok && (data.role === 'ENGINEER' || data.role === 'CUSTOMER')) {
                localStorage.setItem('loggedIn', 'true');
                localStorage.setItem('username', data.username);
                localStorage.setItem('role', data.role);

                if (data.role === 'ENGINEER') {
                    window.location.href = 'engineer_dashboard.html';
                } else {
                    window.location.href = 'customer_dashboard.html';
                }
            } else {
                // Show error message returned by backend or default
                messageBox.textContent = data.message || 'Login failed.';
                messageBox.classList.remove('hidden');
            }
        } catch (err) {
            messageBox.textContent = 'Error: ' + err.message;
            messageBox.classList.remove('hidden');
        }
    });
});