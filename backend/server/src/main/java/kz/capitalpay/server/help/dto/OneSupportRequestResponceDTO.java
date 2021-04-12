package kz.capitalpay.server.help.dto;

import kz.capitalpay.server.files.model.FileStorage;
import kz.capitalpay.server.help.model.SupportAnswer;
import kz.capitalpay.server.login.model.ApplicationUser;

import javax.persistence.Column;
import java.util.List;

public class OneSupportRequestResponceDTO {

    Long id;

    ApplicationUser author;

    String theme;
    String subject;

    String text;

    List<FileStorage> fileList;

    String status;

   List< SupportAnswer> supportAnswer;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ApplicationUser getAuthor() {
        return author;
    }

    public void setAuthor(ApplicationUser author) {
        this.author = author;
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

    public List<FileStorage> getFileList() {
        return fileList;
    }

    public void setFileList(List<FileStorage> fileList) {
        this.fileList = fileList;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<SupportAnswer> getSupportAnswer() {
        return supportAnswer;
    }

    public void setSupportAnswer(List<SupportAnswer> supportAnswer) {
        this.supportAnswer = supportAnswer;
    }
}
