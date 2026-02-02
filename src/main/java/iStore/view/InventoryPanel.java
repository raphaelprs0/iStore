package iStore.view;

import iStore.dao.ItemDAO;
import iStore.dao.StoreDAO;
import iStore.dao.impl.ItemDAOImpl;
import iStore.dao.impl.StoreDAOImpl;
import iStore.model.Item;
import iStore.model.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class InventoryPanel extends JPanel {
    private ItemDAO itemDAO;
    private JTable itemsTable;
    private DefaultTableModel tableModel;
    private int currentStoreId;
    private User currentUser;
    private JTextField nameField, priceField, qtyField;

    public InventoryPanel(User user, int storeId) {
        this.currentUser = user;
        this.currentStoreId = storeId;
        this.itemDAO = new ItemDAOImpl();

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 1. TABLEAU SIMPLE
        String[] columnNames = {"ID", "Nom Article", "Prix (€)", "Quantité"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        itemsTable = new JTable(tableModel);
        itemsTable.setRowHeight(25);
        add(new JScrollPane(itemsTable), BorderLayout.CENTER);

        // 2. BOUTONS
        JPanel actionPanel = new JPanel(new FlowLayout());

        JButton btnSell = createVisibleButton("Vendre (-1)");
        JButton btnRestock = createVisibleButton("Recevoir (+1)");
        JButton btnTeam = createVisibleButton("Voir l'équipe");

        btnSell.addActionListener(e -> updateSelectedStock(-1));
        btnRestock.addActionListener(e -> updateSelectedStock(1));
        btnTeam.addActionListener(e -> showTeamPopup());

        actionPanel.add(new JLabel("Actions :"));
        actionPanel.add(btnSell);
        actionPanel.add(btnRestock);
        actionPanel.add(new JSeparator(SwingConstants.VERTICAL));
        actionPanel.add(btnTeam);

        // 3. ZONE ADMIN
        if ("ADMIN".equals(currentUser.getRole())) {
            JPanel adminPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            adminPanel.setBorder(BorderFactory.createTitledBorder("Administration Stock"));

            nameField = new JTextField(10);
            priceField = new JTextField(5);
            qtyField = new JTextField(5);

            JButton btnAdd = createVisibleButton("Ajouter");
            JButton btnDelete = createVisibleButton("Supprimer");

            adminPanel.add(new JLabel("Nom:")); adminPanel.add(nameField);
            adminPanel.add(new JLabel("Prix:")); adminPanel.add(priceField);
            adminPanel.add(new JLabel("Qté:")); adminPanel.add(qtyField);
            adminPanel.add(btnAdd);
            adminPanel.add(Box.createHorizontalStrut(20));
            adminPanel.add(btnDelete);

            btnAdd.addActionListener(e -> createItem());
            btnDelete.addActionListener(e -> deleteSelectedItem());

            add(adminPanel, BorderLayout.NORTH);
        }

        add(actionPanel, BorderLayout.SOUTH);
        refreshTable();
    }

    private JButton createVisibleButton(String text) {
        JButton btn = new JButton(text);
        btn.setForeground(Color.BLACK);
        return btn;
    }

    // LOGIQUE MÉTIER

    private void refreshTable() {
        tableModel.setRowCount(0);
        List<Item> items = itemDAO.getItemsByStore(currentStoreId);
        for (Item item : items) {
            tableModel.addRow(new Object[]{item.getId(), item.getName(), item.getPrice(), item.getQuantity()});
        }
    }

    private void updateSelectedStock(int delta) {
        int selectedRow = itemsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Sélectionnez une ligne.");
            return;
        }
        int itemId = (int) tableModel.getValueAt(selectedRow, 0);
        int currentQty = (int) tableModel.getValueAt(selectedRow, 3);
        int newQty = currentQty + delta;

        if (newQty < 0) {
            JOptionPane.showMessageDialog(this, "Stock insuffisant !");
            return;
        }
        try {
            itemDAO.updateStock(itemId, newQty);
            tableModel.setValueAt(newQty, selectedRow, 3);
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void createItem() {
        try {
            String name = nameField.getText();
            double price = Double.parseDouble(priceField.getText());
            int qty = Integer.parseInt(qtyField.getText());

            if(name.isEmpty()) return;

            itemDAO.createItem(new Item(name, price, qty, currentStoreId));
            nameField.setText(""); priceField.setText(""); qtyField.setText("");
            refreshTable();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur de format (Prix/Qté).");
        }
    }

    private void deleteSelectedItem() {
        int selectedRow = itemsTable.getSelectedRow();
        if (selectedRow != -1) {
            int itemId = (int) tableModel.getValueAt(selectedRow, 0);
            if (JOptionPane.showConfirmDialog(this, "Supprimer ?", "Confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                itemDAO.deleteItem(itemId);
                refreshTable();
            }
        }
    }

    private void showTeamPopup() {
        StoreDAO storeDAO = new StoreDAOImpl();
        List<User> team = storeDAO.getEmployeesWithAccess(currentStoreId);
        StringBuilder sb = new StringBuilder("Équipe :\n");
        for (User u : team) sb.append("- ").append(u.getPseudo()).append("\n");
        if (team.isEmpty()) sb.append("Aucun (sauf admin)");
        JOptionPane.showMessageDialog(this, sb.toString());
    }
}