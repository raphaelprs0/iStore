package iStore.main;

import iStore.config.DatabaseManager;
import iStore.dao.UserDAO;
import iStore.dao.impl.UserDAOImpl;
import iStore.model.User;
import iStore.service.SecurityService;
import iStore.view.MainFrame;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 1. Initialiser la base
        DatabaseManager.initDatabase();

        // 2. Créer Admin par défaut
        createDefaultAdmin();

        // 3. Lancer
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }

    private static void createDefaultAdmin() {
        try {
            UserDAO userDAO = new UserDAOImpl();
            if (userDAO.getUserByEmail("admin@istore.com") == null) {
                String salt = SecurityService.getSalt();
                String hash = SecurityService.hashPassword("admin", salt);
                User admin = new User(0, "admin@istore.com", "Administrateur", hash, salt, "ADMIN", true);
                userDAO.createUser(admin);
                System.out.println("Admin créé.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}