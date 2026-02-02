package iStore.view;

import iStore.dao.UserDAO;
import iStore.dao.impl.UserDAOImpl;
import iStore.model.User;
import iStore.service.SecurityService;

import javax.swing.*;
import java.awt.*;

public class ProfilePanel extends JPanel {
    private User currentUser;
    private MainFrame mainFrame;
    private UserDAO userDAO;

    private JTextField pseudoField;
    private JPasswordField passField;

    public ProfilePanel(MainFrame frame, User user) {
        this.mainFrame = frame;
        this.currentUser = user;
        this.userDAO = new UserDAOImpl();

        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        // TITRE
        JLabel title = new JLabel("Mon Profil : " + currentUser.getEmail());
        title.setFont(new Font("Arial", Font.BOLD, 20));
        title.setHorizontalAlignment(SwingConstants.CENTER);
        add(title, BorderLayout.NORTH);

        // FORMULAIRE
        JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 10));

        formPanel.add(new JLabel("Pseudo :"));
        pseudoField = new JTextField(currentUser.getPseudo());
        formPanel.add(pseudoField);

        formPanel.add(new JLabel("Nouveau Mot de passe :"));
        passField = new JPasswordField();
        formPanel.add(passField);

        formPanel.add(new JLabel("(Laisser vide pour ne pas changer)"));
        formPanel.add(new JLabel(""));

        add(formPanel, BorderLayout.CENTER);

        // BOUTONS ACTIONS
        JPanel btnPanel = new JPanel(new FlowLayout());

        JButton btnUpdate = new JButton("Mettre à jour");
        btnUpdate.setForeground(Color.BLACK);

        JButton btnDeleteSelf = new JButton("Supprimer mon compte");
        btnDeleteSelf.setForeground(Color.BLACK);

        btnPanel.add(btnUpdate);
        btnPanel.add(btnDeleteSelf);
        add(btnPanel, BorderLayout.SOUTH);

        // LOGIQUE

        // 1. UPDATE
        btnUpdate.addActionListener(e -> {
            String newPseudo = pseudoField.getText();
            String newPass = new String(passField.getPassword());

            if (newPseudo.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Pseudo vide interdit.");
                return;
            }

            currentUser.setPseudo(newPseudo);

            if (!newPass.isEmpty()) {
                String newSalt = SecurityService.getSalt();
                String newHash = SecurityService.hashPassword(newPass, newSalt);
                currentUser.setPassword(newHash);
                currentUser.setSalt(newSalt);
            }

            try {
                userDAO.updateUser(currentUser);
                JOptionPane.showMessageDialog(this, "Profil mis à jour !");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        // 2. DELETE SELF
        btnDeleteSelf.addActionListener(e -> {
            if ("ADMIN".equals(currentUser.getRole())) {
                JOptionPane.showMessageDialog(this, "Impossible : Un admin ne peut pas se supprimer (sécurité).");
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(this,
                    "Voulez-vous vraiment supprimer VOTRE compte ?\nIrréversible.",
                    "Danger", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                userDAO.deleteUser(currentUser.getId());
                JOptionPane.showMessageDialog(this, "Compte supprimé.");
                mainFrame.showView("LOGIN");
            }
        });
    }
}