package com.gestionprojet.dao;

import com.gestionprojet.models.Projet;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ProjetDAO {
    
    public boolean creer(Projet projet) {
        String query = "INSERT INTO projets (nom, description, date_echeance, proprietaire_id) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, projet.getNom());
            stmt.setString(2, projet.getDescription());
            stmt.setDate(3, projet.getDateEcheance() != null ? Date.valueOf(projet.getDateEcheance()) : null);
            stmt.setInt(4, projet.getProprietaireId());
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    projet.setId(rs.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Projet> getByProprietaire(int proprietaireId) {
        List<Projet> projets = new ArrayList<>();
        String query = "SELECT * FROM projets WHERE proprietaire_id = ? ORDER BY date_creation DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, proprietaireId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                projets.add(mapResultSetToProjet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return projets;
    }

    public Projet getById(int id) {
        String query = "SELECT * FROM projets WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToProjet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean update(Projet projet) {
        String query = "UPDATE projets SET nom = ?, description = ?, date_echeance = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, projet.getNom());
            stmt.setString(2, projet.getDescription());
            stmt.setDate(3, projet.getDateEcheance() != null ? Date.valueOf(projet.getDateEcheance()) : null);
            stmt.setInt(4, projet.getId());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean delete(int id) {
        String query = "DELETE FROM projets WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private Projet mapResultSetToProjet(ResultSet rs) throws SQLException {
        Projet projet = new Projet();
        projet.setId(rs.getInt("id"));
        projet.setNom(rs.getString("nom"));
        projet.setDescription(rs.getString("description"));
        if (rs.getTimestamp("date_creation") != null) {
            projet.setDateCreation(rs.getTimestamp("date_creation").toLocalDateTime());
        }
        Date dateEcheance = rs.getDate("date_echeance");
        if (dateEcheance != null) {
            projet.setDateEcheance(dateEcheance.toLocalDate());
        }
        projet.setProprietaireId(rs.getInt("proprietaire_id"));
        return projet;
    }
}