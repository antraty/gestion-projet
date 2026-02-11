-- Seed data
INSERT OR IGNORE INTO users (name, email, password_hash) VALUES ('Admin','admin@example.com','$2a$12$examplehashreplace');
-- Note: replace the password_hash with an actual bcrypt hash if needed.

-- Example tasks
INSERT INTO tasks (title, description, created_at, due_date, priority, category, status, assigned_user_id)
VALUES ('Exemple', 'Tâche exemple', date('now'), date('now','+2 day'), 'Haute', 'Travail', 'À faire', 1);