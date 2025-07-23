document.addEventListener('DOMContentLoaded', () => {
    const form = document.getElementById('loginForm');
    const messageBox = document.getElementById('messageBox');

    form.addEventListener('submit', async (e) => {
        e.preventDefault();

        const username = document.getElementById('username').value.trim();
        const password = document.getElementById('password').value.trim();

        messageBox.classList.add('hidden');
        messageBox.textContent = '';

        try {
            const response = await fetch('http://localhost:9090/login', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ username, password })
            });

            const data = await response.json();

            if (response.ok) {
                localStorage.setItem('loggedIn', 'true');
                localStorage.setItem('username', data.username);
                localStorage.setItem('role', data.role);

                if (data.role === 'ENGINEER') {
                    window.location.href = 'engineer_dashboard.html';
                } else if (data.role === 'CUSTOMER') {
                    window.location.href = 'customer_dashboard.html';
                } else {
                    throw new Error('Unknown role');
                }
            } else {
                messageBox.textContent = data.message || 'Login failed.';
                messageBox.classList.remove('hidden');
            }
        } catch (err) {
            messageBox.textContent = 'Error: ' + err.message;
            messageBox.classList.remove('hidden');
        }
    });
});