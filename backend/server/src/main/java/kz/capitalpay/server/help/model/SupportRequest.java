package kz.capitalpay.server.help.model;

import kz.capitalpay.server.login.model.ApplicationRole;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

@Entity
public class SupportRequest {

    @Id
    @GeneratedValue
    Long id;

    Long authorId;

    String theme;
    String subject;
    @Column(length = 16383)
    String text;

    String fileIdList;

    String status;
    @Column(name = "important", columnDefinition = "boolean default false", nullable = false)
    boolean important;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAuthorId() {
        return authorId;
    }

    public void setAuthorId(Long authorId) {
        this.authorId = authorId;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getFileIdList() {
        return fileIdList;
    }

    public void setFileIdList(String fileIdList) {
        this.fileIdList = fileIdList;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isImportant() {
        return important;
    }

    public void setImportant(boolean important) {
        this.important = important;
    }
}
