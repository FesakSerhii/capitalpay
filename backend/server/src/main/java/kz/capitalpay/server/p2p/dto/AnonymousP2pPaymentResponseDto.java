package kz.capitalpay.server.p2p.dto;

import kz.capitalpay.server.paysystems.dto.BillPaymentDto;

public class AnonymousP2pPaymentResponseDto {
    private String acsUrl;
    private String MD;
    private String PaReq;
    private BillPaymentDto billPaymentDto;
    private String paySystemCallbackUrl;

    public AnonymousP2pPaymentResponseDto(String acsUrl, String MD, String paReq, BillPaymentDto billPaymentDto, String paySystemCallbackUrl) {
        this.acsUrl = acsUrl;
        this.MD = MD;
        PaReq = paReq;
        this.billPaymentDto = billPaymentDto;
        this.paySystemCallbackUrl = paySystemCallbackUrl;
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

    public BillPaymentDto getBillPaymentDto() {
        return billPaymentDto;
    }

    public void setBillPaymentDto(BillPaymentDto billPaymentDto) {
        this.billPaymentDto = billPaymentDto;
    }

    public String getPaySystemCallbackUrl() {
        return paySystemCallbackUrl;
    }

    public void setPaySystemCallbackUrl(String paySystemCallbackUrl) {
        this.paySystemCallbackUrl = paySystemCallbackUrl;
    }
}
