package kz.capitalpay.server.help.dto;

import java.util.List;

public class SendSupportAnswerDTO {
    Long requestId;
    String text;
    List<Long> fileList;

    public Long getRequestId() {
        return requestId;
    }

    public void setRequestId(Long requestId) {
        this.requestId = requestId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<Long> getFileList() {
        return fileList;
    }

    public void setFileList(List<Long> fileList) {
        this.fileList = fileList;
    }
}
