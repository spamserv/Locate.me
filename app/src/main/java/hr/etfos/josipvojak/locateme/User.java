package hr.etfos.josipvojak.locateme;

import java.util.Date;

/**
 * Created by jvojak on 26.6.2016..
 */
public class User {
    private String email;
    private Date last_online;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getLast_online() {
        return last_online;
    }

    public void setLast_online(Date last_online) {
        this.last_online = last_online;
    }

    public User(String email) {
        this.email = email;
    }

    public User(String email, Date last_online) {
        this.email = email;
        this.last_online = last_online;
    }

    @Override
    public String toString() {
        return "User's email" + email + ",\n Last online=" + last_online;
    }
}
