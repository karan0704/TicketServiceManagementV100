// js/login.js

// Function to update header visibility based on login status (specific to login page)
function updateHeaderVisibility() {
    const headerLogoText = document.getElementById('headerLogoText');
    const headerNav = document.getElementById('headerNav');
    // Check localStorage for 'loggedIn' status
    const isLoggedIn = localStorage.getItem('loggedIn') === 'true';

    // On login page, header elements are hidden until successful login
    if (isLoggedIn) {
        headerLogoText.classList.remove('hidden');
        headerNav.classList.remove('hidden');
    } else {
        headerLogoText.classList.add('hidden');
        headerNav.classList.add('hidden');
    }
}

// Call updateHeaderVisibility on page load for the login page
document.addEventListener('DOMContentLoaded', updateHeaderVisibility);

document.getElementById('loginForm').addEventListener('submit', async function(event) {
    event.preventDefault(); // Prevent default form submission

    const username = document.getElementById('username').value;
    const password = document.getElementById('password').value;
    const messageBox = document.getElementById('messageBox');

    // Clear previous messages and reset styling
    messageBox.textContent = '';
    messageBox.classList.remove('show', 'bg-green-100', 'text-green-700', 'border-green-700');
    messageBox.classList.add('bg-red-100', 'text-red-700', 'border-red-700');


    // Basic validation
    if (!username || !password) {
        messageBox.textContent = 'Please enter both username and password.';
        messageBox.classList.add('show');
        return;
    }

    try {
        // Assuming the backend is running on localhost:9090 as per application.properties
        const response = await fetch('http://localhost:9090/login', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ username, password })
        });

        const data = await response.json(); // Parse the JSON response

        console.log('Login Page - Backend response status:', response.status);
        console.log('Login Page - Backend response data:', data);

        if (response.ok) {
            // Login successful
            messageBox.textContent = data.message || 'Login successful!';
            messageBox.classList.remove('bg-red-100', 'text-red-700', 'border-red-700');
            messageBox.classList.add('bg-green-100', 'text-green-700', 'border-green-700', 'show');

            // Store user data and login status
            localStorage.setItem('username', data.username);
            localStorage.setItem('role', data.role);
            localStorage.setItem('loggedIn', 'true'); // Explicitly store as string 'true'

            console.log('Login Page - Stored username:', localStorage.getItem('username'));
            console.log('Login Page - Stored role:', localStorage.getItem('role'));
            console.log('Login Page - Stored loggedIn status:', localStorage.getItem('loggedIn'));

            // Update header visibility after successful login (for login page only)
            updateHeaderVisibility();

            // Redirect based on user role
            if (data.role === 'CUSTOMER') {
                console.log('Login Page - Redirecting to customer_dashboard.html');
                window.location.href = 'customer_dashboard.html';
            } else if (data.role === 'ENGINEER') {
                console.log('Login Page - Redirecting to engineer_dashboard.html');
                window.location.href = 'engineer_dashboard.html';
            } else {
                // Fallback for unknown roles
                console.warn('Login Page - Unknown user role received from backend:', data.role);
                window.location.href = 'index.html'; // Or a generic dashboard
            }

        } else {
            // Login failed
            messageBox.textContent = data.message || 'Login failed. Please check your credentials.';
            messageBox.classList.add('show');
            localStorage.removeItem('loggedIn'); // Ensure loggedIn flag is false
            localStorage.removeItem('role'); // Clear role on failed login
            localStorage.removeItem('username'); // Clear username on failed login
            updateHeaderVisibility(); // Update header visibility on failed login
            console.error('Login Page - Login failed:', data);
        }
    } catch (error) {
        messageBox.textContent = 'An error occurred. Please try again later.';
        messageBox.classList.add('show');
        localStorage.removeItem('loggedIn'); // Ensure loggedIn flag is false
        localStorage.removeItem('role'); // Clear role on error
        localStorage.removeItem('username'); // Clear username on error
        updateHeaderVisibility(); // Update header visibility on error
        console.error('Login Page - Fetch error:', error);
    }
});