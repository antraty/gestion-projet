package com.gestionprojet.dao;

import com.gestionprojet.models.Priorite;
import com.gestionprojet.models.Tache;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TacheDAO {
    
    // Classe interne pour les tâches avec projet
    public static class TacheAvecProjet {
        private Tache tache;
        private String nomProjet;
        
        public TacheAvecProjet(Tache tache, String nomProjet) {
            this.tache = tache;
            this.nomProjet = nomProjet;
        }
        
        public Tache getTache() { return tache; }
        public String getNomProjet() { return nomProjet; }
    }
    
    public boolean creer(Tache tache) {
        String query = "INSERT INTO taches (titre, description, priorite, statut, date_echeance, projet_id, assignee_id) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, tache.getTitre());
            stmt.setString(2, tache.getDescription());
            stmt.setString(3, tache.getPriorite().name());
            stmt.setString(4, tache.getStatut().name());
            stmt.setDate(5, tache.getDateEcheance() != null ? Date.valueOf(tache.getDateEcheance()) : null);
            stmt.setInt(6, tache.getProjetId());
            stmt.setInt(7, tache.getAssigneeId());
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    tache.setId(rs.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Tache> getByProjet(int projetId) {
        List<Tache> taches = new ArrayList<>();
        String query = "SELECT * FROM taches WHERE projet_id = ? ORDER BY date_echeance ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, projetId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                taches.add(mapResultSetToTache(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return taches;
    }

    public List<Tache> getByAssignee(int assigneeId) {
        List<Tache> taches = new ArrayList<>();
        String query = "SELECT * FROM taches WHERE assignee_id = ? ORDER BY date_echeance ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, assigneeId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                taches.add(mapResultSetToTache(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return taches;
    }
    
    public List<TacheAvecProjet> getTachesAvecProjetByAssignee(int assigneeId) {
        List<TacheAvecProjet> resultats = new ArrayList<>();
        String query = """
            SELECT t.*, p.nom as nom_projet 
            FROM taches t 
            JOIN projets p ON t.projet_id = p.id 
            WHERE t.assignee_id = ? 
            ORDER BY t.date_echeance ASC
            """;
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, assigneeId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Tache tache = mapResultSetToTache(rs);
                String nomProjet = rs.getString("nom_projet");
                resultats.add(new TacheAvecProjet(tache, nomProjet));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resultats;
    }

    public List<Tache> getTachesPourAujourdhui(int assigneeId) {
        List<Tache> taches = new ArrayList<>();
        String query = "SELECT * FROM taches WHERE assignee_id = ? AND date_echeance = CURRENT_DATE AND statut != 'TERMINE' ORDER BY priorite DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, assigneeId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                taches.add(mapResultSetToTache(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return taches;
    }

    public List<Tache> getTachesCetteSemaine(int assigneeId) {
        List<Tache> taches = new ArrayList<>();
        String query = "SELECT * FROM taches WHERE assignee_id = ? AND YEARWEEK(date_echeance) = YEARWEEK(CURRENT_DATE) AND statut != 'TERMINE' ORDER BY date_echeance ASC, priorite DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, assigneeId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                taches.add(mapResultSetToTache(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return taches;
    }

    public boolean update(Tache tache) {
        String query = "UPDATE taches SET titre = ?, description = ?, priorite = ?, statut = ?, date_echeance = ?, assignee_id = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, tache.getTitre());
            stmt.setString(2, tache.getDescription());
            stmt.setString(3, tache.getPriorite().name());
            stmt.setString(4, tache.getStatut().name());
            stmt.setDate(5, tache.getDateEcheance() != null ? Date.valueOf(tache.getDateEcheance()) : null);
            stmt.setInt(6, tache.getAssigneeId());
            stmt.setInt(7, tache.getId());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean delete(int id) {
        String query = "DELETE FROM taches WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private Tache mapResultSetToTache(ResultSet rs) throws SQLException {
        Tache tache = new Tache();
        tache.setId(rs.getInt("id"));
        tache.setTitre(rs.getString("titre"));
        tache.setDescription(rs.getString("description"));
        tache.setPriorite(Priorite.valueOf(rs.getString("priorite")));
        tache.setStatut(Tache.StatutTache.valueOf(rs.getString("statut")));
        if (rs.getTimestamp("date_creation") != null) {
            tache.setDateCreation(rs.getTimestamp("date_creation").toLocalDateTime());
        }
        Date dateEcheance = rs.getDate("date_echeance");
        if (dateEcheance != null) {
            tache.setDateEcheance(dateEcheance.toLocalDate());
        }
        tache.setProjetId(rs.getInt("projet_id"));
        tache.setAssigneeId(rs.getInt("assignee_id"));
        return tache;
    }
}