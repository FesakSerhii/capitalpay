package kz.capitalpay.server.help.dto;

import kz.capitalpay.server.files.model.FileStorage;

import java.util.List;

public class SupportAnswerDTO {
    Long Id;
    Long operatorId;
    String text;
    List<FileStorage> fileList;

    public Long getId() {
        return Id;
    }

    public void setId(Long id) {
        Id = id;
    }


    public Long getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Long operatorId) {
        this.operatorId = operatorId;
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
}
