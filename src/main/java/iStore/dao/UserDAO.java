package iStore.dao;

import iStore.model.User;
import java.util.List;

public interface UserDAO {
    void createUser(User user) throws Exception;
    User getUserByEmail(String email);
    void updateUser(User user);
    void deleteUser(int id);
    List<User> getAllUsers();
    boolean isEmailWhitelisted(String email);
    void whitelistEmail(String email);
    void changeRole(int userId, String newRole);

    // Récupère uniquement les utilisateurs qui partagent le même magasin
    List<User> getColleagues(int currentUserId);
}