package com.taskmanager.dao;

import com.taskmanager.models.User;

import java.sql.*;

public class UserDAO extends BaseDAO {

    public int create(User u) {
        String sql = "INSERT INTO users(name, email, password_hash) VALUES(?,?,?)";
        try (PreparedStatement ps = connection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, u.getName());
            ps.setString(2, u.getEmail());
            ps.setString(3, u.getPasswordHash());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }
            return -1;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public User findByEmail(String email) {
        String sql = "SELECT id, name, email, password_hash FROM users WHERE email = ?";
        try (PreparedStatement ps = connection().prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    User u = new User(rs.getInt("id"), rs.getString("name"), rs.getString("email"), rs.getString("password_hash"));
                    return u;
                }
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}