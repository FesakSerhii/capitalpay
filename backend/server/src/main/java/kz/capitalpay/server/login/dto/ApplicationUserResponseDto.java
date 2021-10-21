package kz.capitalpay.server.login.dto;

import kz.capitalpay.server.login.model.ApplicationRole;
import kz.capitalpay.server.login.model.ApplicationUser;

import java.util.Set;

public class ApplicationUserResponseDto {

    Long id;
    String username;
    String realname;
    String email;
    Long timestamp;
    Status status;
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

    public String getRealname() {
        return realname;
    }

    public void setRealname(String realname) {
        this.realname = realname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Set<ApplicationRole> getRoles() {
        return roles;
    }

    public void setRoles(Set<ApplicationRole> roles) {
        this.roles = roles;
    }

    public enum Status {
        ACTIVE, BLOCKED, INACTIVE
    }

    public static Status getUserStatus(ApplicationUser user) {
        if (!user.isActive() && !user.isBlocked()) {
            return Status.INACTIVE;
        }
        if (!user.isActive() && user.isBlocked()) {
            return Status.BLOCKED;
        }
        return Status.ACTIVE;
    }

}
