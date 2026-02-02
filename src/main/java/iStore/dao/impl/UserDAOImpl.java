package iStore.dao.impl;

import iStore.config.DatabaseManager;
import iStore.dao.UserDAO;
import iStore.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAOImpl implements UserDAO {

    @Override
    public void createUser(User user) throws Exception {
        String updateSql = "UPDATE users SET pseudo=?, password=?, salt=?, role=?, is_whitelisted=? WHERE email=?";
        String insertSql = "INSERT INTO users(email, pseudo, password, salt, role, is_whitelisted) VALUES(?,?,?,?,?,?)";
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement pstmt = conn.prepareStatement(updateSql)) {
                pstmt.setString(1, user.getPseudo()); pstmt.setString(2, user.getPassword());
                pstmt.setString(3, user.getSalt()); pstmt.setString(4, user.getRole());
                pstmt.setBoolean(5, user.isWhitelisted()); pstmt.setString(6, user.getEmail());
                if (pstmt.executeUpdate() == 0) {
                    try (PreparedStatement insert = conn.prepareStatement(insertSql)) {
                        insert.setString(1, user.getEmail()); insert.setString(2, user.getPseudo());
                        insert.setString(3, user.getPassword()); insert.setString(4, user.getSalt());
                        insert.setString(5, user.getRole()); insert.setBoolean(6, user.isWhitelisted());
                        insert.executeUpdate();
                    }
                }
            }
        }
    }

    @Override
    public User getUserByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";
        try (Connection conn = DatabaseManager.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if(rs.next()) return mapResultSetToUser(rs);
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    @Override
    public void updateUser(User user) {
        String sql = "UPDATE users SET pseudo=?, password=?, salt=?, role=?, is_whitelisted=? WHERE id=?";
        try (Connection conn = DatabaseManager.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, user.getPseudo()); ps.setString(2, user.getPassword());
            ps.setString(3, user.getSalt()); ps.setString(4, user.getRole());
            ps.setBoolean(5, user.isWhitelisted()); ps.setInt(6, user.getId());
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    @Override
    public void deleteUser(int id) {
        String deleteAccess = "DELETE FROM store_access WHERE user_id = ?";
        String deleteUser = "DELETE FROM users WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(deleteAccess)) { ps.setInt(1, id); ps.executeUpdate(); }
            try (PreparedStatement ps = conn.prepareStatement(deleteUser)) { ps.setInt(1, id); ps.executeUpdate(); }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    @Override
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection(); Statement st = conn.createStatement(); ResultSet rs = st.executeQuery("SELECT * FROM users")) {
            while(rs.next()) users.add(mapResultSetToUser(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return users;
    }

    @Override
    public boolean isEmailWhitelisted(String email) {
        try (Connection conn = DatabaseManager.getConnection(); PreparedStatement ps = conn.prepareStatement("SELECT is_whitelisted FROM users WHERE email=?")) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if(rs.next()) return rs.getBoolean("is_whitelisted");
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    @Override
    public void whitelistEmail(String email) {
        String sql = "INSERT OR IGNORE INTO users(email, pseudo, password, salt, role, is_whitelisted) VALUES(?, 'En attente', 'PENDING', 'PENDING', 'USER', 1)";
        try (Connection conn = DatabaseManager.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email); ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    @Override
    public void changeRole(int userId, String newRole) {
        try (Connection conn = DatabaseManager.getConnection(); PreparedStatement ps = conn.prepareStatement("UPDATE users SET role=? WHERE id=?")) {
            ps.setString(1, newRole); ps.setInt(2, userId); ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    @Override
    public List<User> getColleagues(int currentUserId) {
        List<User> users = new ArrayList<>();
        String sql = "SELECT DISTINCT u.* FROM users u " +
                "JOIN store_access sa1 ON u.id = sa1.user_id " +
                "JOIN store_access sa2 ON sa1.store_id = sa2.store_id " +
                "WHERE sa2.user_id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, currentUserId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        return new User(rs.getInt("id"), rs.getString("email"), rs.getString("pseudo"),
                rs.getString("password"), rs.getString("salt"), rs.getString("role"), rs.getBoolean("is_whitelisted"));
    }
}