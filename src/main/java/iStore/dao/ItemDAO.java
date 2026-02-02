package iStore.dao;

import iStore.model.Item;
import java.util.List;

public interface ItemDAO {
    void createItem(Item item);
    void deleteItem(int itemId);
    void updateStock(int itemId, int newQuantity); // Pour les employés
    List<Item> getItemsByStore(int storeId); // Pour l'affichage
}