// Admin Dashboard functionality
class AdminDashboard {
    constructor() {
        this.currentUser = null;
        this.initializeDashboard();
    }

    initializeDashboard() {
        // Check authentication
        this.currentUser = this.checkAuth();
        if (!this.currentUser) return;

        // Initialize UI
        this.setupUserInfo();
        this.setupNavigation();
        this.loadInitialData();
        this.setupEventListeners();
    }

    checkAuth() {
        const user = JSON.parse(localStorage.getItem('currentUser'));
        if (!user || user.role !== 'ENGINEER' || !user.isDefaultEngineer) {
            window.location.href = 'index.html';
            return null;
        }
        return user;
    }

    setupUserInfo() {
        const userInfoElement = document.getElementById('userInfo');
        if (userInfoElement) {
            userInfoElement.innerHTML = `
                <h1>Welcome, ${this.currentUser.fullName}</h1>
                <p>Admin Engineer Dashboard - System Administrator</p>
            `;
        }
    }

    setupNavigation() {
        const navTabs = document.querySelectorAll('.nav-tab');
        navTabs.forEach(tab => {
            tab.addEventListener('click', (e) => {
                const targetSection = e.target.dataset.section;
                this.showSection(targetSection);
            });
        });

        // Show first section by default
        this.showSection('tickets');
    }

    showSection(sectionName) {
        // Hide all sections
        document.querySelectorAll('.section').forEach(section => {
            section.classList.remove('active');
        });

        // Remove active from all tabs
        document.querySelectorAll('.nav-tab').forEach(tab => {
            tab.classList.remove('active');
        });

        // Show target section
        const targetSection = document.getElementById(sectionName + 'Section');
        const targetTab = document.querySelector(`[data-section="${sectionName}"]`);

        if (targetSection) targetSection.classList.add('active');
        if (targetTab) targetTab.classList.add('active');

        // Load section data
        this.loadSectionData(sectionName);
    }

    loadSectionData(sectionName) {
        switch (sectionName) {
            case 'tickets':
                this.loadAllTickets();
                break;
            case 'users':
                // Users will be loaded when button is clicked
                break;
            case 'engineers':
                // Form already loaded
                break;
            case 'customers':
                // Form already loaded
                break;
            case 'categories':
                // Categories will be loaded when button is clicked
                break;
        }
    }

    setupEventListeners() {
        // Create engineer form
        const createEngineerForm = document.getElementById('createEngineerForm');
        if (createEngineerForm) {
            createEngineerForm.addEventListener('submit', (e) => this.handleCreateEngineer(e));
        }

        // Create customer form
        const createCustomerForm = document.getElementById('createCustomerForm');
        if (createCustomerForm) {
            createCustomerForm.addEventListener('submit', (e) => this.handleCreateCustomer(e));
        }

        // Create category form
        const createCategoryForm = document.getElementById('createCategoryForm');
        if (createCategoryForm) {
            createCategoryForm.addEventListener('submit', (e) => this.handleCreateCategory(e));
        }

        // Logout button
        const logoutBtn = document.getElementById('logoutBtn');
        if (logoutBtn) {
            logoutBtn.addEventListener('click', () => this.logout());
        }
    }

    async loadInitialData() {
        await this.loadAllTickets();
    }

    async loadAllTickets() {
        try {
            this.showLoading('ticketsList');

            const response = await fetch('/api/public/tickets');
            const tickets = await response.json();

            this.displayTickets(tickets);
        } catch (error) {
            this.showMessage('Error loading tickets: ' + error.message, 'error');
        } finally {
            this.hideLoading('ticketsList');
        }
    }

    async loadUnassignedTickets() {
        try {
            this.showLoading('ticketsList');

            const response = await this.apiCall('/api/engineer/tickets/unassigned');
            const tickets = await response.json();

            this.displayTickets(tickets);
        } catch (error) {
            this.showMessage('Error loading unassigned tickets: ' + error.message, 'error');
        } finally {
            this.hideLoading('ticketsList');
        }
    }

    displayTickets(tickets) {
        const ticketsList = document.getElementById('ticketsList');

        if (tickets.length === 0) {
            ticketsList.innerHTML = '<div class="message info">No tickets found.</div>';
            return;
        }

        let html = `
            <div class="table-container">
                <table class="table">
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Title</th>
                            <th>Customer</th>
                            <th>Engineer</th>
                            <th>Status</th>
                            <th>Category</th>
                            <th>Created</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
        `;

        tickets.forEach(ticket => {
            html += `
                <tr>
                    <td>#${ticket.id}</td>
                    <td>${ticket.title}</td>
                    <td>${ticket.customer ? ticket.customer.fullName : 'N/A'}</td>
                    <td>${ticket.assignedEngineer ? ticket.assignedEngineer.fullName : 'Unassigned'}</td>
                    <td><span class="status-badge status-${ticket.status.toLowerCase().replace('_', '-')}">${ticket.status}</span></td>
                    <td>${ticket.category ? ticket.category.name : 'N/A'}</td>
                    <td>${this.formatDate(ticket.createdAt)}</td>
                    <td>
                        <button class="btn btn-primary btn-small" onclick="adminDashboard.viewComments(${ticket.id})">Comments</button>
                        <button class="btn btn-danger btn-small" onclick="adminDashboard.deleteTicket(${ticket.id})">Delete</button>
                    </td>
                </tr>
            `;
        });

        html += '</tbody></table></div>';
        ticketsList.innerHTML = html;
    }

