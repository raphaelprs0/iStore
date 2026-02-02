package iStore.dao.impl;

import iStore.config.DatabaseManager;
import iStore.dao.StoreDAO;
import iStore.model.Store;
import iStore.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StoreDAOImpl implements StoreDAO {

    @Override
    public void createStore(Store store) {
        String sql = "INSERT INTO stores(name) VALUES(?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, store.getName());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteStore(int id) {
        String sql = "DELETE FROM stores WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Store> getAllStores() {
        List<Store> list = new ArrayList<>();
        String sql = "SELECT * FROM stores";
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Store(rs.getInt("id"), rs.getString("name")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public void grantAccess(int userId, int storeId) {
        String sql = "INSERT OR IGNORE INTO store_access(user_id, store_id) VALUES(?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setInt(2, storeId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void revokeAccess(int userId, int storeId) {
        String sql = "DELETE FROM store_access WHERE user_id = ? AND store_id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setInt(2, storeId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<User> getEmployeesWithAccess(int storeId) {
        List<User> users = new ArrayList<>();
        String sql = "SELECT u.* FROM users u " +
                "JOIN store_access sa ON u.id = sa.user_id " +
                "WHERE sa.store_id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, storeId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                // On reconstruit l'objet User
                User u = new User();
                u.setId(rs.getInt("id"));
                u.setEmail(rs.getString("email"));
                u.setPseudo(rs.getString("pseudo"));
                u.setRole(rs.getString("role"));
                users.add(u);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    @Override
    public List<Store> getStoresForUser(int userId) {
        List<Store> stores = new ArrayList<>();
        String sql = "SELECT s.* FROM stores s " +
                "JOIN store_access sa ON s.id = sa.store_id " +
                "WHERE sa.user_id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                stores.add(new Store(rs.getInt("id"), rs.getString("name")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return stores;
    }
}