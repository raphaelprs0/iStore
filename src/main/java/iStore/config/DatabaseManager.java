package iStore.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {
    private static final String URL = "jdbc:sqlite:istore.db";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    public static void initDatabase() {
        String createUsers = "CREATE TABLE IF NOT EXISTS users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "email TEXT UNIQUE NOT NULL, " +
                "pseudo TEXT, " +
                "password TEXT NOT NULL, " +
                "salt TEXT NOT NULL, " +
                "role TEXT NOT NULL, " +
                "is_whitelisted BOOLEAN DEFAULT 0)";

        String createStores = "CREATE TABLE IF NOT EXISTS stores (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT NOT NULL)";

        String createItems = "CREATE TABLE IF NOT EXISTS items (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT NOT NULL, " +
                "price REAL NOT NULL, " +
                "quantity INTEGER DEFAULT 0, " +
                "store_id INTEGER, " +
                "FOREIGN KEY(store_id) REFERENCES stores(id))";

        // Table de liaison Many-to-Many (Employés <-> Magasins)
        String createAccess = "CREATE TABLE IF NOT EXISTS store_access (" +
                "user_id INTEGER, " +
                "store_id INTEGER, " +
                "PRIMARY KEY (user_id, store_id), " +
                "FOREIGN KEY(user_id) REFERENCES users(id), " +
                "FOREIGN KEY(store_id) REFERENCES stores(id))";

        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            stmt.execute(createUsers);
            stmt.execute(createStores);
            stmt.execute(createItems);
            stmt.execute(createAccess);

            // Création de l'admin par défaut
            // Appelle de SecurityService pour hasher le mot de passe
            System.out.println("Base de données initialisée avec succès.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}