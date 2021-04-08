package kz.capitalpay.server.login.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;

@Entity
public class DeletedApplicationUser implements Serializable {

    @Id
    Long id;
    String username;
    String password;
    String email;

    public DeletedApplicationUser() {
    }

    public DeletedApplicationUser(ApplicationUser applicationUser) {
        this.id = applicationUser.getId();
        this.username = applicationUser.getUsername();
        this.password = applicationUser.getPassword();
        this.email = applicationUser.getEmail();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
