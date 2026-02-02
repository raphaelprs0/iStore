package iStore.model;

public class Item {
    private int id;
    private String name;
    private double price;
    private int quantity;
    private int storeId; // Lien vers le magasin

    public Item(int id, String name, double price, int quantity, int storeId) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.storeId = storeId;
    }

    // Constructeur sans ID pour la création (l'ID est auto-généré par la DB)
    public Item(String name, double price, int quantity, int storeId) {
        this(0, name, price, quantity, storeId);
    }

    // Validation métier : La quantité ne peut pas être négative
    public void setQuantity(int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("La quantité ne peut pas être inférieure à 0");
        }
        this.quantity = quantity;
    }

    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public double getPrice() { return price; }
    public int getQuantity() { return quantity; }
    public int getStoreId() { return storeId; }
}