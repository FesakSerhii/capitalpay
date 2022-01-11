package kz.capitalpay.server.usercard.dto;

public class CheckCardValidityResponse {

    private boolean valid;
    private String returnCode;

    public CheckCardValidityResponse(boolean valid, String returnCode) {
        this.valid = valid;
        this.returnCode = returnCode;
    }

    public String getReturnCode() {
        return returnCode;
    }

    public void setReturnCode(String returnCode) {
        this.returnCode = returnCode;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }
}
