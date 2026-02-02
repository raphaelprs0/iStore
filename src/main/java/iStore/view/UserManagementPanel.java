package iStore.view;

import iStore.dao.UserDAO;
import iStore.dao.impl.UserDAOImpl;
import iStore.model.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class UserManagementPanel extends JPanel {
    private UserDAO userDAO;
    private JTable usersTable;
    private DefaultTableModel tableModel;
    private User currentUser;

    public UserManagementPanel(User connectedUser) {
        this.currentUser = connectedUser;
        this.userDAO = new UserDAOImpl();

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // TITRE
        String titleText = "ADMIN".equals(currentUser.getRole()) ? "Administration Utilisateurs" : "Annuaire de mes Collègues";
        JLabel title = new JLabel(titleText);
        title.setFont(new Font("Arial", Font.BOLD, 18));
        add(title, BorderLayout.NORTH);

        // TABLEAU
        String[] columnNames = {"ID", "Email", "Pseudo", "Rôle", "Statut"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        usersTable = new JTable(tableModel);
        usersTable.setRowHeight(25);
        add(new JScrollPane(usersTable), BorderLayout.CENTER);

        // BOUTONS
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JButton btnRefresh = createStandardButton("Rafraîchir");

        if ("ADMIN".equals(currentUser.getRole())) {
            JButton btnWhitelist = createStandardButton("Whitelister Email");
            JButton btnPromote = createStandardButton("Promouvoir Admin");
            JButton btnDelete = createStandardButton("Supprimer");

            btnWhitelist.addActionListener(e -> whitelistAction());
            btnPromote.addActionListener(e -> promoteAction());
            btnDelete.addActionListener(e -> deleteAction());

            btnPanel.add(btnWhitelist);
            btnPanel.add(btnPromote);
            btnPanel.add(btnDelete);
        }

        btnPanel.add(btnRefresh);
        add(btnPanel, BorderLayout.SOUTH);

        btnRefresh.addActionListener(e -> refreshTable());
        refreshTable();
    }
    private JButton createStandardButton(String text) {
        JButton btn = new JButton(text);
        btn.setForeground(Color.BLACK);
        return btn;
    }

    private void whitelistAction() {
        String email = JOptionPane.showInputDialog(this, "Email à autoriser :");
        if (email != null && !email.trim().isEmpty()) {
            if (userDAO.isEmailWhitelisted(email)) {
                JOptionPane.showMessageDialog(this, "Déjà whitelisté.");
            } else {
                userDAO.whitelistEmail(email);
                refreshTable();
            }
        }
    }

    private void promoteAction() {
        int row = usersTable.getSelectedRow();
        if (row == -1) return;
        int uid = (int) tableModel.getValueAt(row, 0);
        String pseudo = (String) tableModel.getValueAt(row, 2);

        int confirm = JOptionPane.showConfirmDialog(this, "Promouvoir " + pseudo + " Admin ?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            userDAO.changeRole(uid, "ADMIN");
            refreshTable();
        }
    }

    private void deleteAction() {
        int row = usersTable.getSelectedRow();
        if (row == -1) return;
        int uid = (int) tableModel.getValueAt(row, 0);

        if (uid == currentUser.getId()) {
            JOptionPane.showMessageDialog(this, "Allez dans 'Mon Profil' pour supprimer votre compte.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Supprimer cet utilisateur ?", "Danger", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            userDAO.deleteUser(uid);
            refreshTable();
        }
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        List<User> users;
        if ("ADMIN".equals(currentUser.getRole())) {
            users = userDAO.getAllUsers();
        } else {
            users = userDAO.getColleagues(currentUser.getId());
        }
        for (User u : users) {
            tableModel.addRow(new Object[]{
                    u.getId(), u.getEmail(), u.getPseudo(), u.getRole(), u.isWhitelisted() ? "Actif" : "En attente"
            });
        }
    }
}