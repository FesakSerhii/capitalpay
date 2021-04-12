package kz.capitalpay.server.help.dto;

public class ChangeStatusSupportRequestDTO {
    Long requestId;
    Long status;

    public Long getRequestId() {
        return requestId;
    }

    public void setRequestId(Long requestId) {
        this.requestId = requestId;
    }

    public Long getStatus() {
        return status;
    }

    public void setStatus(Long status) {
        this.status = status;
    }
}
