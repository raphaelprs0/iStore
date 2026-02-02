package iStore.dao;

import iStore.model.Store;
import iStore.model.User;
import java.util.List;

public interface StoreDAO {
    void createStore(Store store);
    void deleteStore(int id);
    List<Store> getAllStores();

    // Gestion des accès (Many-to-Many)
    void grantAccess(int userId, int storeId);
    void revokeAccess(int userId, int storeId);
    List<User> getEmployeesWithAccess(int storeId); // Pour l'Admin
    List<Store> getStoresForUser(int userId); // Pour savoir ce que voit l'employé
}