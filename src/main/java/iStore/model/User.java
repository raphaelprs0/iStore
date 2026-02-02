package iStore.model;

public class User {
    private int id;
    private String email;
    private String pseudo;
    private String password;
    private String salt;
    private String role; // "ADMIN" ou "USER"
    private boolean isWhitelisted;

    // Constructeur vide (nécessaire pour JDBC)
    public User() {}

    // Constructeur complet
    public User(int id, String email, String pseudo, String password, String salt, String role, boolean isWhitelisted) {
        this.id = id;
        this.email = email;
        this.pseudo = pseudo;
        this.password = password;
        this.salt = salt;
        this.role = role;
        this.isWhitelisted = isWhitelisted;
    }

    // GETTERS ET SETTERS OBLIGATOIRES
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPseudo() { return pseudo; }
    public void setPseudo(String pseudo) { this.pseudo = pseudo; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getSalt() { return salt; }
    public void setSalt(String salt) { this.salt = salt; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public boolean isWhitelisted() { return isWhitelisted; }
    public void setWhitelisted(boolean whitelisted) { isWhitelisted = whitelisted; }
}