// Login functionality
class LoginManager {
    constructor() {
        this.initializeLogin();
    }

    initializeLogin() {
        const loginForm = document.getElementById('loginForm');
        if (loginForm) {
            loginForm.addEventListener('submit', (e) => this.handleLogin(e));
        }

        // Check if user already logged in
        this.checkExistingSession();
    }

    checkExistingSession() {
        const currentUser = localStorage.getItem('currentUser');
        if (currentUser) {
            try {
                const userData = JSON.parse(currentUser);
                this.redirectToRoleDashboard(userData.role, userData.isDefaultEngineer);
            } catch (error) {
                localStorage.removeItem('currentUser');
            }
        }
    }

    async handleLogin(event) {
        event.preventDefault();

        const username = document.getElementById('username').value.trim();
        const password = document.getElementById('password').value.trim();

        if (!username || !password) {
            this.showMessage('Please enter both username and password', 'error');
            return;
        }

        this.showLoading(true);
        this.clearMessage();

        try {
            const response = await fetch('/api/auth/login', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ username, password })
            });

            const data = await response.json();

            if (data.success) {
                // Store user data in localStorage
                localStorage.setItem('currentUser', JSON.stringify(data));

                this.showMessage('Login successful! Redirecting...', 'success');

                // Redirect after short delay
                setTimeout(() => {
                    this.redirectToRoleDashboard(data.role, data.isDefaultEngineer);
                }, 1000);
            } else {
                this.showMessage(data.message || 'Login failed', 'error');
            }
        } catch (error) {
            console.error('Login error:', error);
            this.showMessage('Network error. Please try again.', 'error');
        } finally {
            this.showLoading(false);
        }
    }

    redirectToRoleDashboard(role, isDefaultEngineer) {
        if (role === 'CUSTOMER') {
            window.location.href = 'customer_dashboard.html';
        } else if (role === 'ENGINEER') {
            if (isDefaultEngineer) {
                window.location.href = 'admin_dashboard.html';
            } else {
                window.location.href = 'engineer_dashboard.html';
            }
        } else {
            this.showMessage('Unknown user role', 'error');
        }
    }

    showMessage(message, type) {
        let messageDiv = document.getElementById('message');
        if (!messageDiv) {
            messageDiv = document.createElement('div');
            messageDiv.id = 'message';
            document.querySelector('.login-container').appendChild(messageDiv);
        }

        messageDiv.className = `message ${type}`;
        messageDiv.textContent = message;
        messageDiv.style.display = 'block';

        // Auto hide success messages
        if (type === 'success') {
            setTimeout(() => {
                messageDiv.style.display = 'none';
            }, 3000);
        }
    }

    clearMessage() {
        const messageDiv = document.getElementById('message');
        if (messageDiv) {
            messageDiv.style.display = 'none';
        }
    }

    showLoading(show) {
        let loadingDiv = document.getElementById('loading');
        if (!loadingDiv) {
            loadingDiv = document.createElement('div');
            loadingDiv.id = 'loading';
            loadingDiv.className = 'loading';
            loadingDiv.innerHTML = '<div class="spinner"></div><p>Logging in...</p>';
            document.querySelector('.login-container').appendChild(loadingDiv);
        }

        loadingDiv.style.display = show ? 'block' : 'none';

        // Disable/enable form
        const form = document.getElementById('loginForm');
        const inputs = form.querySelectorAll('input, button');
        inputs.forEach(input => {
            input.disabled = show;
        });
    }
}

// Utility functions
function validateInput(input) {
    if (input.value.trim() === '') {
        input.style.borderColor = '#dc3545';
        return false;
    } else {
        input.style.borderColor = '#28a745';
        return true;
    }
}

// Initialize when DOM is loaded
document.addEventListener('DOMContentLoaded', () => {
    new LoginManager();

    // Add input validation
    const inputs = document.querySelectorAll('.form-control');
    inputs.forEach(input => {
        input.addEventListener('blur', () => validateInput(input));
        input.addEventListener('input', () => {
            if (input.style.borderColor === 'rgb(220, 53, 69)') {
                input.style.borderColor = '#e0e0e0';
            }
        });
    });
});