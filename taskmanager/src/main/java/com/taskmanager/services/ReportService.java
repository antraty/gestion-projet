package com.taskmanager.services;

import com.taskmanager.dao.TaskDAO;

public class ReportService {
    private static ReportService instance;
    private final TaskDAO taskDAO = new TaskDAO();

    private ReportService() {}

    public static ReportService getInstance() {
        if (instance == null) instance = new ReportService();
        return instance;
    }

    public int totalTasks() { return taskDAO.countAll(); }
    public int overdueTasks() { return taskDAO.countOverdue(); }
    public int tasksForToday() { return taskDAO.countForToday(); }
    public int highPriorityTasks() { return taskDAO.countByPriority("Urgent"); }
}