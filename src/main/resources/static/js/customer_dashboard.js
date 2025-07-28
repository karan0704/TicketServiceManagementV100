// Customer Dashboard functionality
class CustomerDashboard {
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
        if (!user || user.role !== 'CUSTOMER') {
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
                <p>Customer Dashboard - ${this.currentUser.companyName || 'No Company'}</p>
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
                this.loadMyTickets();
                break;
            case 'create':
                this.loadCategories();
                break;
            case 'profile':
                this.loadProfileData();
                break;
        }
    }

    setupEventListeners() {
        // Create ticket form
        const createTicketForm = document.getElementById('createTicketForm');
        if (createTicketForm) {
            createTicketForm.addEventListener('submit', (e) => this.handleCreateTicket(e));
        }

        // Profile update form
        const profileForm = document.getElementById('profileForm');
        if (profileForm) {
            profileForm.addEventListener('submit', (e) => this.handleProfileUpdate(e));
        }

        // Logout button
        const logoutBtn = document.getElementById('logoutBtn');
        if (logoutBtn) {
            logoutBtn.addEventListener('click', () => this.logout());
        }
    }

    async loadInitialData() {
        await this.loadCategories();
        await this.loadMyTickets();
        this.loadProfileData();
    }

    async loadCategories() {
        try {
            const response = await fetch('/api/public/categories');
            const categories = await response.json();

            const categorySelect = document.getElementById('categorySelect');
            if (categorySelect) {
                categorySelect.innerHTML = '<option value="">Select Category (Optional)</option>';
                categories.forEach(category => {
                    const option = document.createElement('option');
                    option.value = category.id;
                    option.textContent = category.name;
                    categorySelect.appendChild(option);
                });
            }
        } catch (error) {
            console.error('Error loading categories:', error);
        }
    }

    async loadMyTickets() {
        try {
            this.showLoading('ticketsList');

            const response = await this.apiCall('/api/customer/tickets');
            const tickets = await response.json();

            this.displayTickets(tickets);
        } catch (error) {
            this.showMessage('Error loading tickets: ' + error.message, 'error');
        } finally {
            this.hideLoading('ticketsList');
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

    async viewAttachments(ticketId) {
        try {
            const attachments = await this.loadTicketAttachments(ticketId);
            this.showAttachmentsModal(ticketId, attachments);
        } catch (error) {
            this.showMessage('Error loading attachments: ' + error.message, 'error');
        }
    }

    showAttachmentsModal(ticketId, attachments) {
        let attachmentsHtml = `
        <div class="modal" id="attachmentsModal">
            <div class="modal-content">
                <div class="modal-header">
                    <h3 class="modal-title">Attachments for Ticket #${ticketId}</h3>
                    <button class="close" onclick="customerDashboard.closeModal('attachmentsModal')">&times;</button>
                </div>
                <div class="modal-body">
    `;

        if (attachments.length === 0) {
            attachmentsHtml += '<div class="message info">No attachments found for this ticket.</div>';
        } else {
            attachmentsHtml += `
            <div class="table-container">
                <table class="table">
                    <thead>
                        <tr>
                            <th>File Name</th>
                            <th>Size</th>
                            <th>Uploaded By</th>
                            <th>Comment</th>
                            <th>Date</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
        `;

            attachments.forEach(attachment => {
                attachmentsHtml += `
                <tr>
                    <td>${attachment.fileName}</td>
                    <td>${this.formatFileSize(attachment.fileSize)}</td>
                    <td>${attachment.uploadedBy.fullName}</td>
                    <td>${attachment.comment || 'No comment'}</td>
                    <td>${this.formatDateTime(attachment.uploadedAt)}</td>
                    <td>
                        <button class="btn btn-primary btn-small" onclick="customerDashboard.downloadAttachment(${attachment.id}, '${attachment.fileName}')">Download</button>
                    </td>
                </tr>
            `;
            });

            attachmentsHtml += '</tbody></table></div>';
        }

        attachmentsHtml += `
                </div>
            </div>
        </div>
    `;

        document.body.insertAdjacentHTML('beforeend', attachmentsHtml);
        document.getElementById('attachmentsModal').style.display = 'block';
    }

    formatFileSize(bytes) {
        if (bytes === 0) return '0 Bytes';
        const k = 1024;
        const sizes = ['Bytes', 'KB', 'MB', 'GB'];
        const i = Math.floor(Math.log(bytes) / Math.log(k));
        return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
    }

    async downloadAttachment(attachmentId, fileName) {
        try {
            const response = await fetch(`/api/customer/attachments/${attachmentId}/download`, {
                method: 'GET',
                headers: {
                    'X-User-ID': this.currentUser.id,
                    'X-User-Role': this.currentUser.role,
                    'X-Username': this.currentUser.username
                }
            });

            if (response.ok) {
                const blob = await response.blob();
                const url = window.URL.createObjectURL(blob);
                const a = document.createElement('a');
                a.href = url;
                a.download = fileName;
                document.body.appendChild(a);
                a.click();
                window.URL.revokeObjectURL(url);
                document.body.removeChild(a);
            } else {
                throw new Error('Failed to download file');
            }
        } catch (error) {
            this.showMessage('Error downloading file: ' + error.message, 'error');
        }
    }

    async handleCreateTicket(event) {
        event.preventDefault();

        const description = document.getElementById('ticketDescription').value.trim();
        const categoryId = document.getElementById('categorySelect').value || null;

        if (!description) {
            this.showMessage('Please enter a ticket description', 'error');
            return;
        }

        try {
            const response = await this.apiCall('/api/customer/tickets', 'POST', {
                description,
                categoryId: categoryId ? parseInt(categoryId) : null
            });

            if (response.ok) {
                this.showMessage('Ticket created successfully!', 'success');
                document.getElementById('createTicketForm').reset();
                this.loadMyTickets();
                this.showSection('tickets');
            } else {
                throw new Error('Failed to create ticket');
            }
        } catch (error) {
            this.showMessage('Error creating ticket: ' + error.message, 'error');
        }
    }

    async viewComments(ticketId) {
        try {
            const response = await this.apiCall(`/api/customer/tickets/${ticketId}/comments`);
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
                        <button class="close" onclick="customerDashboard.closeModal('commentsModal')">&times;</button>
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
                        <button class="close" onclick="customerDashboard.closeModal('addCommentModal')">&times;</button>
                    </div>
                    <div class="modal-body">
                        <form id="addCommentForm">
                            <div class="form-group">
                                <label>Your Comment:</label>
                                <textarea class="form-control" id="commentContent" required rows="4" placeholder="Enter your comment..."></textarea>
                            </div>
                            <div style="text-align: right;">
                                <button type="button" class="btn btn-secondary" onclick="customerDashboard.closeModal('addCommentModal')">Cancel</button>
                                <button type="button" class="btn btn-primary" onclick="customerDashboard.addComment(${ticketId})">Add Comment</button>
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
            const response = await this.apiCall(`/api/customer/tickets/${ticketId}/comments`, 'POST', {
                content
            });

            if (response.ok) {
                this.showMessage('Comment added successfully!', 'success');
                this.closeModal('addCommentModal');
                this.loadMyTickets();
            } else {
                throw new Error('Failed to add comment');
            }
        } catch (error) {
            this.showMessage('Error adding comment: ' + error.message, 'error');
        }
    }

    showAddAttachmentModal(ticketId) {
        const modalHtml = `
            <div class="modal" id="addAttachmentModal">
                <div class="modal-content">
                    <div class="modal-header">
                        <h3 class="modal-title">Add Attachment to Ticket #${ticketId}</h3>
                        <button class="close" onclick="customerDashboard.closeModal('addAttachmentModal')">&times;</button>
                    </div>
                    <div class="modal-body">
                        <form id="addAttachmentForm">
                            <div class="form-group">
                                <label>Select File:</label>
                                <input type="file" class="form-control" id="attachmentFile" required>
                            </div>
                            <div class="form-group">
                                <label>Comment (optional):</label>
                                <textarea class="form-control" id="attachmentComment" rows="3" placeholder="Optional comment about this file..."></textarea>
                            </div>
                            <div style="text-align: right;">
                                <button type="button" class="btn btn-secondary" onclick="customerDashboard.closeModal('addAttachmentModal')">Cancel</button>
                                <button type="button" class="btn btn-primary" onclick="customerDashboard.uploadAttachment(${ticketId})">Upload</button>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        `;

        document.body.insertAdjacentHTML('beforeend', modalHtml);
        document.getElementById('addAttachmentModal').style.display = 'block';
    }

    async uploadAttachment(ticketId) {
        const fileInput = document.getElementById('attachmentFile');
        const comment = document.getElementById('attachmentComment').value.trim();

        if (!fileInput.files[0]) {
            this.showMessage('Please select a file', 'error');
            return;
        }

        const formData = new FormData();
        formData.append('file', fileInput.files[0]);
        formData.append('comment', comment);

        try {
            const response = await fetch(`/api/customer/tickets/${ticketId}/attachments`, {
                method: 'POST',
                headers: {
                    'X-User-ID': this.currentUser.id,
                    'X-User-Role': this.currentUser.role,
                    'X-Username': this.currentUser.username
                },
                body: formData
            });

            if (response.ok) {
                this.showMessage('Attachment uploaded successfully!', 'success');
                this.closeModal('addAttachmentModal');
                this.loadMyTickets();
            } else {
                throw new Error('Failed to upload attachment');
            }
        } catch (error) {
            this.showMessage('Error uploading attachment: ' + error.message, 'error');
        }
    }

    async loadTicketAttachments(ticketId) {
        try {
            const response = await this.apiCall(`/api/customer/tickets/${ticketId}/attachments`);
            if (response.ok) {
                return await response.json();
            }
            return [];
        } catch (error) {
            console.error('Error loading attachments:', error);
            return [];
        }
    }

    loadProfileData() {
        document.getElementById('profileFullName').value = this.currentUser.fullName || '';
        document.getElementById('profileEmail').value = this.currentUser.email || '';
        document.getElementById('profilePhone').value = this.currentUser.phoneNumber || '';
        document.getElementById('profileAddress').value = this.currentUser.address || '';
        document.getElementById('profileCompanyName').value = this.currentUser.companyName || '';
    }

    async handleProfileUpdate(event) {
        event.preventDefault();

        const profileData = {
            fullName: document.getElementById('profileFullName').value.trim(),
            email: document.getElementById('profileEmail').value.trim(),
            phoneNumber: document.getElementById('profilePhone').value.trim(),
            address: document.getElementById('profileAddress').value.trim(),
            companyName: document.getElementById('profileCompanyName').value.trim()
        };

        try {
            const response = await this.apiCall('/api/customer/profile', 'PUT', profileData);

            if (response.ok) {
                // Update localStorage
                this.currentUser = {...this.currentUser, ...profileData};
                localStorage.setItem('currentUser', JSON.stringify(this.currentUser));

                this.showMessage('Profile updated successfully!', 'success');
                this.setupUserInfo(); // Refresh header
            } else {
                throw new Error('Failed to update profile');
            }
        } catch (error) {
            this.showMessage('Error updating profile: ' + error.message, 'error');
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
let customerDashboard;
document.addEventListener('DOMContentLoaded', () => {
    customerDashboard = new CustomerDashboard();
});