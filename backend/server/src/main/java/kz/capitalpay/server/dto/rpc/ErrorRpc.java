package kz.capitalpay.server.dto.rpc;

public class ErrorRpc {
    Long code;
    String message;

    public ErrorRpc() {
    }

    public ErrorRpc(Long code, String message) {
        this.code = code;
        this.message = message;
    }

    public Long getCode() {
        return code;
    }

    public void setCode(Long code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
