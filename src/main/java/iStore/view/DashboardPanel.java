package iStore.view;

import iStore.dao.StoreDAO;
import iStore.dao.impl.StoreDAOImpl;
import iStore.model.Store;
import iStore.model.User;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class DashboardPanel extends JPanel {
    private MainFrame mainFrame;
    private User currentUser;
    private StoreDAO storeDAO;

    private JPanel contentPanel;
    private CardLayout cardLayout;
    private JPanel menuPanel;

    public DashboardPanel(MainFrame frame, User user) {
        this.mainFrame = frame;
        this.currentUser = user;
        this.storeDAO = new StoreDAOImpl();

        setLayout(new BorderLayout());

        // HEADER
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(45, 52, 54));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        JLabel titleLabel = new JLabel("iStore ltd - " + currentUser.getRole());
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));

        JLabel userLabel = new JLabel("Connecté : " + currentUser.getPseudo() + "  ");
        userLabel.setForeground(Color.LIGHT_GRAY);

        JButton logoutBtn = new JButton("Déconnexion");
        logoutBtn.addActionListener(e -> mainFrame.showView("LOGIN"));

        JPanel headerRight = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        headerRight.setOpaque(false);
        headerRight.add(userLabel);
        headerRight.add(logoutBtn);

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(headerRight, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // SIDEBAR
        menuPanel = new JPanel();
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setBackground(new Color(223, 230, 233));
        menuPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        menuPanel.setPreferredSize(new Dimension(200, 0));

        addButtonsBasedOnRole();
        add(menuPanel, BorderLayout.WEST);

        // CONTENT
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);

        // --- AJOUT DES VUES ---
        contentPanel.add(createHomeView(), "HOME");
        contentPanel.add(new UserManagementPanel(currentUser), "DIRECTORY");
        contentPanel.add(new ProfilePanel(mainFrame, currentUser), "PROFILE");
        if ("ADMIN".equals(currentUser.getRole())) {
            contentPanel.add(new StoreManagementPanel(), "MANAGE_STORES");
        }

        add(contentPanel, BorderLayout.CENTER);
    }

    private void addButtonsBasedOnRole() {
        addMenuLabel("NAVIGATION");
        addMenuButton("Mes Magasins", "HOME");
        addMenuButton("Annuaire Utilisateurs", "DIRECTORY");
        addMenuButton("Mon Profil", "PROFILE");

        if ("ADMIN".equals(currentUser.getRole())) {
            addMenuSeparator();
            addMenuLabel("ADMINISTRATION");
            addMenuButton("Gérer Magasins", "MANAGE_STORES");
        }
    }

    private JPanel createHomeView() {
        JPanel home = new JPanel(new BorderLayout());
        home.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        home.add(new JLabel("Sélectionnez un magasin :", SwingConstants.CENTER), BorderLayout.NORTH);

        List<Store> myStores;
        if ("ADMIN".equals(currentUser.getRole())) {
            myStores = storeDAO.getAllStores();
        } else {
            myStores = storeDAO.getStoresForUser(currentUser.getId());
        }

        JPanel storesGrid = new JPanel(new GridLayout(0, 3, 15, 15));
        for (Store store : myStores) {
            JButton storeBtn = new JButton(store.getName());
            storeBtn.setPreferredSize(new Dimension(150, 100));
            storeBtn.addActionListener(e -> openInventory(store));
            storesGrid.add(storeBtn);
        }
        if (myStores.isEmpty()) storesGrid.add(new JLabel("Aucun accès magasin."));

        home.add(new JScrollPane(storesGrid), BorderLayout.CENTER);
        return home;
    }

    private void openInventory(Store store) {
        InventoryPanel invPanel = new InventoryPanel(currentUser, store.getId());
        String viewName = "INV_" + store.getId();
        contentPanel.add(invPanel, viewName);
        cardLayout.show(contentPanel, viewName);
    }

    private void addMenuButton(String text, String viewName) {
        JButton btn = new JButton(text);
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setMaximumSize(new Dimension(180, 40));
        btn.addActionListener(e -> {
            if ("HOME".equals(viewName)) contentPanel.add(createHomeView(), "HOME");
            cardLayout.show(contentPanel, viewName);
        });
        menuPanel.add(btn);
        menuPanel.add(Box.createRigidArea(new Dimension(0, 5)));
    }

    private void addMenuLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(Color.GRAY);
        label.setFont(new Font("Arial", Font.BOLD, 10));
        label.setBorder(BorderFactory.createEmptyBorder(10, 2, 5, 0));
        menuPanel.add(label);
    }

    private void addMenuSeparator() {
        JSeparator sep = new JSeparator();
        sep.setMaximumSize(new Dimension(180, 5));
        menuPanel.add(sep);
    }
}