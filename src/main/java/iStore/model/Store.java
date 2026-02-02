package iStore.model;

public class Store {
    private int id;
    private String name;

    public Store(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public Store(String name) {
        this(0, name);
    }

    public int getId() { return id; }
    public String getName() { return name; }

    @Override
    public String toString() {
        return name + " (ID: " + id + ")";
    }
}