package kz.capitalpay.server.dto;

public class ResultDTO {

    boolean result;
    Object data;
    int error;
    //TODO: временный костыль для тестирования двухфакторной аутентификации
    String sms;

    public ResultDTO(boolean result, Object data, int error) {
        this.result = result;
        this.data = data;
        this.error = error;
    }

    public ResultDTO(boolean result, Object data, int error, String sms) {
        this.result = result;
        this.data = data;
        this.error = error;
        this.sms = sms;
    }

    public String getSms() {
        return sms;
    }

    public void setSms(String sms) {
        this.sms = sms;
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

    @Override
    public String toString() {
        return "ResultDTO{" +
                "result=" + result +
                ", data=" + data +
                ", error=" + error +
                ", sms='" + sms + '\'' +
                '}';
    }
}
