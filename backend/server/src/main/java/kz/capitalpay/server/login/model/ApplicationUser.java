package kz.capitalpay.server.login.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;

@Entity
public class ApplicationUser implements Serializable, Cloneable {

    @Id
    @GeneratedValue
    Long id;
    String username;
    String password;
    String realname;
    String email;
    @Column(name = "active", columnDefinition = "boolean default false", nullable = false)
    boolean active;
    @Column(name = "blocked", columnDefinition = "boolean default false", nullable = false)
    boolean blocked;

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

    public String getRealname() {
        return realname;
    }

    public void setRealname(String realname) {
        this.realname = realname;
    }

    public Set<ApplicationRole> getRoles() {
        return roles;
    }

    public void setRoles(Set<ApplicationRole> roles) {
        this.roles = roles;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isBlocked() {
        return blocked;
    }

    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }
}
