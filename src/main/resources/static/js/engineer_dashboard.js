// Engineer Dashboard functionality
class EngineerDashboard {
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
        if (!user || user.role !== 'ENGINEER') {
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
                <p>Engineer Dashboard - ${this.currentUser.specialization || 'General Support'}</p>
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
                this.loadUnassignedTickets();
                break;
            case 'assigned':
                this.loadAssignedTickets();
                break;
            case 'customers':
                this.loadCustomers();
                break;
            case 'create':
                // Form already loaded
                break;
        }
    }

    setupEventListeners() {
        // Create customer form
        const createCustomerForm = document.getElementById('createCustomerForm');
        if (createCustomerForm) {
            createCustomerForm.addEventListener('submit', (e) => this.handleCreateCustomer(e));
        }

        // Logout button
        const logoutBtn = document.getElementById('logoutBtn');
        if (logoutBtn) {
            logoutBtn.addEventListener('click', () => this.logout());
        }

        // Ticket filter buttons
        document.addEventListener('click', (e) => {
            if (e.target.classList.contains('filter-btn')) {
                this.handleTicketFilter(e.target.dataset.filter);
            }
        });
    }

    async loadInitialData() {
        await this.loadUnassignedTickets();
    }

    async loadUnassignedTickets() {
        try {
            this.showLoading('ticketsList');

            const response = await this.apiCall('/api/engineer/tickets/unassigned');
            const tickets = await response.json();

            this.displayTickets(tickets, 'Unassigned Tickets', true);
        } catch (error) {
            this.showMessage('Error loading unassigned tickets: ' + error.message, 'error');
        } finally {
            this.hideLoading('ticketsList');
        }
    }

    async loadAssignedTickets() {
        try {
            this.showLoading('assignedTicketsList');

            const response = await this.apiCall('/api/engineer/tickets/assigned');
            const tickets = await response.json();

            this.displayAssignedTickets(tickets);
        } catch (error) {
            this.showMessage('Error loading assigned tickets: ' + error.message, 'error');
        } finally {
            this.hideLoading('assignedTicketsList');
        }
    }

    async loadCustomers() {
        try {
            this.showLoading('customersList');

            const response = await this.apiCall('/api/engineer/customers');
            const customers = await response.json();

            this.displayCustomers(customers);
        } catch (error) {
            this.showMessage('Error loading customers: ' + error.message, 'error');
        } finally {
            this.hideLoading('customersList');
        }
    }

    async displayTickets(tickets) {
        const ticketsList = document.getElementById('ticketsList');

        if (tickets.length === 0) {
            ticketsList.innerHTML = '<div class="message info">No tickets found. Create your first ticket!</div>';
            return;
        }

        let html = `
        <div class="table-container">
            <table class="table">
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Title</th>
                        <th>Description</th>
                        <th>Status</th>
                        <th>Category</th>
                        <th>Engineer</th>
                        <th>Attachments</th>
                        <th>Created</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
    `;

        // Process each ticket and load its attachments
        for (const ticket of tickets) {
            const attachments = await this.loadTicketAttachments(ticket.id);

            html += `
            <tr>
                <td>#${ticket.id}</td>
                <td>${ticket.title}</td>
                <td>${this.truncateText(ticket.description, 50)}</td>
                <td><span class="status-badge status-${ticket.status.toLowerCase().replace('_', '-')}">${ticket.status}</span></td>
                <td>${ticket.category ? ticket.category.name : 'N/A'}</td>
                <td>${ticket.assignedEngineer ? ticket.assignedEngineer.fullName : 'Unassigned'}</td>
                <td>
                    <span class="attachment-count">${attachments.length} file(s)</span>
                    ${attachments.length > 0 ? `<button class="btn btn-small btn-secondary" onclick="customerDashboard.viewAttachments(${ticket.id})">View Files</button>` : ''}
                </td>
                <td>${this.formatDate(ticket.createdAt)}</td>
                <td>
                    <button class="btn btn-primary btn-small" onclick="customerDashboard.viewComments(${ticket.id})">Comments</button>
                    <button class="btn btn-secondary btn-small" onclick="customerDashboard.showAddCommentModal(${ticket.id})">Add Comment</button>
                    <button class="btn btn-success btn-small" onclick="customerDashboard.showAddAttachmentModal(${ticket.id})">Attach File</button>
                </td>
            </tr>
        `;
        }

        html += '</tbody></table></div>';
        ticketsList.innerHTML = html;
    }

    displayAssignedTickets(tickets) {
        const assignedTicketsList = document.getElementById('assignedTicketsList');

        if (tickets.length === 0) {
            assignedTicketsList.innerHTML = '<div class="message info">No assigned tickets found.</div>';
            return;
        }

        let html = `
            <h3>My Assigned Tickets</h3>
            <div class="table-container">
                <table class="table">
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Title</th>
                            <th>Customer</th>
                            <th>Status</th>
                            <th>Resolution Date</th>
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
                    <td>${ticket.customer.fullName}</td>
                    <td><span class="status-badge status-${ticket.status.toLowerCase().replace('_', '-')}">${ticket.status}</span></td>
                    <td>${ticket.tentativeResolutionDate ? this.formatDate(ticket.tentativeResolutionDate) : 'Not set'}</td>
                    <td>${this.formatDate(ticket.createdAt)}</td>
                    <td>
                        <button class="btn btn-primary btn-small" onclick="engineerDashboard.showUpdateTicketModal(${ticket.id})">Update</button>
                        <button class="btn btn-secondary btn-small" onclick="engineerDashboard.viewComments(${ticket.id})">Comments</button>
                        <button class="btn btn-success btn-small" onclick="engineerDashboard.showAddCommentModal(${ticket.id})">Add Comment</button>
                    </td>
                </tr>
            `;
        });

        html += '</tbody></table></div>';
        assignedTicketsList.innerHTML = html;
    }

    async viewAttachments(ticketId) {
        try {
            const response = await this.apiCall(`/api/engineer/tickets/${ticketId}/attachments`);
            const attachments = await response.json();
            this.showAttachmentsModal(ticketId, attachments);
        } catch (error) {
            this.showMessage('Error loading attachments: ' + error.message, 'error');
        }
    }

    displayCustomers(customers) {
        const customersList = document.getElementById('customersList');

        if (customers.length === 0) {
            customersList.innerHTML = '<div class="message info">No customers found.</div>';
            return;
        }

        let html = `
            <h3>All Customers</h3>
            <div class="table-container">
                <table class="table">
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Username</th>
                            <th>Full Name</th>
                            <th>Email</th>
                            <th>Company</th>
                            <th>Phone</th>
                            <th>Created</th>
                        </tr>
                    </thead>
                    <tbody>
        `;

        customers.forEach(customer => {
            html += `
                <tr>
                    <td>#${customer.id}</td>
                    <td>${customer.username}</td>
                    <td>${customer.fullName}</td>
                    <td>${customer.email}</td>
                    <td>${customer.companyName || 'N/A'}</td>
                    <td>${customer.phoneNumber || 'N/A'}</td>
                    <td>${this.formatDate(customer.createdAt)}</td>
                </tr>
            `;
        });

        html += '</tbody></table></div>';
        customersList.innerHTML = html;
    }

    async acknowledgeTicket(ticketId) {
        if (!confirm('Do you want to acknowledge and assign this ticket to yourself?')) {
            return;
        }

        try {
            const response = await this.apiCall(`/api/engineer/tickets/${ticketId}/acknowledge`, 'PUT');

            if (response.ok) {
                this.showMessage('Ticket acknowledged successfully!', 'success');
                this.loadUnassignedTickets();
                this.loadAssignedTickets();
            } else {
                throw new Error('Failed to acknowledge ticket');
            }
        } catch (error) {
            this.showMessage('Error acknowledging ticket: ' + error.message, 'error');
        }
    }

    showUpdateTicketModal(ticketId) {
        const modalHtml = `
            <div class="modal" id="updateTicketModal">
                <div class="modal-content">
                    <div class="modal-header">
                        <h3 class="modal-title">Update Ticket #${ticketId}</h3>
                        <button class="close" onclick="engineerDashboard.closeModal('updateTicketModal')">&times;</button>
                    </div>
                    <div class="modal-body">
                        <form id="updateTicketForm">
                            <div class="form-row">
                                <div class="form-group">
                                    <label>Status:</label>
                                    <select class="form-control" id="ticketStatus">
                                        <option value="CREATED">Created</option>
                                        <option value="ACKNOWLEDGED">Acknowledged</option>
                                        <option value="IN_PROGRESS">In Progress</option>
                                        <option value="CLOSED">Closed</option>
                                    </select>
                                </div>
                                <div class="form-group">
                                    <label>Tentative Resolution Date:</label>
                                    <input type="date" class="form-control" id="resolutionDate">
                                </div>
                            </div>
                            <div class="form-group">
                                <label>Engineer Comments:</label>
                                <textarea class="form-control" id="engineerComment" rows="3" placeholder="Add your comments about this ticket..."></textarea>
                            </div>
                            <div style="text-align: right;">
                                <button type="button" class="btn btn-secondary" onclick="engineerDashboard.closeModal('updateTicketModal')">Cancel</button>
                                <button type="button" class="btn btn-primary" onclick="engineerDashboard.updateTicket(${ticketId})">Update Ticket</button>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        `;

        document.body.insertAdjacentHTML('beforeend', modalHtml);
        document.getElementById('updateTicketModal').style.display = 'block';
    }

    async updateTicket(ticketId) {
        const status = document.getElementById('ticketStatus').value;
        const resolutionDate = document.getElementById('resolutionDate').value || null;
        const engineerComment = document.getElementById('engineerComment').value.trim();

        const updateData = {
            status,
            tentativeResolutionDate: resolutionDate,
            engineerComment
        };

        try {
            const response = await this.apiCall(`/api/engineer/tickets/${ticketId}/update`, 'PUT', updateData);

            if (response.ok) {
                this.showMessage('Ticket updated successfully!', 'success');
                this.closeModal('updateTicketModal');
                this.loadAssignedTickets();
                this.loadUnassignedTickets();
            } else {
                throw new Error('Failed to update ticket');
            }
        } catch (error) {
            this.showMessage('Error updating ticket: ' + error.message, 'error');
        }
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
                        <button class="close" onclick="engineerDashboard.closeModal('commentsModal')">&times;</button>
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

    showAddCommentModal(ticketId) {
        const modalHtml = `
            <div class="modal" id="addCommentModal">
                <div class="modal-content">
                    <div class="modal-header">
                        <h3 class="modal-title">Add Comment to Ticket #${ticketId}</h3>
                        <button class="close" onclick="engineerDashboard.closeModal('addCommentModal')">&times;</button>
                    </div>
                    <div class="modal-body">
                        <form id="addCommentForm">
                            <div class="form-group">
                                <label>Your Comment:</label>
                                <textarea class="form-control" id="commentContent" required rows="4" placeholder="Enter your comment..."></textarea>
                            </div>
                            <div style="text-align: right;">
                                <button type="button" class="btn btn-secondary" onclick="engineerDashboard.closeModal('addCommentModal')">Cancel</button>
                                <button type="button" class="btn btn-primary" onclick="engineerDashboard.addComment(${ticketId})">Add Comment</button>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        `;

        document.body.insertAdjacentHTML('beforeend', modalHtml);
        document.getElementById('addCommentModal').style.display = 'block';
    }

    async addComment(ticketId) {
        const content = document.getElementById('commentContent').value.trim();

        if (!content) {
            this.showMessage('Please enter a comment', 'error');
            return;
        }

        try {
            const response = await this.apiCall(`/api/engineer/tickets/${ticketId}/comments`, 'POST', {
                content
            });

            if (response.ok) {
                this.showMessage('Comment added successfully!', 'success');
                this.closeModal('addCommentModal');
            } else {
                throw new Error('Failed to add comment');
            }
        } catch (error) {
            this.showMessage('Error adding comment: ' + error.message, 'error');
        }
    }

    async deleteTicket(ticketId) {
        if (!confirm('Are you sure you want to delete this ticket? This action cannot be undone.')) {
            return;
        }

        try {
            const response = await this.apiCall(`/api/engineer/tickets/${ticketId}`, 'DELETE');

            if (response.ok) {
                this.showMessage('Ticket deleted successfully!', 'success');
                this.loadUnassignedTickets();
                this.loadAssignedTickets();
            } else {
                throw new Error('Failed to delete ticket');
            }
        } catch (error) {
            this.showMessage('Error deleting ticket: ' + error.message, 'error');
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
                this.loadCustomers();
            } else {
                const errorData = await response.json();
                throw new Error(errorData.message || 'Failed to create customer');
            }
        } catch (error) {
            this.showMessage('Error creating customer: ' + error.message, 'error');
        }
    }

    // Utility methods
    async apiCall(url, method = 'GET', body = null) {
        const headers = {
            'Content-Type': 'application/json',
            'X-User-ID': this.currentUser.id,
            'X-User-Role': this.currentUser.role,
            'X-Username': this.currentUser.username
        };

        const config = {method, headers};
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

    truncateText(text, maxLength) {
        return text.length > maxLength ? text.substring(0, maxLength) + '...' : text;
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
let engineerDashboard;
document.addEventListener('DOMContentLoaded', () => {
    engineerDashboard = new EngineerDashboard();
});