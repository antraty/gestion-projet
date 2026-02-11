-- Schema for TaskManager (SQLite)
PRAGMA foreign_keys = ON;

CREATE TABLE IF NOT EXISTS users (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,
    email TEXT NOT NULL UNIQUE,
    password_hash TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS tasks (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    title TEXT NOT NULL,
    description TEXT,
    created_at TEXT NOT NULL,
    due_date TEXT,
    priority TEXT,
    category TEXT,
    status TEXT,
    assigned_user_id INTEGER,
    FOREIGN KEY (assigned_user_id) REFERENCES users(id) ON DELETE SET NULL
);