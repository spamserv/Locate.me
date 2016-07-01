package hr.etfos.josipvojak.locateme;


/**
 * Created by jvojak on 26.6.2016..
 */
public class User {
    private String email, username, status;

    public String getUsername() {
        return username;
    }

    public String getStatus() {
        return status;
    }

    private String last_online;

    public User(String email, String username, String status, String last_online) {
        this.email = email;
        this.last_online = last_online;
        this.username = username;
        this.status = status;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLast_online() {
        return last_online;
    }

    public void setLast_online(String last_online) {
        this.last_online = last_online;
    }

    public User(String email) {
        this.email = email;
    }

    public User(String email, String last_online) {
        this.email = email;
        this.last_online = last_online;
    }

    @Override
    public String toString() {
        return "User's email" + email + ",\n Last online=" + last_online;
    }
}
