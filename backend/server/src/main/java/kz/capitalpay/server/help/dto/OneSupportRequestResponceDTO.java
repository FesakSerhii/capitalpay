package kz.capitalpay.server.help.dto;

import kz.capitalpay.server.files.model.FileStorage;
import kz.capitalpay.server.login.model.ApplicationUser;

import java.util.List;

public class OneSupportRequestResponceDTO {

    private Long id;

    private ApplicationUser author;

    private String theme;
    private String subject;

    private String text;

    private List<FileStorage> fileList;

    private String status;

    private List<SupportAnswerDTO> supportAnswer;

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

    public List<SupportAnswerDTO> getSupportAnswer() {
        return supportAnswer;
    }

    public void setSupportAnswer(List<SupportAnswerDTO> supportAnswer) {
        this.supportAnswer = supportAnswer;
    }
}
