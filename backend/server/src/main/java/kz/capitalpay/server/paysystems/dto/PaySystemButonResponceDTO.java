package kz.capitalpay.server.paysystems.dto;

import kz.capitalpay.server.paysystems.model.PaysystemInfo;

public class PaySystemButonResponceDTO {

    private PaysystemInfo paysystemInfo;
    private String paymentForm;

    public PaysystemInfo getPaysystemInfo() {
        return paysystemInfo;
    }

    public void setPaysystemInfo(PaysystemInfo paysystemInfo) {
        this.paysystemInfo = paysystemInfo;
    }

    public String getPaymentForm() {
        return paymentForm;
    }

    public void setPaymentForm(String paymentForm) {
        this.paymentForm = paymentForm;
    }
}