    async viewComments(ticketId) {
        try {
            const response = await this.apiCall(`/api/engineer/tickets/${ticketId}/comments`);
            const comments = await response.json();

            this.showCommentsModal(ticketId, comments);
        } catch (error) {
            this.showMessage('Error loading comments: ' + error.message, 'error');
        }
    }

    showCommentsModal(ticketId, comments) {
        let commentsHtml = `
            <div class="modal" id="commentsModal">
                <div class="modal-content">
                    <div class="modal-header">
                        <h3 class="modal-title">Comments for Ticket #${ticketId}</h3>
                        <button class="close" onclick="adminDashboard.closeModal('commentsModal')">&times;</button>
                    </div>
                    <div class="modal-body">
        `;

        if (comments.length === 0) {
            commentsHtml += '<div class="message info">No comments found for this ticket.</div>';
        } else {
            comments.forEach(comment => {
                commentsHtml += `
                    <div class="comment" style="border-left: 3px solid #667eea; padding-left: 15px; margin-bottom: 15px;">
                        <strong>${comment.author.fullName} (${comment.author.role})</strong><br>
                        <p style="margin: 5px 0;">${comment.content}</p>
                        <small style="color: #666;">${this.formatDateTime(comment.createdAt)}</small>
                    </div>
                `;
            });
        }

        commentsHtml += `
                    </div>
                </div>
            </div>
        `;

        document.body.insertAdjacentHTML('beforeend', commentsHtml);
        document.getElementById('commentsModal').style.display = 'block';
    }

    async deleteTicket(ticketId) {
        if (!confirm('Are you sure you want to delete this ticket? This action cannot be undone.')) {
            return;
        }

        try {
            const response = await this.apiCall(`/api/engineer/tickets/${ticketId}`, 'DELETE');

            if (response.ok) {
                this.showMessage('Ticket deleted successfully!', 'success');
                this.loadAllTickets();
            } else {
                throw new Error('Failed to delete ticket');
            }
        } catch (error) {
            this.showMessage('Error deleting ticket: ' + error.message, 'error');
        }
    }

    async loadAllUsers() {
        try {
            this.showLoading('usersList');

            const response = await this.apiCall('/api/admin/users');
            const users = await response.json();

            this.displayUsers(users);
        } catch (error) {
            this.showMessage('Error loading users: ' + error.message, 'error');
        } finally {
            this.hideLoading('usersList');
        }
    }

    displayUsers(users) {
        const usersList = document.getElementById('usersList');

        if (users.length === 0) {
            usersList.innerHTML = '<div class="message info">No users found.</div>';
            return;
        }

        let html = `
            <div class="table-container">
                <table class="table">
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Username</th>
                            <th>Full Name</th>
                            <th>Email</th>
                            <th>Role</th>
                            <th>Admin</th>
                            <th>Company/Specialization</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
        `;

        users.forEach(user => {
            html += `
                <tr>
                    <td>#${user.id}</td>
                    <td>${user.username}</td>
                    <td>${user.fullName}</td>
                    <td>${user.email}</td>
                    <td><span class="status-badge ${user.role === 'CUSTOMER' ? 'status-created' : 'status-acknowledged'}">${user.role}</span></td>
                    <td>${user.isDefaultEngineer ? 'Yes' : 'No'}</td>
                    <td>${user.role === 'CUSTOMER' ? (user.companyName || 'N/A') : (user.specialization || 'N/A')}</td>
                    <td>
                        <button class="btn btn-danger btn-small" onclick="adminDashboard.deleteUser(${user.id})">Delete</button>
                    </td>
                </tr>
            `;
        });

        html += '</tbody></table></div>';
        usersList.innerHTML = html;
    }

    async deleteUser(userId) {
        if (!confirm('Are you sure you want to delete this user? This action cannot be undone.')) {
            return;
        }

        try {
            const response = await this.apiCall(`/api/admin/users/${userId}`, 'DELETE');

            if (response.ok) {
                this.showMessage('User deleted successfully!', 'success');
                this.loadAllUsers();
            } else {
                throw new Error('Failed to delete user');
            }
        } catch (error) {
            this.showMessage('Error deleting user: ' + error.message, 'error');
        }
    }

    async handleCreateEngineer(event) {
        event.preventDefault();

        const engineerData = {
            username: document.getElementById('engineerUsername').value.trim(),
            password: document.getElementById('engineerPassword').value.trim(),
            fullName: document.getElementById('engineerFullName').value.trim(),
            email: document.getElementById('engineerEmail').value.trim(),
            phoneNumber: document.getElementById('engineerPhone').value.trim(),
            specialization: document.getElementById('engineerSpecialization').value.trim(),
            isDefaultEngineer: document.getElementById('isDefaultEngineer').checked,
            role: 'ENGINEER'
        };

        try {
            const response = await this.apiCall('/api/admin/engineers', 'POST', engineerData);

            if (response.ok) {
                this.showMessage('Engineer created successfully!', 'success');
                document.getElementById('createEngineerForm').reset();
            } else {
                const errorData = await response.json();
                throw new Error(errorData.message || 'Failed to create engineer');
            }
        } catch (error) {
            this.showMessage('Error creating engineer: ' + error.message, 'error');
        }
    }

