import com.taskmanager.dao.UserDAO;
import com.taskmanager.models.User;


public class UserDAOTest {
    public static void main(String args[]){
        // Charger la configuration avant d'initialiser la base de données
        com.taskmanager.config.AppConfig.load();
        com.taskmanager.services.DatabaseService.getInstance().initialize();

        UserDAO dao = new UserDAO();

        // Test création utilisateur
        User u = new User();
        u.setName("Test");
        u.setEmail("test@example.com");
        u.setPasswordHash("hash");
        int id = dao.create(u);
        System.out.println("ID créé: " + id);

        // Test recherche par email
        User found = dao.findByEmail("test@example.com");
        System.out.println("Trouvé: " + (found != null ? found.getName() : "Aucun"));

        // Test emailExists
        boolean exists = dao.emailExists("test@example.com");
        System.out.println("Existe: " + exists);
    }
}
