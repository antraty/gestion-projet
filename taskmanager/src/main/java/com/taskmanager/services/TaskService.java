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

    public int create(Task t) { return taskDAO.create(t); }
    public List<Task> findAll() { return taskDAO.findAll(); }
    public Task findById(int id) { return taskDAO.findById(id); }
    public void update(Task t) { taskDAO.update(t); }
    public void delete(int id) { taskDAO.delete(id); }

    public List<Task> search(String q, LocalDate date) { return taskDAO.search(q, date); }
}