    async handleCreateCustomer(event) {
        event.preventDefault();

        const customerData = {
            username: document.getElementById('customerUsername').value.trim(),
            password: document.getElementById('customerPassword').value.trim(),
            fullName: document.getElementById('customerFullName').value.trim(),
            email: document.getElementById('customerEmail').value.trim(),
            phoneNumber: document.getElementById('customerPhone').value.trim(),
            address: document.getElementById('customerAddress').value.trim(),
            companyName: document.getElementById('customerCompanyName').value.trim(),
            role: 'CUSTOMER'
        };

        try {
            const response = await this.apiCall('/api/engineer/customers', 'POST', customerData);

            if (response.ok) {
                this.showMessage('Customer created successfully!', 'success');
                document.getElementById('createCustomerForm').reset();
            } else {
                const errorData = await response.json();
                throw new Error(errorData.message || 'Failed to create customer');
            }
        } catch (error) {
            this.showMessage('Error creating customer: ' + error.message, 'error');
        }
    }

    async handleCreateCategory(event) {
        event.preventDefault();

        const categoryData = {
            name: document.getElementById('categoryName').value.trim(),
            description: document.getElementById('categoryDescription').value.trim()
        };

        try {
            const response = await this.apiCall('/api/admin/categories', 'POST', categoryData);

            if (response.ok) {
                this.showMessage('Category created successfully!', 'success');
                document.getElementById('createCategoryForm').reset();
            } else {
                const errorData = await response.json();
                throw new Error(errorData.message || 'Failed to create category');
            }
        } catch (error) {
            this.showMessage('Error creating category: ' + error.message, 'error');
        }
    }

    async loadCategories() {
        try {
            this.showLoading('categoriesList');

            const response = await this.apiCall('/api/admin/categories');
            const categories = await response.json();

            this.displayCategories(categories);
        } catch (error) {
            this.showMessage('Error loading categories: ' + error.message, 'error');
        } finally {
            this.hideLoading('categoriesList');
        }
    }

    displayCategories(categories) {
        const categoriesList = document.getElementById('categoriesList');

        if (categories.length === 0) {
            categoriesList.innerHTML = '<div class="message info">No categories found.</div>';
            return;
        }

        let html = `
            <div class="table-container">
                <table class="table">
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Name</th>
                            <th>Description</th>
                        </tr>
                    </thead>
                    <tbody>
        `;

        categories.forEach(category => {
            html += `
                <tr>
                    <td>#${category.id}</td>
                    <td>${category.name}</td>
                    <td>${category.description || 'No description'}</td>
                </tr>
            `;
        });

        html += '</tbody></table></div>';
        categoriesList.innerHTML = html;
    }

    // Utility methods
    async apiCall(url, method = 'GET', body = null) {
        const headers = {
            'Content-Type': 'application/json',
            'X-User-ID': this.currentUser.id,
            'X-User-Role': this.currentUser.role,
            'X-Username': this.currentUser.username
        };

        const config = { method, headers };
        if (body) config.body = JSON.stringify(body);

        return fetch(url, config);
    }

    showMessage(message, type) {
        let messageDiv = document.getElementById('globalMessage');
        if (!messageDiv) {
            messageDiv = document.createElement('div');
            messageDiv.id = 'globalMessage';
            messageDiv.style.position = 'fixed';
            messageDiv.style.top = '20px';
            messageDiv.style.right = '20px';
            messageDiv.style.zIndex = '9999';
            messageDiv.style.maxWidth = '400px';
            document.body.appendChild(messageDiv);
        }

        messageDiv.className = `message ${type}`;
        messageDiv.textContent = message;
        messageDiv.style.display = 'block';

        setTimeout(() => {
            messageDiv.style.display = 'none';
        }, 5000);
    }

    showLoading(containerId) {
        const container = document.getElementById(containerId);
        if (container) {
            container.innerHTML = '<div class="loading"><div class="spinner"></div><p>Loading...</p></div>';
        }
    }

    hideLoading(containerId) {
        // Loading will be replaced by actual content
    }

    closeModal(modalId) {
        const modal = document.getElementById(modalId);
        if (modal) {
            modal.remove();
        }
    }

    formatDate(dateString) {
        return new Date(dateString).toLocaleDateString();
    }

    formatDateTime(dateString) {
        return new Date(dateString).toLocaleString();
    }

    logout() {
        if (confirm('Are you sure you want to logout?')) {
            localStorage.removeItem('currentUser');
            window.location.href = 'index.html';
        }
    }
}

// Initialize dashboard when DOM is loaded
let adminDashboard;
document.addEventListener('DOMContentLoaded', () => {
    adminDashboard = new AdminDashboard();
});