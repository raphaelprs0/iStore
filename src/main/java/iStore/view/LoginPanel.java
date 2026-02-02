package iStore.view;

import iStore.dao.UserDAO;
import iStore.dao.impl.UserDAOImpl;
import iStore.model.User;
import iStore.service.SecurityService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class LoginPanel extends JPanel {
    private MainFrame mainFrame;
    private JTextField emailField;
    private JPasswordField passwordField;
    private UserDAO userDAO;

    public LoginPanel(MainFrame frame) {
        this.mainFrame = frame;
        this.userDAO = new UserDAOImpl();

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(50, 100, 50, 100));

        // LOGIN
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(emailLabel);

        emailField = new JTextField(20);
        emailField.setMaximumSize(new Dimension(300, 30));
        add(emailField);

        add(Box.createRigidArea(new Dimension(0, 10))); // Espace

        JLabel passLabel = new JLabel("Mot de passe:");
        passLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(passLabel);

        passwordField = new JPasswordField(20);
        passwordField.setMaximumSize(new Dimension(300, 30));
        add(passwordField);

        add(Box.createRigidArea(new Dimension(0, 20))); // Espace

        // BOUTONS
        JPanel buttonPanel = new JPanel();
        JButton loginButton = new JButton("Se connecter");
        JButton registerButton = new JButton("S'inscrire (Register)");

        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);
        add(buttonPanel);

        // ACTIONS
        loginButton.addActionListener(this::handleLogin);
        registerButton.addActionListener(e -> handleRegister());
    }

    private void handleLogin(ActionEvent e) {
        String email = emailField.getText();
        String pass = new String(passwordField.getPassword());

        User user = userDAO.getUserByEmail(email);

        if (user == null) {
            JOptionPane.showMessageDialog(this, "Email inconnu.", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Vérification du mot de passe haché
        if (SecurityService.verifyPassword(pass, user.getPassword(), user.getSalt())) {
            JOptionPane.showMessageDialog(this, "Connexion réussie ! Bienvenue " + user.getPseudo());

            // Lancement du Dashboard
            DashboardPanel dashboard = new DashboardPanel(mainFrame, user);
            mainFrame.getMainPanel().add(dashboard, "DASHBOARD");
            mainFrame.showView("DASHBOARD");
        } else {
            JOptionPane.showMessageDialog(this, "Mot de passe incorrect.", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleRegister() {
        // Formulaire popup pour l'inscription
        JTextField mailField = new JTextField();
        JTextField pseudoField = new JTextField();
        JPasswordField passField = new JPasswordField();

        Object[] message = {
                "Email (doit être whitelisté par l'admin):", mailField,
                "Pseudo:", pseudoField,
                "Mot de passe:", passField
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Créer un compte", JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            try {
                String email = mailField.getText();
                String pseudo = pseudoField.getText();
                String pass = new String(passField.getPassword());

                if (email.isEmpty() || pseudo.isEmpty() || pass.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Tous les champs sont obligatoires.", "Erreur", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                // 1. Vérifier si l'email est whitelisté
                if (!userDAO.isEmailWhitelisted(email)) {
                    JOptionPane.showMessageDialog(this, "Erreur: Cet email n'a pas été autorisé par un administrateur.\nDemandez à l'admin de l'ajouter.", "Accès refusé", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // 2. Création du compte sécurisé
                String salt = SecurityService.getSalt();
                String hash = SecurityService.hashPassword(pass, salt);

                // ID 0 (auto), email, pseudo, hash, salt, ROLE USER, whitelisted true
                User newUser = new User(0, email, pseudo, hash, salt, "USER", true);

                userDAO.createUser(newUser);
                JOptionPane.showMessageDialog(this, "Compte créé avec succès ! Connectez-vous.");

            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Erreur lors de la création: " + ex.getMessage());
            }
        }
    }
}