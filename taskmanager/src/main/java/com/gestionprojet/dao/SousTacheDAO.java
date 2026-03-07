package com.gestionprojet.dao;

import com.gestionprojet.models.SousTache;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SousTacheDAO {
    
    public boolean creer(SousTache sousTache) {
        String query = "INSERT INTO sous_taches (titre, tache_id) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, sousTache.getTitre());
            stmt.setInt(2, sousTache.getTacheId());
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    sousTache.setId(rs.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<SousTache> getByTache(int tacheId) {
        List<SousTache> sousTaches = new ArrayList<>();
        String query = "SELECT * FROM sous_taches WHERE tache_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, tacheId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                sousTaches.add(mapResultSetToSousTache(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return sousTaches;
    }

    public boolean updateStatut(int id, boolean estTerminee) {
        String query = "UPDATE sous_taches SET est_terminee = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setBoolean(1, estTerminee);
            stmt.setInt(2, id);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean delete(int id) {
        String query = "DELETE FROM sous_taches WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private SousTache mapResultSetToSousTache(ResultSet rs) throws SQLException {
        SousTache sousTache = new SousTache();
        sousTache.setId(rs.getInt("id"));
        sousTache.setTitre(rs.getString("titre"));
        sousTache.setEstTerminee(rs.getBoolean("est_terminee"));
        sousTache.setTacheId(rs.getInt("tache_id"));
        return sousTache;
    }
}