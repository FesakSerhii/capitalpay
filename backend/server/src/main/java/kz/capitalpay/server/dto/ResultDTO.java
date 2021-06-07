package kz.capitalpay.server.dto;

public class ResultDTO {

    boolean result;
    Object data;
    int error;

    public ResultDTO(boolean result, Object data, int error) {
        this.result = result;
        this.data = data;
        this.error = error;
    }

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public int getError() {
        return error;
    }

    public void setError(int error) {
        this.error = error;
    }
}
