-- Insert default engineer (admin)
INSERT IGNORE INTO users (username, password, full_name, email, role, is_default_engineer, created_at, updated_at) 
VALUES ('default_engineer', 'admin123', 'Default Engineer', 'admin@company.com', 'ENGINEER', true, NOW(), NOW());

-- Insert sample categories
INSERT IGNORE INTO ticket_categories (name, description) VALUES 
('Technical Issue', 'Technical problems and bugs'),
('Feature Request', 'New feature requests'),
('Support', 'General support requests'),
('Bug Report', 'Bug reports and fixes');

-- Insert sample customer
INSERT IGNORE INTO users (username, password, full_name, email, role, is_default_engineer, created_at, updated_at) 
VALUES ('customer1', 'pass123', 'John Doe', 'john@example.com', 'CUSTOMER', false, NOW(), NOW());

-- Insert sample engineer
INSERT IGNORE INTO users (username, password, full_name, email, role, is_default_engineer, created_at, updated_at) 
VALUES ('engineer1', 'pass123', 'Jane Smith', 'jane@company.com', 'ENGINEER', false, NOW(), NOW());
