package kz.capitalpay.server.p2p.dto;

public class AnonymousP2pPaymentResponseDto {
    private String acsUrl;
    private String MD;
    private String PaReq;
    //    private BillPaymentDto billPaymentDto;
    private String paySystemCallbackUrl;
    private boolean is3ds;

    public AnonymousP2pPaymentResponseDto(String acsUrl, String MD, String paReq, String paySystemCallbackUrl, boolean is3ds) {
        this.acsUrl = acsUrl;
        this.MD = MD;
        PaReq = paReq;
        this.paySystemCallbackUrl = paySystemCallbackUrl;
        this.is3ds = is3ds;
    }

    public String getAcsUrl() {
        return acsUrl;
    }

    public void setAcsUrl(String acsUrl) {
        this.acsUrl = acsUrl;
    }

    public String getMD() {
        return MD;
    }

    public void setMD(String MD) {
        this.MD = MD;
    }

    public String getPaReq() {
        return PaReq;
    }

    public void setPaReq(String paReq) {
        PaReq = paReq;
    }

    public String getPaySystemCallbackUrl() {
        return paySystemCallbackUrl;
    }

    public void setPaySystemCallbackUrl(String paySystemCallbackUrl) {
        this.paySystemCallbackUrl = paySystemCallbackUrl;
    }

    public boolean isIs3ds() {
        return is3ds;
    }

    public void setIs3ds(boolean is3ds) {
        this.is3ds = is3ds;
    }
}
