import com.taskmanager.services.AuthService;
import com.taskmanager.models.User;


public class AuthServiceTest {
    public static void main(String[] args) {
        com.taskmanager.config.AppConfig.load();
        com.taskmanager.services.DatabaseService.getInstance().initialize();
        AuthService auth = AuthService.getInstance();

        // Test inscription
        try {
            User u = auth.register("Alice", "alice@example.com", "motdepasse");
            System.out.println("Inscription OK: " + u.getEmail());
        } catch (Exception e) {
            System.out.println("Erreur inscription: " + e.getMessage());
        }

        // Test connexion
        try {
            User u = auth.login("alice@example.com", "motdepasse");
            System.out.println("Connexion OK: " + u.getName());
        } catch (Exception e) {
            System.out.println("Erreur connexion: " + e.getMessage());
        }

        // Test mauvais mot de passe
        try {
            auth.login("alice@example.com", "mauvais");
        } catch (Exception e) {
            System.out.println("Erreur attendue: " + e.getMessage());
        }
    }
}
