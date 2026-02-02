package iStore.view;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainPanel;

    public MainFrame() {
        setTitle("iStore Ltd - Management System");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        mainPanel.add(new LoginPanel(this), "LOGIN");

        add(mainPanel);

        // Affiche l'écran de login au démarrage
        showView("LOGIN");
    }

    public void showView(String viewName) {
        cardLayout.show(mainPanel, viewName);
    }
    public JPanel getMainPanel() {
        return mainPanel;
    }

}