package kz.capitalpay.server.login.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;

@Entity
public class ApplicationUser implements Serializable {

    @Id
    @GeneratedValue
    Long id;
    String username;
    String password;


    @ManyToMany(fetch = FetchType.EAGER)
    Set<ApplicationRole> roles;

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

    public Set<ApplicationRole> getRoles() {
        return roles;
    }

    public void setRoles(Set<ApplicationRole> roles) {
        this.roles = roles;
    }


}
