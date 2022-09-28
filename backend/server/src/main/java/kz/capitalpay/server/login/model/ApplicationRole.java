package kz.capitalpay.server.login.model;

import org.springframework.security.core.GrantedAuthority;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class ApplicationRole implements GrantedAuthority {

    @Id
    @GeneratedValue
    private Long id;
    @Column(unique = true)
    private String authority;

    public ApplicationRole() {
    }


    public ApplicationRole(String authority) {
        this.authority = authority;
    }


    public ApplicationRole(Long id, String authority) {
        this.id = id;
        this.authority = authority;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }

    @Override
    public String getAuthority() {
        return this.authority;
    }
}
