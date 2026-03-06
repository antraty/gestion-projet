package com.taskmanager.services;

import com.taskmanager.models.Task;
import com.taskmanager.models.TaskStatus;
import com.taskmanager.models.TaskPriority;
import com.taskmanager.models.TaskCategory;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
public class TaskService {
    private static TaskService instance;
    private DatabaseService dbService;
    
    private TaskService() {
        dbService = DatabaseService.getInstance();
    }
    
    public static TaskService getInstance() {
        if (instance == null) {
            instance = new TaskService();
        }
        return instance;
    }
    
    // Create
    public boolean createTask(Task task) {
        String query = "INSERT INTO tasks (title, description, creation_date, due_date, " +
                      "priority, category, status, user_id) " +
                      "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = dbService.getConnection().prepareStatement(query)) {
            pstmt.setString(1, task.getTitle());
            pstmt.setString(2, task.getDescription());
            pstmt.setTimestamp(3, Timestamp.valueOf(task.getCreationDate()));
            pstmt.setDate(4, task.getDueDate() != null ? Date.valueOf(task.getDueDate()) : null);
            pstmt.setString(5, task.getPriority().name());
            pstmt.setString(6, task.getCategory().name());
            pstmt.setString(7, task.getStatus().name());
            pstmt.setInt(8, task.getUserId());
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Read all tasks for user
    public List<Task> getUserTasks(int userId) {
        List<Task> tasks = new ArrayList<>();
        String query = "SELECT * FROM tasks WHERE user_id = ? ORDER BY due_date";
        
        try (PreparedStatement pstmt = dbService.getConnection().prepareStatement(query)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                tasks.add(mapResultSetToTask(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return tasks;
    }
    
    // Read filtered tasks
    public List<Task> getFilteredTasks(int userId, String searchText, 
                                      TaskStatus status, TaskPriority priority,
                                      TaskCategory category, LocalDate dueDate) {
        List<Task> tasks = new ArrayList<>();
        StringBuilder query = new StringBuilder("SELECT * FROM tasks WHERE user_id = ?");
        List<Object> params = new ArrayList<>();
        params.add(userId);
        
        if (searchText != null && !searchText.isEmpty()) {
            query.append(" AND (title LIKE ? OR description LIKE ?)");
            params.add("%" + searchText + "%");
            params.add("%" + searchText + "%");
        }
        
        if (status != null) {
            query.append(" AND status = ?");
            params.add(status.name());
        }
        
        if (priority != null) {
            query.append(" AND priority = ?");
            params.add(priority.name());
        }
        
        if (category != null) {
            query.append(" AND category = ?");
            params.add(category.name());
        }
        
        if (dueDate != null) {
            query.append(" AND due_date = ?");
            params.add(Date.valueOf(dueDate));
        }
        
        query.append(" ORDER BY due_date");
        
        try (PreparedStatement pstmt = dbService.getConnection().prepareStatement(query.toString())) {
            for (int i = 0; i < params.size(); i++) {
                Object param = params.get(i);
                if (param instanceof String) {
                    pstmt.setString(i + 1, (String) param);
                } else if (param instanceof Integer) {
                    pstmt.setInt(i + 1, (Integer) param);
                } else if (param instanceof Date) {
                    pstmt.setDate(i + 1, (Date) param);
                }
            }
            
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                tasks.add(mapResultSetToTask(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return tasks;
    }
    
    // Update
    public boolean updateTask(Task task) {
        String query = "UPDATE tasks SET title = ?, description = ?, due_date = ?, " +
                      "priority = ?, category = ?, status = ? " +
                      "WHERE id = ? AND user_id = ?";
        
        try (PreparedStatement pstmt = dbService.getConnection().prepareStatement(query)) {
            pstmt.setString(1, task.getTitle());
            pstmt.setString(2, task.getDescription());
            pstmt.setDate(3, task.getDueDate() != null ? Date.valueOf(task.getDueDate()) : null);
            pstmt.setString(4, task.getPriority().name());
            pstmt.setString(5, task.getCategory().name());
            pstmt.setString(6, task.getStatus().name());
            pstmt.setInt(7, task.getId());
            pstmt.setInt(8, task.getUserId());
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Delete
    public boolean deleteTask(int taskId, int userId) {
        String query = "DELETE FROM tasks WHERE id = ? AND user_id = ?";
        
        try (PreparedStatement pstmt = dbService.getConnection().prepareStatement(query)) {
            pstmt.setInt(1, taskId);
            pstmt.setInt(2, userId);
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Statistiques pour le dashboard
    public int getTotalTasks(int userId) {
        return getTasksCount(userId, null, null);
    }
    
    public int getOverdueTasks(int userId) {
        String query = "SELECT COUNT(*) FROM tasks WHERE user_id = ? AND due_date < ? AND status != ?";
        
        try (PreparedStatement pstmt = dbService.getConnection().prepareStatement(query)) {
            pstmt.setInt(1, userId);
            pstmt.setDate(2, Date.valueOf(LocalDate.now()));
            pstmt.setString(3, TaskStatus.TERMINE.name());
            
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    public int getTasksForToday(int userId) {
        String query = "SELECT COUNT(*) FROM tasks WHERE user_id = ? AND due_date = ? AND status != ?";
        
        try (PreparedStatement pstmt = dbService.getConnection().prepareStatement(query)) {
            pstmt.setInt(1, userId);
            pstmt.setDate(2, Date.valueOf(LocalDate.now()));
            pstmt.setString(3, TaskStatus.TERMINE.name());
            
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    public int getHighPriorityTasks(int userId) {
        String query = "SELECT COUNT(*) FROM tasks WHERE user_id = ? AND (priority = ? OR priority = ?) AND status != ?";
        
        try (PreparedStatement pstmt = dbService.getConnection().prepareStatement(query)) {
            pstmt.setInt(1, userId);
            pstmt.setString(2, TaskPriority.HAUTE.name());
            pstmt.setString(3, TaskPriority.URGENT.name());
            pstmt.setString(4, TaskStatus.TERMINE.name());
            
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    public List<Integer> getTasksCountByStatus(int userId) {
        List<Integer> counts = new ArrayList<>();
        String query = "SELECT status, COUNT(*) FROM tasks WHERE user_id = ? GROUP BY status";
        
        // Initialiser avec 0 pour chaque statut
        for (int i = 0; i < TaskStatus.values().length; i++) {
            counts.add(0);
        }
        
        try (PreparedStatement pstmt = dbService.getConnection().prepareStatement(query)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                String statusStr = rs.getString(1);
                int count = rs.getInt(2);
                
                TaskStatus status = TaskStatus.valueOf(statusStr);
                counts.set(status.ordinal(), count);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return counts;
    }
    
    private int getTasksCount(int userId, TaskStatus status, LocalDate dueDate) {
        StringBuilder query = new StringBuilder("SELECT COUNT(*) FROM tasks WHERE user_id = ?");
        List<Object> params = new ArrayList<>();
        params.add(userId);
        
        if (status != null) {
            query.append(" AND status = ?");
            params.add(status.name());
        }
        
        if (dueDate != null) {
            query.append(" AND due_date = ?");
            params.add(Date.valueOf(dueDate));
        }
        
        try (PreparedStatement pstmt = dbService.getConnection().prepareStatement(query.toString())) {
            for (int i = 0; i < params.size(); i++) {
                Object param = params.get(i);
                if (param instanceof String) {
                    pstmt.setString(i + 1, (String) param);
                } else if (param instanceof Integer) {
                    pstmt.setInt(i + 1, (Integer) param);
                } else if (param instanceof Date) {
                    pstmt.setDate(i + 1, (Date) param);
                }
            }
            
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return 0;
    }
    
    private Task mapResultSetToTask(ResultSet rs) throws SQLException {
        Task task = new Task();
        task.setId(rs.getInt("id"));
        task.setTitle(rs.getString("title"));
        task.setDescription(rs.getString("description"));
        task.setCreationDate(rs.getTimestamp("creation_date").toLocalDateTime());
        
        Date dueDate = rs.getDate("due_date");
        if (dueDate != null) {
            task.setDueDate(dueDate.toLocalDate());
        }
        
        task.setPriority(TaskPriority.valueOf(rs.getString("priority")));
        task.setCategory(TaskCategory.valueOf(rs.getString("category")));
        task.setStatus(TaskStatus.valueOf(rs.getString("status")));
        task.setUserId(rs.getInt("user_id"));
        
        return task;
    }
}