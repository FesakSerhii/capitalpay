package kz.capitalpay.server.help.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class SupportRequest {

    @Id
    @GeneratedValue
    private Long id;
    private Long authorId;
    private Long timestamp;

    private String theme;
    private String subject;
    @Column(length = 16383)
    private String text;

    private String fileIdList;

    private String status;
    @Column(name = "important", columnDefinition = "boolean default false", nullable = false)
    boolean important;

    @Column(columnDefinition = "boolean default false")
    boolean emailMessageSent;

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

    public boolean isEmailMessageSent() {
        return emailMessageSent;
    }

    public void setEmailMessageSent(boolean emailMessageSent) {
        this.emailMessageSent = emailMessageSent;
    }
}
