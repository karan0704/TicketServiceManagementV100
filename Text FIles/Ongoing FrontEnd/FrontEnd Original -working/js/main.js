  let currentUser = null;
        let currentRole = null;
        const API_BASE_URL = 'http://localhost:9090';

        // Initialize application
        document.addEventListener('DOMContentLoaded', function() {
            setupEventListeners();
        });

        function setupEventListeners() {
            // Login form
            document.getElementById('loginForm').addEventListener('submit', handleLogin);
            
            // Engineer form
            document.getElementById('engineerForm').addEventListener('submit', handleCreateEngineer);
            
            // Customer form
            document.getElementById('customerForm').addEventListener('submit', handleCreateCustomer);
            
            // Ticket form
            document.getElementById('ticketForm').addEventListener('submit', handleCreateTicket);
        }