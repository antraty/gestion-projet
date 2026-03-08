package com.taskmanager.services;

import com.taskmanager.dao.TaskDAO;
import com.taskmanager.models.Task;

import java.time.LocalDate;
import java.util.List;

public class TaskService {
    private static TaskService instance;
    private final TaskDAO taskDAO = new TaskDAO();

    private TaskService() {}

    public static TaskService getInstance() {
        if (instance == null) instance = new TaskService();
        return instance;
    }

    public int create(Task t) {
        // Règle : Une tâche doit toujours avoir une date de création
        if (t.getCreatedAt() == null) {
            t.setCreatedAt(LocalDate.now());
        }
        
        // Règle : Si aucun statut n'est défini, on met "À faire"
        if (t.getStatus() == null || t.getStatus().isEmpty()) {
            t.setStatus("À faire");
        }
        return taskDAO.create(t);
    }
    public List<Task> findAll() { return taskDAO.findAll(); }
    public Task findById(int id) { return taskDAO.findById(id); }
    public void update(Task t) { taskDAO.update(t); }
    public void delete(int id) { taskDAO.delete(id); }

    public List<Task> search(String q, LocalDate date, String priority, String category, String status) {
    return taskDAO.search(q, date, priority, category, status);
    }
}