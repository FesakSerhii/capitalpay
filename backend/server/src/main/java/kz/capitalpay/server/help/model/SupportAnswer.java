package kz.capitalpay.server.help.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class SupportAnswer {
    @Id @GeneratedValue
    Long Id;
    Long requestId;
    Long operatorId;
    String text;
    String fileIdList;

    public Long getId() {
        return Id;
    }

    public void setId(Long id) {
        Id = id;
    }

    public Long getRequestId() {
        return requestId;
    }

    public void setRequestId(Long requestId) {
        this.requestId = requestId;
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

    public String getFileIdList() {
        return fileIdList;
    }

    public void setFileIdList(String fileIdList) {
        this.fileIdList = fileIdList;
    }
}
