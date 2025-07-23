document.addEventListener('DOMContentLoaded', () => {
    const loggedIn = localStorage.getItem('loggedIn') === 'true';
    const role = localStorage.getItem('role');

    if (loggedIn) {
        if (role === 'ENGINEER') {
            window.location.href = 'engineer_dashboard.html';
        } else if (role === 'CUSTOMER') {
            window.location.href = 'customer_dashboard.html';
        }
    }
});