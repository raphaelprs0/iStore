package iStore.view;

import iStore.dao.StoreDAO;
import iStore.dao.UserDAO;
import iStore.dao.impl.StoreDAOImpl;
import iStore.dao.impl.UserDAOImpl;
import iStore.model.Store;
import iStore.model.User;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class StoreManagementPanel extends JPanel {
    private StoreDAO storeDAO;
    private UserDAO userDAO;

    private DefaultListModel<Store> storesListModel;
    private JList<Store> storesList;

    private DefaultListModel<String> employeesListModel;
    private JList<String> employeesList;

    private JLabel selectedStoreLabel;
    private Store selectedStore;

    public StoreManagementPanel() {
        this.storeDAO = new StoreDAOImpl();
        this.userDAO = new UserDAOImpl();

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        //  PARTIE GAUCHE : LISTE DES MAGASINS
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBorder(BorderFactory.createTitledBorder("Magasins"));

        storesListModel = new DefaultListModel<>();
        storesList = new JList<>(storesListModel);
        storesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        storesList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) loadStoreDetails();
        });

        leftPanel.add(new JScrollPane(storesList), BorderLayout.CENTER);

        JButton btnAddStore = new JButton("Nouveau Magasin");
        btnAddStore.addActionListener(e -> createStoreAction());
        JButton btnDelStore = new JButton("Supprimer Magasin");
        btnDelStore.addActionListener(e -> deleteStoreAction());

        JPanel leftButtons = new JPanel(new GridLayout(1, 2));
        leftButtons.add(btnAddStore);
        leftButtons.add(btnDelStore);
        leftPanel.add(leftButtons, BorderLayout.SOUTH);

        //  PARTIE DROITE : DETAILS & ACCES
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBorder(BorderFactory.createTitledBorder("Accès Employés"));

        selectedStoreLabel = new JLabel("Sélectionnez un magasin...");
        selectedStoreLabel.setFont(new Font("Arial", Font.BOLD, 14));
        rightPanel.add(selectedStoreLabel, BorderLayout.NORTH);

        employeesListModel = new DefaultListModel<>();
        employeesList = new JList<>(employeesListModel);
        rightPanel.add(new JScrollPane(employeesList), BorderLayout.CENTER);

        JButton btnAddAccess = new JButton("Ajouter Employé (Email)");
        btnAddAccess.addActionListener(e -> addAccessAction());

        JButton btnRevokeAccess = new JButton("Retirer Accès");
        btnRevokeAccess.addActionListener(e -> revokeAccessAction());

        JPanel rightButtons = new JPanel(new GridLayout(1, 2));
        rightButtons.add(btnAddAccess);
        rightButtons.add(btnRevokeAccess);
        rightPanel.add(rightButtons, BorderLayout.SOUTH);

        // SPLIT PANE
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
        splitPane.setDividerLocation(300);
        add(splitPane, BorderLayout.CENTER);

        refreshStoresList();
    }

    //  ACTIONS

    private void createStoreAction() {
        String name = JOptionPane.showInputDialog(this, "Nom du nouveau magasin :");
        if (name != null && !name.trim().isEmpty()) {
            storeDAO.createStore(new Store(name));
            refreshStoresList();
        }
    }

    private void deleteStoreAction() {
        Store store = storesList.getSelectedValue();
        if (store == null) return;

        int confirm = JOptionPane.showConfirmDialog(this,
                "Supprimer " + store.getName() + " ?\nCela ne supprimera PAS l'inventaire (protection).",
                "Confirmation", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            storeDAO.deleteStore(store.getId());
            refreshStoresList();
            employeesListModel.clear();
            selectedStoreLabel.setText("Sélectionnez un magasin...");
        }
    }

    private void addAccessAction() {
        if (selectedStore == null) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un magasin d'abord.");
            return;
        }

        String email = JOptionPane.showInputDialog(this, "Email de l'employé à ajouter :");
        if (email == null || email.trim().isEmpty()) return;

        User user = userDAO.getUserByEmail(email);
        if (user == null) {
            JOptionPane.showMessageDialog(this, "Utilisateur introuvable.", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        storeDAO.grantAccess(user.getId(), selectedStore.getId());
        loadStoreDetails(); // Rafraichir la liste
        JOptionPane.showMessageDialog(this, "Accès accordé à " + user.getPseudo());
    }

    private void revokeAccessAction() {
        String selectedLine = employeesList.getSelectedValue();
        if (selectedLine == null) return;
        String email = selectedLine.substring(selectedLine.indexOf("(") + 1, selectedLine.indexOf(")"));
        User user = userDAO.getUserByEmail(email);

        if (user != null) {
            storeDAO.revokeAccess(user.getId(), selectedStore.getId());
            loadStoreDetails();
        }
    }

    // LOGIQUE D'AFFICHAGE

    private void refreshStoresList() {
        storesListModel.clear();
        List<Store> stores = storeDAO.getAllStores();
        for (Store s : stores) {
            storesListModel.addElement(s);
        }
    }

    private void loadStoreDetails() {
        selectedStore = storesList.getSelectedValue();
        if (selectedStore == null) return;

        selectedStoreLabel.setText("Magasin : " + selectedStore.getName());
        employeesListModel.clear();

        List<User> employees = storeDAO.getEmployeesWithAccess(selectedStore.getId());
        for (User u : employees) {
            employeesListModel.addElement(u.getPseudo() + " (" + u.getEmail() + ")");
        }
    }
}