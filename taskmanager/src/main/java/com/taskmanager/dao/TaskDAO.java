package com.taskmanager.dao;

import com.taskmanager.models.Task;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TaskDAO extends BaseDAO {

    public int create(Task t) {
        String sql = "INSERT INTO tasks(title, description, created_at, due_date, priority, category, status, assigned_user_id) VALUES(?,?,?,?,?,?,?,?)";
        try (PreparedStatement ps = connection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, t.getTitle());
            ps.setString(2, t.getDescription());
            ps.setString(3, t.getCreatedAt().toString());
            ps.setString(4, t.getDueDate() != null ? t.getDueDate().toString() : null);
            ps.setString(5, t.getPriority());
            ps.setString(6, t.getCategory());
            ps.setString(7, t.getStatus());
            ps.setInt(8, t.getAssignedUserId());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }
            return -1;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Task> findAll() {
        String sql = "SELECT * FROM tasks ORDER BY created_at DESC";
        List<Task> list = new ArrayList<>();
        try (Statement st = connection().createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Task t = mapRow(rs);
                list.add(t);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    public Task findById(int id) {
        String sql = "SELECT * FROM tasks WHERE id = ?";
        try (PreparedStatement ps = connection().prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public void update(Task t) {
        String sql = "UPDATE tasks SET title=?, description=?, due_date=?, priority=?, category=?, status=?, assigned_user_id=? WHERE id=?";
        try (PreparedStatement ps = connection().prepareStatement(sql)) {
            ps.setString(1, t.getTitle());
            ps.setString(2, t.getDescription());
            ps.setString(3, t.getDueDate() != null ? t.getDueDate().toString() : null);
            ps.setString(4, t.getPriority());
            ps.setString(5, t.getCategory());
            ps.setString(6, t.getStatus());
            ps.setInt(7, t.getAssignedUserId());
            ps.setInt(8, t.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void delete(int id) {
        String sql = "DELETE FROM tasks WHERE id = ?";
        try (PreparedStatement ps = connection().prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // TaskDAO.java
    public List<Task> search(String q, LocalDate date, String priority, String category, String status) {
        StringBuilder sql = new StringBuilder("SELECT * FROM tasks WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (q != null && !q.isEmpty()) {
            sql.append(" AND (title LIKE ? OR description LIKE ?)");
            params.add("%" + q + "%");
            params.add("%" + q + "%");
        }
        if (date != null) {
            sql.append(" AND due_date = ?");
            params.add(date.toString());
        }
        if (priority != null && !priority.isEmpty()) {
            sql.append(" AND priority = ?");
            params.add(priority);
        }
        if (category != null && !category.isEmpty()) {
            sql.append(" AND category = ?");
            params.add(category);
        }
        if (status != null && !status.isEmpty()) {
            sql.append(" AND status = ?");
            params.add(status);
        }
        sql.append(" ORDER BY created_at DESC");

        try (PreparedStatement ps = connection().prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            List<Task> list = new ArrayList<>();
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
            return list;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Task mapRow(ResultSet rs) throws SQLException {
        Task t = new Task();
        t.setId(rs.getInt("id"));
        t.setTitle(rs.getString("title"));
        t.setDescription(rs.getString("description"));
        String created = rs.getString("created_at");
        t.setCreatedAt(created != null ? LocalDate.parse(created) : null);
        String due = rs.getString("due_date");
        t.setDueDate(due != null ? LocalDate.parse(due) : null);
        t.setPriority(rs.getString("priority"));
        t.setCategory(rs.getString("category"));
        t.setStatus(rs.getString("status"));
        t.setAssignedUserId(rs.getInt("assigned_user_id"));
        return t;
    }

    public int countAll() {
        String sql = "SELECT COUNT(*) FROM tasks";
        try (Statement st = connection().createStatement(); ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {}
        return 0;
    }

    public int countOverdue() {
        String sql = "SELECT COUNT(*) FROM tasks WHERE due_date < CURDATE() AND status != 'Terminé'";
        try (Statement st = connection().createStatement(); ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {}
        return 0;
    }

    public int countForToday() {
        String sql = "SELECT COUNT(*) FROM tasks WHERE due_date = CURDATE()";
        try (Statement st = connection().createStatement(); ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {}
        return 0;
    }

    public int countByPriority(String priority) {
        String sql = "SELECT COUNT(*) FROM tasks WHERE priority = ?";
        try (PreparedStatement ps = connection().prepareStatement(sql)) {
            ps.setString(1, priority);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {}
        return 0;
    }
}