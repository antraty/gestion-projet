package com.taskmanager.dao;

import com.taskmanager.services.DatabaseService;

import java.sql.Connection;

public abstract class BaseDAO {
    protected Connection connection() {
        return DatabaseService.getInstance().getConnection();
    }
}