package kz.capitalpay.server.login.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;

@Entity
public class ApplicationUser implements Serializable, Cloneable {

    @Id
    @GeneratedValue
    private Long id;
    private String username;
    private String password;
    private String realname;
    private String email;
    private Long timestamp;
    @Column(columnDefinition = "boolean default false")
    private boolean active;
    @Column(columnDefinition = "boolean default false")
    private boolean blocked;
    @Column(columnDefinition = "boolean default false")
    private boolean test;

    @ManyToMany(fetch = FetchType.EAGER)
    private Set<ApplicationRole> roles;

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
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

    public boolean isTest() {
        return test;
    }

    public void setTest(boolean test) {
        this.test = test;
    }
}
